# Entity 생성자 패턴 — @NoArgsConstructor(PROTECTED) + @Builder

## 왜 이 조합을 쓰는가

JPA Entity는 두 가지 상충되는 요구사항이 있다.

1. **JPA 스펙** — 기본 생성자(파라미터 없는 생성자)가 반드시 있어야 함. JPA가 DB에서 데이터를 읽어 객체로 만들 때 기본 생성자로 인스턴스를 생성하기 때문.
2. **설계 원칙** — 외부에서 `new User()`로 빈 객체를 만들어 필드를 직접 세팅하는 걸 막아야 함. 불완전한 상태의 객체가 돌아다니면 버그 발생.

이 둘을 동시에 만족하는 조합이 `@NoArgsConstructor(access = AccessLevel.PROTECTED)` + `@Builder`다.

## @NoArgsConstructor(access = AccessLevel.PROTECTED)

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    // Lombok이 아래 코드를 자동 생성
    protected User() {}
}
```

- `protected` — 같은 패키지 or 상속 관계에서만 호출 가능
- JPA(Hibernate)는 리플렉션으로 `protected` 생성자도 호출 가능 → JPA 스펙 충족
- 외부 코드에서 `new User()` 직접 호출 불가 → 불완전한 객체 생성 차단

`private`으로 하면 안 되는 이유 — Hibernate가 프록시 객체(상속)를 만들 때 부모 생성자를 호출해야 하는데 `private`이면 접근 불가.

## @Builder

```java
@Builder
public User(String email, String password, String nickname, ...) {
    this.email = email;
    ...
}
```

- **생성자가 아닌 Builder 메서드에 붙인다** — 클래스에 `@Builder`를 붙이면 모든 필드를 받는 생성자가 생겨서 `@NoArgsConstructor`와 충돌
- 필요한 필드만 Builder 생성자 파라미터로 선언 → 나머지는 기본값이나 메서드로 세팅

```java
// 올바른 사용
User user = User.builder()
    .email("test@test.com")
    .password(encodedPassword)
    .nickname("pong")
    .role(User.Role.ROLE_USER)
    .loginType(User.LoginType.LOCAL)
    .build();

// 차단된 사용
User user = new User();  // 컴파일 에러 — protected라 외부 접근 불가
user.setEmail("...");    // Setter 없음
```

## @Getter만 있고 @Setter가 없는 이유

```java
@Getter
// @Setter 없음
public class User { ... }
```

Setter를 열어두면 어디서든 필드를 바꿀 수 있어서 변경 흐름 추적이 불가능해진다.

```java
// Setter가 있으면
user.setStatus(OrderStatus.CANCELLED);  // 누가 언제 왜 바꿨는지 모름

// 의미 있는 메서드로 대체
user.softDelete();   // 의도가 명확함, 내부에서 여러 필드 일관성 있게 처리 가능
order.cancel();      // 취소에 필요한 검증 로직도 여기 넣을 수 있음
```

상태 변경은 반드시 **의미 있는 메서드**를 통해서만 하는 것이 객체지향 원칙이다.

## pong-to-rich에서 사용된 곳

모든 Entity에 동일하게 적용.

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA용 기본 생성자, 외부 접근 차단
public class Order {

    @Builder  // 클래스가 아닌 생성자에 붙임
    public Order(User user, BrokerAccount brokerAccount, ...) {
        this.user = user;
        this.status = OrderStatus.PENDING;  // 생성 시 기본값 강제
        this.filledQuantity = 0;
    }

    // 상태 변경은 의미 있는 메서드로만
    public void fill(int filledQuantity) { ... }
    public void cancel() { ... }
}
```
