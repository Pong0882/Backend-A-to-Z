# 더티 체킹 (Dirty Checking)

## 개념

JPA가 영속성 컨텍스트에 있는 Entity의 **변경을 자동으로 감지**해서 트랜잭션 종료 시 UPDATE 쿼리를 날리는 메커니즘.

`save()`를 호출하지 않아도 된다.

## 동작 원리

```
1. 조회 — Entity를 DB에서 읽어 영속성 컨텍스트에 올림
2. 스냅샷 — 조회 시점의 상태를 복사해서 보관
3. 변경 — 코드에서 필드 값 변경
4. flush — 트랜잭션 종료 시 스냅샷과 현재 상태 비교
5. UPDATE — 다르면 자동으로 UPDATE 쿼리 실행
```

```java
@Transactional
public void deleteUser(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(...);
    // 1. 조회 → 영속성 컨텍스트에 올라감
    // 2. 스냅샷: {deletedAt: null, isActive: true}

    user.softDelete();
    // 3. 변경: {deletedAt: 2026-04-17T..., isActive: false}

    // save() 없음!
    // 4. 트랜잭션 종료 시 스냅샷과 비교
    // 5. UPDATE users SET deleted_at=..., is_active=false WHERE id=1
}
```

## @Transactional이 반드시 필요한 이유

더티 체킹은 **트랜잭션 안에서만** 동작한다.

```java
// @Transactional 없으면
public void deleteUser(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(...);
    user.softDelete();
    // 트랜잭션이 없으니 flush가 안 됨 → UPDATE 쿼리 안 날아감
    // 변경이 DB에 반영되지 않음
}
```

## save()와의 차이

```java
// 더티 체킹 — 조회한 Entity를 수정할 때
@Transactional
public void updateNickname(Long userId, String nickname) {
    User user = userRepository.findById(userId).orElseThrow(...);
    user.updateNickname(nickname);  // save() 불필요
}

// save() — 새로운 Entity를 저장할 때
public void signUp(SignUpRequest request) {
    User user = User.builder()...build();
    userRepository.save(user);  // 새 Entity라 save() 필요
}
```

- **새로 만든 Entity** → `save()` 필요 (영속성 컨텍스트에 없으므로)
- **조회한 Entity 수정** → `save()` 불필요 (이미 영속 상태)

## 주의사항

### 1. @Transactional 범위 밖에서 수정하면 반영 안 됨

```java
@Transactional
public User getUser(Long id) {
    return userRepository.findById(id).orElseThrow(...);
}

// 다른 곳에서
User user = userService.getUser(1L);  // 트랜잭션 종료됨 (준영속 상태)
user.updateNickname("pong");          // 변경해도 DB 반영 안 됨
```

### 2. 변경 감지는 영속 상태 Entity만 해당

```java
User user = new User(...);  // 비영속 상태
user.updateNickname("pong");
// save() 안 하면 DB에 저장 안 됨 — 더티 체킹 대상 아님
```

### 3. 전체 컬럼 UPDATE

기본적으로 변경된 컬럼만이 아니라 **전체 컬럼을 UPDATE**한다.

```sql
-- nickname만 바꿔도
UPDATE users SET email=?, password=?, nickname=?, profile_image=?, ... WHERE id=?
-- 전체 컬럼이 나감
```

변경된 컬럼만 UPDATE하고 싶으면 `@DynamicUpdate` 사용.

```java
@Entity
@DynamicUpdate  // 변경된 컬럼만 UPDATE 쿼리에 포함
public class User { ... }
```

## pong-to-rich에서 사용된 곳

오늘 만든 모든 update 메서드가 더티 체킹 기반이다.

```java
// User.java
public void softDelete() { ... }         // 탈퇴 처리
public void updateNickname(...) { ... }  // 닉네임 변경
public void updateProfileImage(...) { ... }

// BrokerAccount.java
public void syncBalance(...) { ... }     // 예수금 동기화
public void deactivate() { ... }         // 계좌 비활성화

// Strategy.java
public void activate() { ... }           // 전략 실행
public void pause() { ... }
public void updateLastCheckedAt() { ... }

// Order.java
public void fill(int filledQuantity) { ... }  // 체결 처리
public void cancel() { ... }
public void fail() { ... }

// Holding.java
public void updateQuantityAndPrice(...) { ... }
public void toggleHidden() { ... }
```

Service에서 이 메서드들을 호출할 때 `@Transactional`만 붙이면 `save()` 없이 DB에 반영된다.
