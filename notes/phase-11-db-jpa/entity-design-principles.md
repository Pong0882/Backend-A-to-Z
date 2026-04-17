# Entity 설계 원칙 — pong-to-rich 기준

## 핵심 원칙 요약

오늘 만든 모든 Entity에 공통으로 적용한 원칙들을 한 곳에 정리한다.

---

## 1. 기본키는 Long + IDENTITY

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

- MySQL AUTO_INCREMENT 사용
- `int` 아닌 `Long` — 대용량 대비

---

## 2. 생성자는 PROTECTED + Builder

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA용, 외부 직접 생성 차단
public class Order {
    @Builder  // 클래스 아닌 생성자에 붙임
    public Order(User user, ...) { ... }
}
```

- `@Setter` 없음 — 상태 변경은 의미 있는 메서드로만

---

## 3. 모든 연관관계는 LAZY

```java
@ManyToOne(fetch = FetchType.LAZY)  // 반드시 명시
@JoinColumn(name = "user_id")
private User user;
```

- EAGER는 절대 쓰지 않음
- 필요할 때만 JOIN FETCH로 즉시 로드

---

## 4. Enum은 STRING

```java
@Enumerated(EnumType.STRING)  // ORDINAL 절대 금지
private OrderStatus status;
```

- ORDINAL은 순서 변경 시 데이터 오염

---

## 5. createdAt은 updatable = false

```java
@Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@Column(nullable = false)
private LocalDateTime updatedAt;
```

- `@PrePersist` / `@PreUpdate`로 자동 관리
- 서비스에서 직접 세팅하지 않음

---

## 6. 가격/금액은 BigDecimal

```java
@Column(precision = 12, scale = 4)
private BigDecimal closePrice;
```

- `double`, `float` 절대 금지 — 정밀도 손실
- 예수금처럼 큰 금액은 `precision = 15, scale = 2`

---

## 7. 상태 변경 메서드를 Entity 안에

```java
// Service에서 직접 필드 세팅 금지
// order.status = OrderStatus.CANCELLED;  ← 이렇게 하지 않음

// Entity 안에 의미 있는 메서드
public void cancel() {
    this.status = OrderStatus.CANCELLED;
}

public void fill(int filledQuantity) {
    this.filledQuantity += filledQuantity;
    this.status = this.filledQuantity >= this.quantity
        ? OrderStatus.FILLED : OrderStatus.PARTIAL;
}
```

검증 로직, 연쇄 상태 변경을 Entity 메서드 안에 캡슐화.

---

## 8. 복합 UNIQUE는 @Table에서

```java
@Table(
    name = "stocks",
    uniqueConstraints = @UniqueConstraint(columnNames = {"code", "market"})
)
```

단일 unique → `@Column(unique = true)`
복합 unique → 반드시 `@Table`의 `uniqueConstraints`

---

## 9. Soft Delete — deletedAt + isActive

```java
@Column
private LocalDateTime deletedAt;  // null = 정상, 값 있음 = 탈퇴

@Column(nullable = false)
private boolean isActive = true;

public void softDelete() {
    this.deletedAt = LocalDateTime.now();
    this.isActive = false;
}
```

Hard Delete 금지 — 연관 데이터 무결성 보장

---

## 10. nullable 전략

```java
// 필수 값 — nullable = false
@Column(nullable = false)
private String email;

// 선택 값 — 어노테이션 없음 (기본값 nullable = true)
@Column
private String profileImage;

// 연관관계 선택 — @JoinColumn nullable 생략 (기본 nullable)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "strategy_id")  // 수동 주문은 strategy 없음
private Strategy strategy;
```

`nullable = false`는 DB DDL에 `NOT NULL` 제약으로 반영됨.
