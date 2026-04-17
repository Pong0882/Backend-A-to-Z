# 영속성 컨텍스트 (Persistence Context)

## 개념

JPA가 Entity를 관리하는 **메모리 공간**. DB와 애플리케이션 사이의 버퍼 역할을 한다.

```
애플리케이션
    ↕
영속성 컨텍스트 (1차 캐시 + 변경 감지 + 쓰기 지연)
    ↕
DB
```

`EntityManager`가 영속성 컨텍스트를 관리한다. Spring에서는 `@Transactional` 범위 안에서 자동으로 생성/소멸된다.

## Entity 상태 4가지

### 1. 비영속 (New/Transient)

```java
User user = new User(...);  // 영속성 컨텍스트와 무관
// JPA가 전혀 모르는 상태
```

### 2. 영속 (Managed)

```java
userRepository.save(user);           // save()로 영속화
// 또는
User user = userRepository.findById(1L).orElseThrow();  // 조회로 영속화

// 영속성 컨텍스트가 이 Entity를 관리 중
// 변경 감지(더티 체킹) 대상
```

### 3. 준영속 (Detached)

```java
// 트랜잭션이 끝나면 영속성 컨텍스트가 닫히면서 Entity가 준영속 상태가 됨
@Transactional
public User getUser(Long id) {
    return userRepository.findById(id).orElseThrow();
}
// 메서드 끝 → 트랜잭션 종료 → User가 준영속 상태

// 준영속 상태에서 필드 변경해도 DB 반영 안 됨
// LAZY 연관 Entity 접근 시 LazyInitializationException 발생
```

### 4. 삭제 (Removed)

```java
userRepository.delete(user);
// 트랜잭션 종료 시 DELETE 쿼리 실행
```

## 1차 캐시

영속성 컨텍스트는 조회한 Entity를 메모리에 캐싱한다.

```java
@Transactional
public void example() {
    User user1 = userRepository.findById(1L).orElseThrow();  // DB 쿼리 1번
    User user2 = userRepository.findById(1L).orElseThrow();  // 1차 캐시에서 반환 — DB 쿼리 없음

    System.out.println(user1 == user2);  // true — 같은 객체
}
```

같은 트랜잭션 안에서 같은 id로 여러 번 조회해도 DB는 1번만 찌른다.

단, 1차 캐시는 **트랜잭션 범위**다. 트랜잭션이 끝나면 사라진다. Redis 같은 2차 캐시와 다르다.

## 쓰기 지연 (Write-Behind)

```java
@Transactional
public void saveMultiple() {
    userRepository.save(user1);  // INSERT 쿼리를 버퍼에 쌓음
    userRepository.save(user2);  // INSERT 쿼리를 버퍼에 쌓음
    userRepository.save(user3);  // INSERT 쿼리를 버퍼에 쌓음

    // 트랜잭션 종료 시 flush → 버퍼의 INSERT 쿼리 3개를 한번에 DB로
}
```

매번 즉시 DB에 쓰지 않고 트랜잭션 종료 시 한번에 처리. 네트워크 왕복 횟수 감소.

단, `GenerationType.IDENTITY`는 INSERT 직후 id를 알아야 하므로 쓰기 지연 안 됨 — 즉시 INSERT.

## flush vs commit

```
flush — 영속성 컨텍스트의 변경사항을 DB에 동기화 (트랜잭션은 유지)
commit — 트랜잭션 종료 (flush 포함)
```

```java
@Transactional
public void example() {
    user.updateNickname("pong");
    // 아직 DB에 반영 안 됨

    em.flush();
    // 지금 UPDATE 쿼리 실행 — 트랜잭션은 아직 살아있음

    // 이후 작업...
} // commit — 트랜잭션 종료
```

Spring Data JPA는 트랜잭션 종료 시 자동으로 flush + commit.

## @Transactional과 영속성 컨텍스트

```
@Transactional 시작
    → 영속성 컨텍스트 생성
    → Entity 조회/수정/저장
@Transactional 종료
    → flush (변경사항 DB 반영)
    → commit
    → 영속성 컨텍스트 소멸
    → Entity들이 준영속 상태로 전환
```

## pong-to-rich 적용 포인트

```java
// AuthService.java
@Transactional  // 트랜잭션 시작 → 영속성 컨텍스트 생성
public void signUp(SignUpRequest request) {
    User user = User.builder()...build();  // 비영속
    userRepository.save(user);             // 영속화 — IDENTITY라 즉시 INSERT
}  // 트랜잭션 종료

// 나중에 구현할 updateNickname Service
@Transactional
public void updateNickname(Long userId, String nickname) {
    User user = userRepository.findById(userId).orElseThrow();  // 영속
    user.updateNickname(nickname);  // 더티 체킹 대상
    // save() 없음 — 트랜잭션 종료 시 UPDATE 자동 실행
}
```
