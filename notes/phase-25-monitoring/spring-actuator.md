# Spring Boot Actuator 정리

## 개념

Spring Boot 애플리케이션의 **내부 상태를 HTTP 엔드포인트로 노출**하는 라이브러리.
별도 코드 없이 의존성 추가만으로 헬스체크, 메트릭, 환경 정보 등을 제공한다.

```groovy
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

---

## 주요 엔드포인트

| 엔드포인트 | 설명 | 기본 노출 |
|-----------|------|----------|
| `/actuator/health` | 애플리케이션 + 의존성 상태 (DB, Redis 등) | O |
| `/actuator/info` | 앱 버전, 빌드 정보 등 | O |
| `/actuator/metrics` | JVM, HTTP, DB 커넥션 등 수치 지표 | X (활성화 필요) |
| `/actuator/prometheus` | Prometheus 포맷 메트릭 | X (micrometer 추가 필요) |
| `/actuator/env` | 환경변수, 설정값 전체 | X |
| `/actuator/loggers` | 로그 레벨 런타임 변경 | X |
| `/actuator/threaddump` | 스레드 덤프 | X |
| `/actuator/heapdump` | 힙 덤프 | X |

기본적으로 `health`와 `info`만 외부에 노출됨. 나머지는 `application.yml`에서 명시적으로 활성화해야 한다.

---

## /actuator/health 상세

### 응답 구조

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": { "database": "MySQL", "validationQuery": "isValid()" }
    },
    "diskSpace": {
      "status": "UP",
      "details": { "total": 21474836480, "free": 15000000000 }
    }
  }
}
```

`status`는 3가지:
- `UP` — 정상
- `DOWN` — 문제 있음
- `OUT_OF_SERVICE` — 점검 중

DB가 연결되면 `db` 컴포넌트가 자동으로 포함됨. Redis, Kafka 등 추가하면 해당 컴포넌트도 자동으로 포함.

### 상세 정보 노출 설정

기본은 `status`만 보임. 상세 정보를 보려면 `application.yml`에 추가:

```yaml
management:
  endpoint:
    health:
      show-details: always   # always / never / when-authorized
```

---

## Security와의 관계

Actuator 엔드포인트는 Spring Security에 의해 막힌다.
CI/CD 헬스체크나 모니터링 도구가 인증 없이 접근해야 하므로 `permitAll` 처리 필요.

```java
// SecurityConfig.java
.requestMatchers("/actuator/health").permitAll()
```

`/actuator/**` 전체를 permitAll하면 `env`, `heapdump` 같은 민감한 엔드포인트도 노출됨.
**`/actuator/health`만 열고 나머지는 막는 게 기본 원칙.**

---

## 엔드포인트 활성화/비활성화

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics     # 이것들만 외부 노출
        # include: "*"                     # 전체 노출 (개발환경에서만)
  endpoint:
    health:
      show-details: always
```

---

## Micrometer 연동 (Phase 25 예정)

Actuator + Micrometer + Prometheus를 연결하면 JVM 메트릭을 Grafana 대시보드로 시각화할 수 있다.

```
Spring Boot (Actuator + Micrometer)
    → /actuator/prometheus 엔드포인트 노출
        → Prometheus가 주기적으로 수집 (scrape)
            → Grafana에서 시각화
```

추가 의존성:
```groovy
implementation 'io.micrometer:micrometer-registry-prometheus'
```

수집되는 주요 메트릭:
- JVM: 힙 사용률, GC 횟수/시간, 스레드 수
- HTTP: 요청 수, 응답시간 P50/P95/P99, 에러율
- DB: HikariCP 커넥션 풀 사용률, 대기 시간
- 시스템: CPU, 메모리, 디스크

---

## pong-to-rich에서 사용된 곳

### 현재 용도 — CI/CD 헬스체크

`deploy.yml`에서 배포 후 애플리케이션이 정상 기동했는지 확인:

```bash
# 10초 간격으로 최대 2분 대기
curl http://localhost:8080/actuator/health
# 응답: { "status": "UP" } → 배포 성공
```

DB 컨테이너 기동 순서 때문에 Spring이 DB 연결에 실패하면 `status: DOWN` 반환.
이 경우 `docker logs pong-to-rich-app`으로 원인 확인.

### 추후 용도 (Phase 25)

```yaml
# application-prod.yml에 추가 예정
management:
  endpoints:
    web:
      exposure:
        include: health, prometheus
  endpoint:
    health:
      show-details: always
```

Prometheus + Grafana 연동으로 JVM/DB/HTTP 메트릭 시각화 예정.
