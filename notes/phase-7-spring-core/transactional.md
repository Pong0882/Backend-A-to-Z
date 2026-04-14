# @Transactional

## 왜 필요한가

DB 작업은 여러 쿼리가 묶여서 실행되는 경우가 많다.
예: 로그인 시 `refresh_tokens` 조회 + 저장/갱신이 하나의 요청에서 함께 일어난다.

중간에 실패하면 일부만 저장된 상태가 될 수 있다.
`@Transactional`은 이 묶음 전체를 하나의 단위로 처리한다 — 전부 성공하거나, 전부 실패하거나.

---

## 동작 원리 — 프록시 기반 AOP

Spring은 `@Transactional`이 붙은 빈을 직접 쓰지 않는다.
`@Service`가 붙은 클래스를 Spring이 IoC 컨테이너에 등록할 때, `@Transactional`이 있으면 **진짜 객체를 감싼 프록시 객체를 대신 등록한다.**

Controller가 `StockService`를 주입받을 때 실제로는 프록시를 받는 것이다.

```
Controller가 StockService 주입받을 때
→ 실제 StockService가 아니라
→ Spring이 만든 프록시 StockService를 받는다
```

프록시가 하는 일을 코드로 표현하면:

```java
// Spring이 내부적으로 이런 클래스를 자동 생성한다고 이해하면 됨
class StockService$$SpringProxy extends StockService {

    @Override
    public int fetchAndSaveDailyPrices(...) {
        트랜잭션 시작();                          // BEGIN
        try {
            super.fetchAndSaveDailyPrices(...);  // 실제 코드 실행
            커밋();                               // COMMIT
        } catch (RuntimeException e) {
            롤백();                               // ROLLBACK
            throw e;
        }
    }
}
```

Controller 입장에서는 `StockService`를 쓰는 것 같지만, **실제로는 프록시가 앞에서 트랜잭션을 열고 닫는다.**

---

## 주요 속성

### propagation — 트랜잭션 전파

| 옵션 | 동작 |
|------|------|
| `REQUIRED` (기본값) | 기존 트랜잭션이 있으면 참여, 없으면 새로 시작 |
| `REQUIRES_NEW` | 기존 트랜잭션을 일시 중단하고 새 트랜잭션 시작 |
| `NESTED` | 기존 트랜잭션 안에 중첩 트랜잭션 생성 (부분 롤백 가능) |
| `SUPPORTS` | 기존 트랜잭션이 있으면 참여, 없으면 트랜잭션 없이 실행 |
| `NOT_SUPPORTED` | 트랜잭션 없이 실행 (기존 트랜잭션은 일시 중단) |
| `NEVER` | 트랜잭션이 있으면 예외 |
| `MANDATORY` | 트랜잭션이 없으면 예외 |

**REQUIRED (기본값)** — 있으면 합류, 없으면 새로 시작

```java
@Transactional  // propagation 생략 = REQUIRED
public TokenResponse login(LoginRequest request) {
    userRepository.findByEmail(...);       // 트랜잭션 A
    refreshTokenRepository.save(...);      // 트랜잭션 A (같은 트랜잭션에 합류)
}
```

`login()`이 열어둔 트랜잭션 하나에 두 쿼리가 참여한다.
중간에 하나라도 실패하면 전부 롤백된다.

**REQUIRES_NEW** — 기존 트랜잭션과 무관하게 새 트랜잭션 시작

```java
@Transactional                                          // 트랜잭션 A
public void createOrder(OrderRequest request) {
    orderRepository.save(order);                        // 트랜잭션 A
    paymentRepository.save(payment);                    // 트랜잭션 A

    auditLogService.saveLog("주문 생성");                // → 트랜잭션 B로 독립 실행

    throw new RuntimeException("결제 실패");            // 트랜잭션 A 롤백
}

@Transactional(propagation = Propagation.REQUIRES_NEW)  // 트랜잭션 B
public void saveLog(String message) {
    auditLogRepository.save(message);                   // B는 이미 커밋 완료
}
```

실행 흐름:
```
createOrder() → 트랜잭션 A 시작
    order 저장 (A)
    payment 저장 (A)
    saveLog() 호출 → A 잠깐 중단, 트랜잭션 B 새로 시작
        log 저장 (B) → B 커밋 ✅
    A 재개
    RuntimeException → A 롤백 ❌

결과: order, payment는 사라졌지만 log는 DB에 남아있음
```

`REQUIRES_NEW`를 쓰는 케이스는 정해져 있다:
- **감사 로그** — 누가 언제 뭘 시도했는지는 실패해도 남겨야 함
- **알림/이메일 발송 기록** — 발송 시도 자체를 기록해야 함
- **실패 이력 저장** — 실패했다는 사실을 저장하는데 트랜잭션이 롤백되면 저장도 안 되는 모순

일반 비즈니스 로직은 그냥 `REQUIRED`(기본값)로 하나로 묶으면 된다.

실무에서 주로 쓰는 것: `REQUIRED`(기본), `REQUIRES_NEW`(독립 실행 필요할 때)

### isolation — 트랜잭션 격리 수준

| 옵션 | 동작 |
|------|------|
| `DEFAULT` | DB 기본값 사용 (MySQL InnoDB: REPEATABLE_READ) |
| `READ_UNCOMMITTED` | 커밋 안 된 데이터도 읽기 가능 — Dirty Read 발생 |
| `READ_COMMITTED` | 커밋된 데이터만 읽기 — Non-Repeatable Read 가능 |
| `REPEATABLE_READ` | 같은 쿼리 재실행 시 동일 결과 보장 — Phantom Read 가능 |
| `SERIALIZABLE` | 완전 격리 — 성능 저하 큼 |

격리 수준이 높아질수록 동시성은 낮아지고 정합성은 높아진다.

실무에서는 **거의 DEFAULT로 둔다.** MySQL InnoDB 기본값이 `REPEATABLE_READ`인데 대부분 충분하고, 직접 바꾸면 오히려 문제 생기기 쉽다.

### readOnly — 읽기 전용

```java
@Transactional(readOnly = true)
public List<StockPriceResponse> getDailyPrices(String stockCode) { ... }
```

- `true`로 설정하면 Hibernate가 **스냅샷 저장, 더티체킹(변경감지)을 생략**한다
- 쓰기가 없는 조회 메서드에 붙이면 성능 최적화 효과
- Read Replica 라우팅에도 사용 (Phase 11-6에서 실습 예정)

### rollbackFor — 롤백 대상 예외 지정

```java
@Transactional(rollbackFor = Exception.class)
```

- **기본값**: `RuntimeException`과 `Error`에서만 롤백
- `Exception`(Checked Exception)은 기본적으로 롤백 안 됨
- 명시적으로 지정하지 않으면 `IOException`, `SQLException` 등에서 롤백이 안 된다

### timeout — 타임아웃 (초 단위)

```java
@Transactional(timeout = 10)  // 10초 초과 시 강제 롤백
```

---

## readOnly = true vs @Transactional 없음의 차이

| 구분 | 설명 |
|------|------|
| 트랜잭션 없음 | 각 쿼리가 개별 트랜잭션으로 실행됨 (Spring Data JPA 기본 동작) |
| `@Transactional(readOnly = true)` | 하나의 트랜잭션 안에서 일관된 스냅샷으로 여러 쿼리 실행 + 더티체킹 생략 |

조회가 여러 쿼리로 이루어진 경우 `readOnly = true`가 데이터 일관성 보장 + 성능 최적화 모두를 챙긴다.

---

## 주의사항

### 1. 자기 호출(Self-Invocation) 문제

같은 클래스 안에서 `this.메서드()`로 호출하면 프록시를 건너뛰어서 `@Transactional`이 무시된다.

```java
@Service
public class OrderService {

    public void createOrder() {
        // 뭔가 하다가...
        this.sendNotification();  // ❌ this = 진짜 객체, 프록시가 아님
    }

    @Transactional
    public void sendNotification() {
        // 트랜잭션이 열릴 것 같지만 실제로는 안 열림
    }
}
```

Controller가 `createOrder()`를 호출하는 흐름:

```
Controller → 프록시.createOrder()   ← createOrder에 @Transactional 없으니 그냥 통과
               → this.sendNotification()  ← this가 프록시가 아닌 진짜 객체
                    → 트랜잭션 안 열림 ❌
```

반면 Controller에서 직접 호출하면:

```
Controller → 프록시.sendNotification()  ← 프록시가 트랜잭션 열어줌 ✅
```

**해결 방법:** `sendNotification()`을 별도 Service 빈으로 분리한다.
외부 빈에서 호출해야 프록시를 거친다.

```java
@Service
public class OrderService {
    private final NotificationService notificationService;  // 별도 빈 주입

    public void createOrder() {
        notificationService.sendNotification();  // ✅ 프록시.sendNotification() 호출됨
    }
}

@Service
public class NotificationService {
    @Transactional
    public void sendNotification() { ... }  // 이제 트랜잭션 정상 동작
}
```

### 2. `@Transactional`은 `public` 메서드에만 적용

`private`, `protected`, `package-private` 메서드에 붙여도 프록시가 적용되지 않는다.

### 3. Checked Exception은 기본 롤백 안 됨

```java
@Transactional
public void save() throws IOException {
    // IOException 발생해도 기본적으로 롤백 안 됨
    // rollbackFor = IOException.class 명시 필요
}
```

---

## pong-to-rich에서 사용된 곳

### StockService — `@Transactional(readOnly = true)`

```java
// StockService.java:77
@Transactional(readOnly = true)
public List<StockPriceResponse> getDailyPrices(String stockCode) {
    Stock stock = stockRepository.findByCode(stockCode)
            .orElseThrow(() -> new IllegalArgumentException("종목을 찾을 수 없습니다: " + stockCode));

    return stockPriceRepository.findByStockOrderByTradeDateDesc(stock)
            .stream()
            .map(StockPriceResponse::from)
            .collect(Collectors.toList());
}
```

- `stockRepository.findByCode()` + `stockPriceRepository.findByStockOrderByTradeDateDesc()` 두 쿼리를 하나의 트랜잭션에서 실행
- 쓰기가 없으므로 `readOnly = true` — 더티체킹 생략으로 성능 최적화

### StockService — `@Transactional`

```java
// StockService.java:92
@Transactional
public int fetchAndSaveDailyPrices(String stockCode, String startDate, String endDate) { ... }
```

- 종목 등록(`stockRepository.save`) + 일봉 데이터 다건 저장(`stockPriceRepository.save` 반복)이 하나의 트랜잭션
- 중간에 실패하면 해당 페이지에서 저장된 것들이 전부 롤백됨
- 단, `Thread.sleep(500)` 포함 → 트랜잭션이 오래 유지됨. 추후 페이지 단위 트랜잭션 분리 고려 필요

### AuthService — `@Transactional`

```java
// AuthService.java:57
@Transactional
public TokenResponse login(LoginRequest request) { ... }

// AuthService.java:134
@Transactional
public void logout(String email) {
    refreshTokenRepository.deleteByEmail(email);
}
```

- `login`: 사용자 조회 + Refresh Token 저장/갱신을 하나의 트랜잭션으로 묶음
- `logout`: Refresh Token 삭제 단일 쓰기이지만 명시적으로 트랜잭션 보장
- `signUp`에는 `@Transactional`이 없음 — Spring Data JPA `save()`가 자체 트랜잭션으로 처리하므로 단일 저장이면 생략 가능. 단, 여러 저장 작업이 묶일 경우 추가 필요
