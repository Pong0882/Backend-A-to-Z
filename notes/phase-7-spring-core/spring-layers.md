# Spring 레이어 어노테이션

## 레이어드 아키텍처

Spring은 역할에 따라 클래스를 레이어로 나눈다.
각 레이어는 어노테이션으로 구분되고, 모두 내부적으로 `@Component`를 포함한다.

```
Client
  ↓
@RestController  (Controller 레이어) — 요청/응답 처리
  ↓
@Service         (Service 레이어)    — 비즈니스 로직
  ↓
@Repository      (Repository 레이어) — DB 접근
  ↓
Database
```

## @Component

- Spring Bean으로 등록하는 가장 기본 어노테이션
- 특정 레이어에 속하지 않는 공통 컴포넌트에 사용

## @Controller / @RestController

- **Controller 레이어** — HTTP 요청을 받아서 Service에 위임하고 응답 반환
- 요청 파라미터 검증, 응답 형식 결정
- 비즈니스 로직은 여기에 넣지 않는다

## @Service

- **Service 레이어** — 핵심 비즈니스 로직 처리
- 여러 Repository를 조합하거나 트랜잭션 관리
- `@Component`와 기능은 동일하나 "이 클래스는 서비스 레이어"라는 의미를 명시

```java
@Service
public class KisAuthService {
    // 토큰 발급, 캐싱 등 비즈니스 로직
}
```

## @Repository

- **Repository 레이어** — DB 접근 (JPA, JDBC 등)
- DB 관련 예외를 Spring의 `DataAccessException`으로 변환해주는 기능 포함
- JPA 사용 시 `JpaRepository` 상속하면 `@Repository` 생략 가능

## @Configuration + @Bean — 수동 Bean 등록

`@Component` 계열은 클래스 자체를 Bean으로 등록하지만, 외부 라이브러리 객체나 세밀한 설정이 필요한 경우는 `@Configuration` + `@Bean`으로 직접 등록한다.

```java
@Configuration  // 이 클래스가 Bean 정의 소스임을 Spring에 알림
public class AppConfig {

    @Bean           // 반환 객체를 Spring 컨테이너에 싱글톤 Bean으로 등록
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

**동작 원리:**
1. Spring이 `@Configuration` 클래스를 스캔
2. `@Bean` 메서드를 호출해서 반환값을 컨테이너에 등록
3. 이후 해당 타입이 필요한 곳에 자동 주입

**`@Component` vs `@Bean` 선택 기준:**

| 상황 | 선택 |
|------|------|
| 내가 만든 클래스를 Bean으로 등록 | `@Component` 계열 |
| 외부 라이브러리 객체를 Bean으로 등록 | `@Bean` |
| 생성 시 복잡한 설정이 필요한 경우 | `@Bean` |

**pong-to-rich에서:**
- `ObjectMapper`는 Jackson 라이브러리 클래스라 `@Component` 붙일 수 없음
- `AppConfig`에 `@Bean`으로 직접 등록 → `StockService`에 생성자 주입

## 왜 레이어를 나누는가

- **단일 책임 원칙** — 각 클래스가 하나의 역할만 담당
- **테스트 용이성** — 레이어별로 독립적으로 테스트 가능
- **유지보수성** — 비즈니스 로직 변경이 Controller에 영향 없음

## pong-to-rich에서

- `KisAuthController` → `@RestController` (토큰 조회 엔드포인트)
- `KisAuthService` → `@Service` (토큰 발급, 캐싱 로직)
- 앞으로 DB 접근 필요하면 → `@Repository` 추가 예정
