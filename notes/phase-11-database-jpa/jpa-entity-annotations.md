# JPA Entity 어노테이션

## @Entity

클래스가 JPA 엔티티임을 선언. 이 어노테이션이 있어야 JPA가 해당 클래스를 DB 테이블과 매핑한다.

```java
@Entity
public class User { ... }
```

- 기본 생성자가 반드시 있어야 한다 (JPA가 리플렉션으로 객체를 생성하기 때문)
- `public` 또는 `protected` 기본 생성자 필요

---

## @Table

엔티티와 매핑할 테이블 이름을 지정한다. 생략하면 클래스 이름이 테이블 이름이 된다.

```java
@Entity
@Table(name = "users")  // DB 테이블명: users
public class User { ... }
```

> `User` 클래스에 `@Table(name = "users")` 를 쓰는 이유 — `user`는 MySQL 예약어라 충돌 방지

---

## @Id

기본키(Primary Key) 필드를 지정한다.

```java
@Id
private Long id;
```

---

## @GeneratedValue

기본키 생성 전략을 지정한다.

| 전략 | 설명 |
|------|------|
| `IDENTITY` | DB의 AUTO_INCREMENT 사용 (MySQL 기본) |
| `SEQUENCE` | DB 시퀀스 사용 (Oracle 등) |
| `TABLE` | 키 생성 전용 테이블 사용 (비추천) |
| `AUTO` | DB에 따라 자동 선택 |

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

MySQL에서는 `IDENTITY` 전략이 일반적이다.

---

## @Column

필드와 컬럼의 매핑 상세 설정. 생략하면 필드명이 컬럼명이 된다.

```java
@Column(nullable = false, unique = true)
private String email;

@Column(nullable = false)
private String password;
```

| 속성 | 설명 | 기본값 |
|------|------|--------|
| `name` | 컬럼명 | 필드명 |
| `nullable` | NULL 허용 여부 | true |
| `unique` | 유니크 제약조건 | false |
| `length` | 문자열 길이 | 255 |

---

## @Enumerated

Enum 타입 필드를 DB에 저장하는 방식 지정.

```java
@Enumerated(EnumType.STRING)
private Role role;
```

| 타입 | DB 저장값 | 설명 |
|------|---------|------|
| `EnumType.ORDINAL` | 0, 1, 2... | Enum 순서 숫자로 저장. **사용 금지** |
| `EnumType.STRING` | "ROLE_USER" | Enum 이름 문자열로 저장. 항상 이걸 써야 함 |

**`ORDINAL`을 쓰면 안 되는 이유** — Enum 중간에 항목을 추가하면 기존 데이터의 숫자가 밀려서 전부 다른 값이 됨. `STRING`은 이름으로 저장하니까 순서 변경에 영향 없음.

---

## @NoArgsConstructor(access = AccessLevel.PROTECTED)

Lombok이 제공하는 어노테이션. `protected` 접근 제어자를 가진 기본 생성자를 자동 생성한다.

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    // Lombok이 아래를 자동 생성:
    // protected User() {}
}
```

**왜 `PROTECTED`인가:**
- JPA는 리플렉션으로 객체를 생성할 때 기본 생성자가 필요 → `protected`면 JPA 내부에서 접근 가능
- `private`이면 JPA가 접근 못함
- `public`이면 외부에서 `new User()`로 불완전한 객체를 만들 수 있어서 위험
- `protected`가 JPA 요구사항을 만족하면서 외부 직접 생성을 막는 적절한 수준

**`@Builder`와 함께 쓰는 이유:**
```java
// 외부에서 객체 생성은 반드시 Builder로만
User user = User.builder()
        .email("test@test.com")
        .password(encodedPassword)
        .role(Role.ROLE_USER)
        .build();

// new User() 로 직접 생성 불가 (protected라서)
```

---

## pong-to-rich에서 사용된 곳

### User.java

```java
@Entity
@Table(name = "users")           // MySQL 예약어 충돌 방지
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA용 기본 생성자, 외부 직접 생성 방지
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Long id;

    @Column(nullable = false, unique = true)  // NOT NULL + UNIQUE 제약
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)  // "ROLE_USER" 문자열로 저장
    private Role role;

    @Builder  // 외부에서 객체 생성 시 사용
    public User(String email, String password, Role role) { ... }
}
```
