# @Enumerated — EnumType.STRING vs ORDINAL

## 개념

JPA에서 Enum을 DB 컬럼에 저장할 때 어떤 형태로 저장할지 지정한다.

## EnumType.ORDINAL (기본값 — 절대 쓰지 마라)

```java
@Enumerated(EnumType.ORDINAL)  // 기본값
private OrderStatus status;
```

Enum의 **선언 순서(0, 1, 2...)** 를 숫자로 저장한다.

```java
public enum OrderStatus {
    PENDING,    // 0
    PARTIAL,    // 1
    FILLED,     // 2
    CANCELLED,  // 3
    FAILED      // 4
}
```

DB에는 `0`, `1`, `2`... 숫자로 저장된다.

**왜 절대 쓰면 안 되는가:**

```java
// 나중에 EXPIRED 상태를 중간에 추가하면
public enum OrderStatus {
    PENDING,    // 0
    EXPIRED,    // 1 ← 새로 추가
    PARTIAL,    // 2 ← 기존 1이었던 것
    FILLED,     // 3 ← 기존 2였던 것
    CANCELLED,  // 4
    FAILED      // 5
}
// DB에 저장된 1은 원래 PARTIAL이었는데 이제 EXPIRED로 읽힘 → 데이터 오염
```

Enum 순서가 바뀌는 순간 기존 데이터가 전부 잘못 읽힌다.

## EnumType.STRING (항상 이걸 써라)

```java
@Enumerated(EnumType.STRING)
private OrderStatus status;
```

Enum의 **이름 문자열** 그대로 저장한다.

```
DB: "PENDING", "FILLED", "CANCELLED"
```

- Enum 중간에 새 값을 추가해도 기존 데이터 영향 없음
- DB에서 바로 읽어도 의미를 알 수 있음
- 저장 공간이 숫자보다 크지만 실무에서 문제가 되는 수준 아님

## pong-to-rich에서 사용된 곳

모든 Enum 컬럼에 `@Enumerated(EnumType.STRING)` 적용.

```java
// Order.java
@Enumerated(EnumType.STRING)
private OrderType orderType;   // "BUY", "SELL"

@Enumerated(EnumType.STRING)
private PriceType priceType;   // "MARKET", "LIMIT"

@Enumerated(EnumType.STRING)
private OrderStatus status;    // "PENDING", "FILLED", ...

// User.java
@Enumerated(EnumType.STRING)
private LoginType loginType;   // "LOCAL", "GOOGLE", ...

// Stock.java
@Enumerated(EnumType.STRING)
private Market market;         // "KRX", "NASDAQ", "NYSE"
```
