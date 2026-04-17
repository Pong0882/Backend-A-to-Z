# JPA 연관관계 (@OneToOne / @OneToMany / @ManyToOne / @ManyToMany)

## 연관관계 종류

| 어노테이션 | 관계 | 예시 |
|-----------|------|------|
| `@OneToOne` | 1:1 | User ↔ Portfolio |
| `@OneToMany` | 1:N | User → Orders (한 유저가 여러 주문) |
| `@ManyToOne` | N:1 | Order → User (여러 주문이 한 유저에) |
| `@ManyToMany` | N:M | 실무에서 거의 안 씀 — 중간 테이블 Entity로 분리 |

## @ManyToOne — 가장 많이 쓰는 패턴

```java
// Order.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

- `@JoinColumn(name = "user_id")` — FK 컬럼명 지정
- `fetch = FetchType.LAZY` — 반드시 LAZY. EAGER는 N+1 문제 발생

## @OneToOne

```java
// Portfolio.java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;
```

1:1 관계. `unique = true`를 붙여야 실제로 유니크 제약이 걸린다.

## FetchType — LAZY vs EAGER

```
EAGER — 연관 Entity를 항상 즉시 로드 (기본값: @OneToOne, @ManyToOne)
LAZY  — 실제로 접근할 때만 로드 (기본값: @OneToMany, @ManyToMany)
```

**실무에서는 항상 LAZY를 명시한다.** EAGER를 쓰면 조회할 때마다 연관 Entity를 전부 JOIN해서 가져오는데, 복잡한 연관관계에서 예상치 못한 쿼리가 발생한다.

```java
// 이렇게 하면 Order 조회 시 User, Strategy, BrokerAccount, Stock을 전부 즉시 로드
// → 하나의 조회에 수십 개의 JOIN이 발생할 수 있음
@ManyToOne(fetch = FetchType.EAGER)  // 절대 하지 마라
private User user;
```

## N+1 문제

```java
List<Order> orders = orderRepository.findAll();  // 쿼리 1번

for (Order order : orders) {
    order.getUser().getNickname();  // 유저 조회 쿼리 N번 추가 발생
}
// 총 1 + N번 쿼리 → N+1 문제
```

해결: `@EntityGraph` 또는 JPQL `JOIN FETCH`로 필요한 경우에만 즉시 로드

```java
@Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.status = :status")
List<Order> findByStatusWithUser(@Param("status") Order.OrderStatus status);
```

## 복합 UNIQUE 제약 (@UniqueConstraint)

여러 컬럼 조합이 유니크해야 할 때 사용한다.

```java
@Table(
    name = "broker_accounts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "broker", "account_type"})
)
```

단일 컬럼 unique는 `@Column(unique = true)`, 복합 unique는 반드시 `@Table`에서 `@UniqueConstraint` 사용.

## pong-to-rich에서 사용된 곳

```java
// Portfolio.java — @OneToOne
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;

// Order.java — @ManyToOne 다중 연관
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "strategy_id")  // nullable — 수동 주문은 strategy 없음
private Strategy strategy;
```
