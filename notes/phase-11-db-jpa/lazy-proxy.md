# FetchType.LAZY와 프록시 객체

## LAZY 로딩이란

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
```

Order를 조회할 때 연관된 User를 **즉시 가져오지 않는다.**
`order.getUser()`를 실제로 호출하는 시점에 User를 DB에서 가져온다.

## 프록시 객체

LAZY 설정 시 JPA는 실제 User 대신 **프록시 객체**를 넣어둔다.

```
order.getUser() 호출 전:
    order.user = UserProxy@1234  ← 가짜 객체, DB 쿼리 안 날아감

order.getUser().getNickname() 호출 시:
    → 그제서야 SELECT * FROM users WHERE id = ? 실행
    → 프록시가 실제 User 데이터로 초기화됨
```

프록시는 실제 User 클래스를 상속한 가짜 객체다. 겉으로 보면 User처럼 동작하지만 내부는 비어있다가 처음 접근 시 DB를 찌른다.

## 프록시 주의사항

### 1. 트랜잭션 밖에서 LAZY 접근 — LazyInitializationException

```java
@Transactional
public Order getOrder(Long id) {
    return orderRepository.findById(id).orElseThrow();
}  // 트랜잭션 종료 — 영속성 컨텍스트 닫힘

// 다른 곳에서
Order order = orderService.getOrder(1L);
order.getUser().getNickname();  // LazyInitializationException 발생!
// 영속성 컨텍스트가 닫혔으니 프록시 초기화 불가
```

**해결책:**
1. 트랜잭션 안에서 필요한 데이터를 DTO로 변환해서 반환
2. `JOIN FETCH`로 필요한 연관 Entity를 즉시 로드

### 2. instanceof, equals 주의

```java
User user = order.getUser();
user instanceof User  // true (프록시도 User 상속)
user.getClass() == User.class  // false (프록시 클래스)

// equals 구현 시 getClass() 비교 대신 instanceof 사용해야 함
```

### 3. @ToString 사용 시 무한 루프

```java
@ToString  // Lombok
public class Order {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    // toString()이 user.toString()을 호출 → user가 프록시 초기화 → User.toString()이 또 order 참조 → 무한루프
}
```

```java
@ToString(exclude = "user")  // 연관 Entity는 제외
public class Order { ... }
```

## EAGER vs LAZY 정리

```
EAGER — Order 조회 시 User, Stock, Strategy, BrokerAccount 전부 JOIN해서 즉시 로드
         → 필요 없는 데이터도 항상 가져옴
         → 연관관계 복잡해지면 예상치 못한 JOIN 폭발

LAZY  — 실제로 접근할 때만 로드
       → 필요한 것만 가져옴
       → 단, 트랜잭션 밖 접근 주의
```

**실무 원칙: 모든 연관관계는 LAZY, 필요한 경우에만 JOIN FETCH로 즉시 로드**

## pong-to-rich에서 사용된 곳

모든 연관관계에 `FetchType.LAZY` 명시.

```java
// Order.java
@ManyToOne(fetch = FetchType.LAZY)
private User user;

@ManyToOne(fetch = FetchType.LAZY)
private Strategy strategy;   // 수동 주문이면 null — LAZY라 문제없음

@ManyToOne(fetch = FetchType.LAZY)
private Stock stock;

// Portfolio.java
@OneToOne(fetch = FetchType.LAZY)
private User user;
```

나중에 Order 목록 API 구현 시 `user`, `stock` 정보가 필요하면:

```java
@Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.stock WHERE o.user = :user")
List<Order> findByUserWithDetails(@Param("user") User user);
```
