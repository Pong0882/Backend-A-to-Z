# Spring 프로파일 (Spring Profiles)

## 개념

환경(로컬, 개발, 운영)마다 다른 설정값을 분리해서 관리하는 기능.
`application.yaml` 에 공통 설정을 두고, 환경별 설정은 `application-{profile}.yml` 로 분리한다.

## 파일 구조

```
resources/
├── application.yaml          ← 공통 설정 + 활성 프로파일 지정
├── application-local.yml     ← 로컬 개발 환경 설정 (git 제외)
└── application-prod.yml      ← 운영 환경 설정
```

## 활성 프로파일 지정

```yaml
# application.yaml
spring:
  profiles:
    active: ${SPRING_PROFILE:local}
```

`${SPRING_PROFILE:local}` — 환경변수 `SPRING_PROFILE` 이 있으면 그 값을 쓰고, 없으면 `local` 을 기본값으로 사용한다.

| 환경 | 환경변수 설정 | 활성 프로파일 |
|------|-------------|------------|
| 로컬 개발 | 없음 | `local` → `application-local.yml` 로드 |
| 운영 서버 | `SPRING_PROFILE=prod` | `prod` → `application-prod.yml` 로드 |

## pong-to-rich 설정

**application-local.yml** (git 제외 — `.gitignore` 등록)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pong_to_rich
    username: pong
    password: 실제값
```

**application-prod.yml** (git 포함 — 환경변수 플레이스홀더만)
```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/${MYSQL_DATABASE}
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}

kis:
  app-key: ${KIS_APP_KEY}
  app-secret: ${KIS_APP_SECRET}
  base-url: ${KIS_BASE_URL}
```

`application-prod.yml` 에는 실제 값 대신 환경변수 참조만 있어서 git에 올려도 안전하다.
실제 값은 VM의 `.env` 파일에서 관리하고, `docker-compose.yml` 이 컨테이너에 주입한다.

## 환경변수 기본값 문법

```yaml
${변수명:기본값}
```

| 표현 | 의미 |
|------|------|
| `${SPRING_PROFILE:local}` | 환경변수 없으면 `local` |
| `${SERVER_PORT:8080}` | 환경변수 없으면 `8080` |
| `${REQUIRED_VAR}` | 환경변수 없으면 시작 실패 |

## 주의사항

- `application-local.yml` 은 반드시 `.gitignore` 에 추가 (실제 비밀번호 포함)
- `application-prod.yml` 은 환경변수 플레이스홀더만 사용하면 git에 올려도 됨
- 운영 서버에서 환경변수가 하나라도 빠지면 앱 시작 실패
