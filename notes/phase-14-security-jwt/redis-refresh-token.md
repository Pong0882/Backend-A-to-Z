# Redis 기반 Refresh Token 저장

## DB 방식의 문제점

Refresh Token을 MySQL에 저장하면 세 가지 문제가 있다.

**1. 성능**
토큰 저장/조회마다 디스크 I/O가 발생한다. 로그인이 많은 서비스에서 병목이 될 수 있다.

**2. 만료 토큰 누적**
TTL 자동 삭제 기능이 없어서 만료된 토큰이 계속 쌓인다.
주기적으로 `DELETE FROM refresh_tokens WHERE expires_at < NOW()` 배치가 필요하다.

**3. 코드 복잡도**
만료 여부를 코드에서 직접 체크해야 한다:
```java
if (saved.getExpiresAt().isBefore(LocalDateTime.now())) {
    refreshTokenRepository.delete(saved);
    throw new ExpiredTokenException();
}
```

---

## Redis 방식의 장점

**1. 성능**
메모리 기반 → 디스크 I/O 없음 → DB 대비 훨씬 빠름

**2. TTL 자동 만료**
키 저장 시 TTL을 설정하면 만료 후 자동 삭제된다.
만료 토큰 청소 배치 불필요.

**3. 코드 단순화**
만료된 키는 Redis가 자동으로 없애므로, 조회 결과가 없으면 그게 만료된 것이다.
별도 만료 체크 로직 불필요.

---

## Redis 키 구조 — 양방향 매핑

Redis는 역방향 조회(값으로 키 찾기)를 지원하지 않는다.
Refresh Token 처리에는 두 방향의 조회가 필요하다:

- **재발급 시**: 토큰 → 이메일 조회
- **로그아웃 시**: 이메일 → 토큰 삭제

그래서 두 가지 키를 동시에 저장하는 양방향 매핑 방식을 사용한다:

```
refresh:token:{token}  → 값: email    (재발급 시 사용)
refresh:email:{email}  → 값: token    (로그아웃 시 사용)
```

두 키에 동일한 TTL을 설정해서 함께 만료되도록 한다.

**재로그인 시 기존 토큰 정리:**
같은 이메일로 재로그인하면 이전 토큰의 `refresh:token:xxx` 키가 고아로 남을 수 있다.
저장 전에 이전 토큰을 먼저 삭제해서 키가 쌓이지 않도록 처리한다:

```java
String oldToken = redisTemplate.opsForValue().get(EMAIL_KEY_PREFIX + email);
if (oldToken != null) {
    redisTemplate.delete(TOKEN_KEY_PREFIX + oldToken);
}
```

---

## DB vs Redis 전환 가능한 구조 설계

성능 비교를 위해 두 구현체를 모두 유지하고 설정값으로 전환할 수 있게 설계했다.

### 인터페이스

```java
public interface RefreshTokenStore {
    void save(String email, String token, long expirationMs);
    Optional<String> findEmailByToken(String token);
    void deleteByEmail(String email);
}
```

### 구현체 선택 Config

```java
@Configuration
public class RefreshTokenStoreConfig {

    @Value("${token.store}")
    private String tokenStore;

    @Bean
    public RefreshTokenStore refreshTokenStore(
            @Qualifier("dbRefreshTokenStore") RefreshTokenStore dbStore,
            @Qualifier("redisRefreshTokenStore") RefreshTokenStore redisStore
    ) {
        return switch (tokenStore) {
            case "redis" -> redisStore;
            default -> dbStore;
        };
    }
}
```

### 전환 방법

```yaml
# application.yml
token:
  store: db     # db 또는 redis
```

값 하나만 바꾸면 전체 Refresh Token 저장소가 전환된다.

---

## StringRedisTemplate vs RedisTemplate

Spring Data Redis는 두 가지 템플릿을 제공한다.

| | StringRedisTemplate | RedisTemplate |
|---|---|---|
| 직렬화 | String 전용 (StringSerializer) | 기본 JdkSerializationRedisSerializer |
| 용도 | 문자열 데이터 | 객체 직렬화 |
| redis-cli 확인 | 사람이 읽을 수 있음 | 바이너리로 보임 |

Refresh Token은 email(String), token(String)만 저장하므로 `StringRedisTemplate`이 적합하다.
`redis-cli`에서 `keys *`로 조회했을 때 값을 바로 읽을 수 있어 디버깅에도 유리하다.

---

## pong-to-rich에서 사용된 곳

- `domain/auth/RedisRefreshTokenStore.java` — Redis 구현체
- `domain/auth/DbRefreshTokenStore.java` — DB 구현체
- `domain/auth/RefreshTokenStore.java` — 추상화 인터페이스
- `config/RefreshTokenStoreConfig.java` — 구현체 선택
- `service/AuthService.java` — `RefreshTokenStore` 주입받아 사용

**전환 설정:**
```yaml
# local: 현재 redis로 설정
token:
  store: redis

# prod: 환경변수로 주입
token:
  store: ${TOKEN_STORE:db}
```

**Redis 키 확인:**
```bash
docker exec -it pong-to-rich-redis redis-cli
keys *
# refresh:token:eyJhbG...
# refresh:email:test@test.com

ttl "refresh:email:test@test.com"
# 604754 (약 7일)
```
