# GitHub Actions 정리

## 개념

GitHub이 제공하는 CI/CD 플랫폼. `.github/workflows/*.yml` 파일을 push하면 자동으로 파이프라인이 등록된다.

---

## 핵심 구성 요소

### Workflow
`.github/workflows/` 안의 YAML 파일 하나 = Workflow 하나.
언제(on), 어디서(runs-on), 무엇을(steps) 실행할지 정의한다.

### Trigger (on)
워크플로우가 실행되는 조건.

```yaml
on:
  push:
    branches: [ main ]          # main에 push될 때
    paths:
      - 'pong-to-rich/**'       # 이 경로 파일이 변경됐을 때만

  pull_request:
    branches: [ main ]          # main 대상 PR이 열릴 때

  workflow_dispatch:            # 수동 실행 버튼
```

`paths` 필터가 중요하다. 없으면 notes, devlog 등 문서만 바꿔도 매번 배포 트리거됨.

### Job
워크플로우 안의 독립 실행 단위. 기본적으로 병렬 실행, `needs`로 순서 지정 가능.

```yaml
jobs:
  build:
    runs-on: ubuntu-latest    # GitHub 호스팅 서버에서 실행

  deploy:
    runs-on: self-hosted      # 직접 등록한 서버(VM)에서 실행
    needs: build              # build 성공 후에만 실행
```

### Step
Job 안의 명령 하나하나. `uses`(액션 사용) 또는 `run`(shell 명령) 두 가지.

```yaml
steps:
  - name: 코드 체크아웃
    uses: actions/checkout@v4       # GitHub 공식 액션

  - name: 빌드
    working-directory: pong-to-rich/backend
    run: ./gradlew build -x test    # 직접 shell 명령
```

---

## runs-on 차이

| 값 | 실행 환경 | 비용 |
|----|----------|------|
| `ubuntu-latest` | GitHub 소유 서버 (매 실행마다 새 환경) | 월 2,000분 무료 |
| `self-hosted` | 직접 등록한 서버 (VM, EC2 등) | 무료 (서버 비용만) |

**GitHub 호스팅 서버의 특징:**
- 매 실행마다 완전히 새로운 환경 (컨테이너)
- 실행 끝나면 환경 삭제 → 다음 실행에 아무것도 남아있지 않음
- 그래서 Gradle 캐시를 `actions/cache`로 별도 저장해야 함

**Self-hosted Runner의 특징:**
- 내 서버가 직접 job을 실행
- 환경이 유지됨 (docker, git 등 이미 설치된 상태)
- GitHub에서 내 서버로 SSH 접속하는 게 아님 — Runner가 GitHub에 polling해서 job을 가져옴

---

## Self-hosted Runner 동작 원리

```
GitHub Actions
    ↑  polling (Runner가 주기적으로 "job 있어?" 확인)
VM의 Runner 프로세스
    ↓  job 있으면 가져와서 직접 실행
VM 로컬 환경 (docker, git 등)
```

Runner가 GitHub을 향해 연결하는 구조라 **VM에 공인 IP가 없어도 동작**.
로컬 Multipass VM에서도 작동하는 이유가 이것.

### Runner 설치 과정 요약
```bash
# 1. GitHub에서 토큰 발급
#    레포 → Settings → Actions → Runners → New self-hosted runner

# 2. VM에서 runner 설치 및 등록
./config.sh --url https://github.com/{계정}/{레포} --token {토큰}

# 3. systemd 서비스 등록 (VM 재시작해도 자동 실행)
sudo ./svc.sh install
sudo ./svc.sh start
```

---

## 주요 공식 액션

| 액션 | 역할 |
|------|------|
| `actions/checkout@v4` | 레포 코드를 runner에 체크아웃 |
| `actions/setup-java@v4` | JDK 설치 |
| `actions/cache@v4` | 파일/폴더 캐시 (Gradle, npm 등) |
| `actions/upload-artifact@v4` | 빌드 결과물 저장 |
| `docker/build-push-action@v5` | Docker 이미지 빌드 & 푸시 |

---

## Gradle 캐시가 필요한 이유

```yaml
- uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: gradle-${{ hashFiles('pong-to-rich/backend/build.gradle') }}
```

GitHub 호스팅 서버는 매번 새 환경 → Gradle이 의존성을 매번 다시 다운로드.
`build.gradle`이 바뀌지 않으면 캐시된 의존성을 재사용 → 빌드 시간 단축.

`key`에 `hashFiles`를 쓰는 이유 — `build.gradle`이 바뀌면 해시값이 달라져서 캐시 무효화, 바뀌지 않으면 캐시 재사용.

---

## pong-to-rich에서 사용된 곳

### `.github/workflows/deploy.yml` 전체 흐름

```
[트리거] main 브랜치 push + pong-to-rich/** 파일 변경

[build job — GitHub 서버]
  1. checkout
  2. JDK 17 설치
  3. Gradle 캐시 복원
  4. ./gradlew build -x test

[deploy job — pong-server VM Runner]
  1. git pull origin main
  2. docker compose up --build -d
  3. /actuator/health 폴링 (10초 간격, 최대 2분)
```

**테스트를 `-x test`로 스킵하는 이유:**
현재 테스트 코드가 DB 연결을 필요로 하는데 GitHub 서버에 DB가 없음.
추후 TestContainers 도입 시 `-x test` 제거 예정.
