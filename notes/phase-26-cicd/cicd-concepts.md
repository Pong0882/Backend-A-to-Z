# CI/CD 개념 정리

## CI vs CD 차이

```
CI (Continuous Integration) — 지속적 통합
  코드를 push할 때마다 자동으로 빌드 + 테스트
  "내 코드가 기존 코드와 잘 합쳐지는가"를 자동으로 검증

CD (Continuous Delivery) — 지속적 전달
  CI를 통과하면 배포 가능한 상태(artifact)를 자동으로 만들어둠
  실제 배포는 사람이 버튼 클릭

CD (Continuous Deployment) — 지속적 배포
  CI를 통과하면 자동으로 프로덕션까지 배포
  사람이 개입하지 않음
```

실무에서 "CI/CD"라고 하면 대부분 **CI + Continuous Deployment**를 합친 의미로 씀.

---

## 왜 필요한가

**CI/CD 없을 때:**
```
개발자 A → 코드 작성 → 수동 빌드 → 수동 테스트 → 수동 서버 접속 → 수동 배포
  └ 실수 가능 / 느림 / 배포할 때마다 긴장
```

**CI/CD 있을 때:**
```
개발자 A → PR 올림 → 자동 빌드+테스트 → main 머지 → 자동 배포
  └ 빠름 / 일관성 / 실수 감소
```

---

## 배포 전략 비교

### Recreate (현재 pong-to-rich 방식)
```
구버전 컨테이너 종료 → 신버전 컨테이너 시작
```
- 장점: 단순
- 단점: 종료~시작 사이 **다운타임 발생**
- 적합: 개발/학습 환경, 트래픽 적은 서비스

### Rolling Update
```
인스턴스 1개씩 순차 교체
[v1][v1][v1] → [v2][v1][v1] → [v2][v2][v1] → [v2][v2][v2]
```
- 장점: 다운타임 없음
- 단점: 교체 중 v1/v2 혼재
- 적합: k8s 기본 배포 방식

### Blue-Green
```
Blue(현재 운영) ←→ Green(신버전 대기)
트래픽을 Green으로 한 번에 전환
```
- 장점: 즉시 롤백 가능 (Blue로 다시 전환)
- 단점: 서버 2배 필요
- 적합: 무중단 + 빠른 롤백이 중요한 서비스

### Canary
```
신버전을 일부 트래픽(10%)에만 먼저 배포
문제 없으면 점진적으로 100%로 확대
```
- 장점: 실제 트래픽으로 검증 가능
- 단점: 설정 복잡
- 적합: 대규모 서비스, A/B 테스트

---

## Push 방식 vs Pull 방식 (GitOps)

### Push 방식 (현재 pong-to-rich)
```
GitHub Actions → SSH or Runner → 서버에 직접 배포 명령
```
- CI가 서버에 대한 접근 권한을 가짐
- 구성 단순, 작은 프로젝트에 적합

### Pull 방식 (GitOps — ArgoCD)
```
ArgoCD가 Git 상태를 주기적으로 감지
Git에 변경 감지 → ArgoCD가 k8s에 자동 적용
```
- Git이 유일한 진실의 소스 (Source of Truth)
- CI는 빌드/테스트만, CD는 ArgoCD가 담당
- k8s 환경에서 표준

---

## pong-to-rich에서 사용된 곳

### `.github/workflows/deploy.yml`

```yaml
on:
  push:
    branches: [ main ]
    paths:
      - 'pong-to-rich/**'

jobs:
  build:
    runs-on: ubuntu-latest   # GitHub 서버에서 실행 (CI)
    steps:
      - ./gradlew build -x test

  deploy:
    runs-on: self-hosted     # VM Runner에서 실행 (CD)
    needs: build
    steps:
      - git pull origin main
      - docker compose up --build -d
      - 헬스체크 (/actuator/health)
```

**현재 방식:** Push 방식 + Recreate 전략
- docker compose up --build 시 기존 컨테이너 종료 후 재시작 → 수초 다운타임 발생
- 추후 Blue-Green 또는 Rolling으로 개선 예정 (Phase 29)
