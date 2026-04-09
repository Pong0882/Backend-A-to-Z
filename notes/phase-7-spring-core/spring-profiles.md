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

## 민감한 설정값 관리 전략 (JWT 시크릿 예시)

JWT 시크릿처럼 보안이 중요한 값은 어디에 어떻게 두느냐가 핵심이다.

```
application.yaml       → ${JWT_SECRET:더미값}   ← git 커밋됨. 더미값은 실제로 쓰이지 않음
application-local.yml  → jwt.secret: 실제시크릿 ← gitignored. 로컬에서 오버라이드
application-prod.yml   → ${JWT_SECRET}          ← git 커밋됨. env var 없으면 시작 실패
```

**왜 application.yaml에 더미 기본값을 두는가:**
- `-local.yml`이 항상 오버라이드하므로 더미값이 실제로 쓰이는 경우가 없음
- `-local.yml` 없이 서버를 실행하면 더미값으로 뜨긴 하지만, 그 값으로 서명된 토큰이 없으므로 실질적 위험 없음
- 기본값을 아예 없애면(`${JWT_SECRET}`) 로컬에서도 반드시 env var 또는 `-local.yml`이 있어야 함 — 더 엄격한 방식

**핵심 원칙:**
- 실제 시크릿은 절대 git에 올라가면 안 됨
- 로컬: `application-local.yml` (gitignored)
- 운영: VM의 `.env` 파일 → `docker-compose.yml`이 컨테이너에 주입

## 주의사항

- `application-local.yml` 은 반드시 `.gitignore` 에 추가 (실제 비밀번호, 시크릿 포함)
- `application-prod.yml` 은 환경변수 플레이스홀더만 사용하면 git에 올려도 됨
- 운영 서버에서 환경변수가 하나라도 빠지면 앱 시작 실패
