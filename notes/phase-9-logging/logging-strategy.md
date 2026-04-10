# 로깅 전략

## SLF4J와 Logback

Spring Boot는 기본적으로 **SLF4J + Logback** 조합을 사용한다.

| 역할 | 라이브러리 |
|------|----------|
| 로깅 인터페이스 | SLF4J (Simple Logging Facade for Java) |
| 실제 구현체 | Logback |

SLF4J는 인터페이스다. 코드에서는 SLF4J API만 쓰고, 실제 로그를 어디에 어떻게 쓸지는 Logback이 처리한다. Spring Boot에 별도 의존성 추가 없이 바로 사용 가능하다.

---

## @Slf4j

Lombok이 제공하는 어노테이션. 클래스에 붙이면 `log` 변수를 자동으로 생성한다.

```java
@Slf4j
@Service
public class AuthService {
    public void login(...) {
        log.info("로그인 시도: {}", email);   // {} 자리에 변수 삽입
        log.warn("비밀번호 불일치: {}", email);
        log.debug("토큰 발급 완료");
    }
}
```

`{}` 플레이스홀더를 쓰는 이유: 문자열 연결(`"로그인: " + email`)보다 성능이 좋다. 해당 로그 레벨이 비활성화되어 있으면 문자열 생성 자체를 건너뛴다.

---

## 로그 레벨

| 레벨 | 용도 |
|------|------|
| `TRACE` | 가장 상세. 거의 안 씀 |
| `DEBUG` | 개발 중 내부 동작 추적 (Refresh Token 신규/갱신 여부 등) |
| `INFO` | 주요 비즈니스 흐름 (로그인 시도/완료, 회원가입 완료) |
| `WARN` | 비정상 상황이지만 서비스는 계속 (이메일 중복, 비밀번호 불일치, 토큰 검증 실패) |
| `ERROR` | 심각한 오류, 즉시 대응 필요 |

레벨을 INFO로 설정하면 INFO + WARN + ERROR만 출력된다. DEBUG, TRACE는 출력 안 됨.

### 레벨 선택 기준

```
정상 흐름의 주요 이벤트          → INFO
예상 가능한 비정상 상황           → WARN
예상 못한 시스템 오류             → ERROR
개발 중 상세 추적 (운영엔 필요 없음) → DEBUG
```

---

## logback-spring.xml 환경별 설정

`src/main/resources/logback-spring.xml`에 환경별 로그 레벨을 분리한다.

```xml
<configuration>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{36}) - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 로컬: 우리 코드는 DEBUG, 나머지는 INFO -->
    <springProfile name="local">
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
        <logger name="com.pongtorich" level="DEBUG"/>
        <logger name="org.hibernate.SQL" level="OFF"/>
    </springProfile>

    <!-- prod: WARN 이상만 -->
    <springProfile name="prod">
        <root level="WARN">
            <appender-ref ref="CONSOLE"/>
        </root>
        <logger name="com.pongtorich" level="WARN"/>
    </springProfile>

</configuration>
```

**`<springProfile name="local">`** — `application.yaml`의 활성 프로파일이 `local`일 때만 적용.

**`<logger name="com.pongtorich" level="DEBUG">`** — 우리 패키지만 DEBUG로 설정. Spring 내부, Hibernate 등 외부 라이브러리 로그는 root 레벨(INFO)을 따름.

**Hibernate SQL 로그 끄기** — `show-sql: true`가 application.yaml에 있어도 logback에서 `OFF`로 설정하면 출력 안 됨. SQL 보고 싶을 때만 `DEBUG`로 바꿔서 확인.

---

## pong-to-rich에서 사용된 곳

### AuthService.java

```java
log.info("[회원가입] 시도: {}", request.getEmail());
log.warn("[회원가입] 이메일 중복: {}", request.getEmail());
log.info("[로그인] 완료: {}", request.getEmail());
log.warn("[로그인] 비밀번호 불일치: {}", request.getEmail());
log.debug("[로그인] Refresh Token 갱신: {}", request.getEmail());
log.warn("[토큰 재발급] DB에 없는 Refresh Token");
log.info("[토큰 재발급] 완료: {}", saved.getEmail());
log.info("[로그아웃] 처리: {}", email);
```

### JwtAuthenticationFilter.java

```java
log.debug("[JWT] 인증 성공: {} ({})", email, request.getRequestURI());
log.warn("[JWT] 토큰 검증 실패: {}", request.getRequestURI());
```
