# JPA 생명주기 콜백 (@PrePersist / @PreUpdate)

## 개념

JPA Entity는 저장/수정/삭제 시점에 특정 메서드를 자동으로 호출하는 콜백을 지원한다.
`createdAt`, `updatedAt` 같은 공통 컬럼을 코드 여러 곳에서 직접 세팅하지 않아도 된다.

## 주요 콜백 어노테이션

| 어노테이션 | 실행 시점 |
|-----------|----------|
| `@PrePersist` | `em.persist()` 직전 — 최초 저장 전 |
| `@PostPersist` | `em.persist()` 직후 — 저장 완료 후 |
| `@PreUpdate` | `em.merge()` 직전 — 수정 전 |
| `@PostUpdate` | `em.merge()` 직후 — 수정 완료 후 |
| `@PreRemove` | `em.remove()` 직전 — 삭제 전 |
| `@PostRemove` | `em.remove()` 직후 — 삭제 완료 후 |
| `@PostLoad` | 조회 직후 — Entity가 영속성 컨텍스트에 로드된 후 |

## 실무에서 가장 많이 쓰는 패턴

```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

`protected` 접근자를 쓰는 이유 — 외부에서 직접 호출하는 게 아니라 JPA가 내부적으로 호출하기 때문. `private`도 가능하지만 프록시 상속 구조에서 문제가 생길 수 있어 `protected`가 관례.

## @EntityListeners로 분리하는 방법

콜백 로직을 Entity 밖으로 꺼내고 싶으면 `@EntityListeners`를 쓴다.

```java
// 별도 클래스로 분리
public class AuditListener {

    @PrePersist
    public void prePersist(Object entity) { ... }

    @PreUpdate
    public void preUpdate(Object entity) { ... }
}

// Entity에 적용
@EntityListeners(AuditListener.class)
@Entity
public class User { ... }
```

Spring Data JPA의 `@CreatedDate` / `@LastModifiedDate`도 이 방식으로 동작한다.

## 주의사항

- 콜백 메서드 안에서 다른 Entity를 조회하거나 flush()를 호출하면 안 됨 — 무한 루프 가능
- `@PrePersist`는 `id`가 아직 없는 시점에 실행됨 (AUTO_INCREMENT라면 DB가 id를 생성하기 전)

## pong-to-rich에서 사용된 곳

모든 Entity (User, Stock, BrokerAccount, Strategy, Order 등)에 `@PrePersist` / `@PreUpdate` 적용.

```java
// User.java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

서비스 레이어에서 `createdAt`을 직접 세팅하지 않아도 저장 시점에 자동으로 채워진다.
