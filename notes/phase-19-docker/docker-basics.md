# Docker 기초

## Docker란

- 애플리케이션을 **컨테이너** 단위로 패키징해서 실행하는 플랫폼
- "내 PC에서는 되는데 서버에서는 안 돼" 문제를 해결
- 컨테이너 안에 앱 실행에 필요한 것(OS 라이브러리, 런타임, 설정)을 전부 담아서 어디서든 동일하게 실행

## VM vs 컨테이너

| | VM | 컨테이너 |
|---|---|---|
| 격리 수준 | OS 수준 (독립된 커널) | 프로세스 수준 (커널 공유) |
| 부팅 시간 | 수십 초 ~ 수 분 | 수 초 이내 |
| 용량 | GB 단위 | MB 단위 |
| 용도 | 완전한 서버 환경 | 앱 패키징/배포 |

> 지금 구조: **멀티패스 VM** (완전한 Linux 서버) 안에서 **Docker 컨테이너** (앱 실행) 를 사용

## 핵심 개념

### 이미지 (Image)
- 컨테이너 실행에 필요한 파일 + 설정의 **스냅샷**
- 읽기 전용. 이미지 자체는 변경 안 됨
- Dockerfile로 빌드하거나 Docker Hub에서 pull

### 컨테이너 (Container)
- 이미지를 실행한 **인스턴스**
- 이미지 위에 쓰기 레이어가 추가된 것
- 컨테이너를 삭제하면 그 안에서 생긴 데이터는 사라짐 → 볼륨으로 해결

### Dockerfile
- 이미지를 만드는 **레시피**
- 어떤 베이스 이미지를 쓸지, 어떤 파일을 복사할지, 어떤 명령어를 실행할지 정의

### 멀티 스테이지 빌드
- Dockerfile 안에서 빌드 단계와 실행 단계를 분리
- 빌드 도구(JDK, Gradle 등)는 최종 이미지에 포함하지 않아서 이미지 경량화 가능

```dockerfile
# 1단계 — 빌드
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

# 2단계 — 실행 (JRE만 포함, JDK 불필요)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose
- 여러 컨테이너를 한번에 정의하고 실행하는 도구
- `docker-compose.yml` 파일 하나로 Spring Boot + MySQL + Redis 등 전체 스택 관리

**V1 vs V2**

| | V1 | V2 |
|---|---|---|
| 명령어 | `docker-compose` (하이픈) | `docker compose` (띄어쓰기) |
| 구현 | Python 별도 설치 | Go, Docker 엔진 플러그인으로 내장 |
| 상태 | Deprecated | 현재 표준 |

> `docker.io` 설치 시 V2 포함. `docker compose` 명령어 사용

## 왜 Docker로 Spring Boot를 실행하는가

1. **환경 일관성** — 로컬 / VM / AWS 어디서든 동일하게 실행
2. **CI/CD 연동** — 이미지 빌드 → 레지스트리 push → 서버 pull & 실행 흐름이 자연스러움
3. **블루그린 배포** — 새 이미지로 새 컨테이너 띄우고 트래픽 전환, 문제 시 이전 컨테이너로 즉시 롤백
4. **의존성 격리** — Java 버전, 라이브러리 충돌 걱정 없음

## 주요 명령어

```bash
# 이미지 빌드
docker build -t <이미지명>:<태그> .

# 이미지 목록
docker images

# 컨테이너 실행
docker run -d -p <호스트포트>:<컨테이너포트> --name <컨테이너명> <이미지명>

# 실행 중인 컨테이너 목록
docker ps

# 컨테이너 로그
docker logs <컨테이너명>

# 컨테이너 중지 / 삭제
docker stop <컨테이너명>
docker rm <컨테이너명>

# Docker Compose 실행
docker compose up -d
docker compose down
```
