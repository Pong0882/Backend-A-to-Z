# Soft Delete (논리 삭제)

## 개념

**Hard Delete** — `DELETE FROM users WHERE id = 1` → 데이터가 DB에서 완전히 사라짐
**Soft Delete** — `UPDATE users SET deleted_at = NOW() WHERE id = 1` → 데이터는 남기고 삭제 표시만

## 왜 Soft Delete를 쓰는가

실무에서 유저를 Hard Delete하면 연관된 데이터가 전부 깨진다.

```
users 삭제 →
  orders.user_id → FK 위반 or NULL
  strategies.user_id → FK 위반 or NULL
  portfolios.user_id → FK 위반 or NULL
```

이 문제를 피하려면:
1. 연관 데이터 전부 CASCADE DELETE → 주문 내역까지 사라짐 (법적 보관 의무 위반 가능)
2. FK를 nullable로 → 데이터 무결성 포기
3. **Soft Delete** → 데이터 보존 + 논리적 삭제 표시

## 구현 방식

### 방법 1 — deletedAt (현재 pong-to-rich 방식)

```java
@Column
private LocalDateTime deletedAt;  // null이면 정상, 값이 있으면 탈퇴

public void softDelete() {
    this.deletedAt = LocalDateTime.now();
    this.isActive = false;
}
```

- 언제 삭제됐는지 시각까지 기록 가능
- 복구도 가능 (`deletedAt = null`로 되돌리면 됨)

### 방법 2 — isDeleted (boolean)

```java
@Column(nullable = false)
private boolean isDeleted = false;
```

- 단순하지만 삭제 시각을 알 수 없음

## 조회 시 주의사항

Soft Delete를 적용하면 **조회 쿼리마다 `WHERE deleted_at IS NULL` 조건을 추가**해야 한다.

```java
// 매번 이렇게 해야 함
userRepository.findByEmailAndDeletedAtIsNull(email);
```

이게 번거로우면 `@Where` 어노테이션으로 Entity 레벨에서 글로벌 필터를 걸 수 있다.

```java
@Entity
@Where(clause = "deleted_at IS NULL")  // 모든 조회에 자동 적용
public class User { ... }
```

단, `@Where`는 Hibernate 전용이고, 삭제된 데이터를 의도적으로 조회해야 할 때 우회하기 까다롭다는 단점이 있다. (관리자 페이지 등)

## 실무 운영 패턴

Soft Delete만 하면 DB에 삭제된 데이터가 쌓인다. 실무에서는 배치로 정리한다.

```
탈퇴 → deletedAt 세팅 (즉시)
       ↓
30일 후 → 배치 잡이 deletedAt이 30일 지난 유저를 Hard Delete
```

개인정보보호법상 탈퇴 후 즉시 파기 or 일정 기간 보관 후 파기 — 서비스 정책에 따라 결정.

## pong-to-rich에서 사용된 곳

```java
// User.java
@Column
private LocalDateTime deletedAt;

@Column(nullable = false)
private boolean isActive = true;

public void softDelete() {
    this.deletedAt = LocalDateTime.now();
    this.isActive = false;
}
```

유저 탈퇴 시 `user.softDelete()` 호출. 연관된 orders, strategies, portfolios는 그대로 보존.
