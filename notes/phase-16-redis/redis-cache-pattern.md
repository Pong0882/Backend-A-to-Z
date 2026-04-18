# Redis 캐싱 패턴 — Cache-Aside

## Cache-Aside (Lazy Loading)

가장 일반적인 캐싱 패턴. 캐시에 없을 때만 원본 소스에서 가져온다.

```
1. 캐시 조회
2. 있으면 → 바로 반환 (캐시 히트)
3. 없으면 → 원본 소스 조회 → 캐시에 저장 → 반환 (캐시 미스)
```

```java
public String getAccessToken() {
    String cached = redisTemplate.opsForValue().get(REDIS_KEY);
    if (cached != null) {
        return cached;           // 캐시 히트
    }
    return issueToken();         // 캐시 미스 → KIS API 호출
}
```

**장점:** 실제로 필요한 데이터만 캐시에 올라감  
**단점:** 첫 요청은 항상 느림 (Cold Start)

---

## TTL 계산 — 만료 시간 기반

KIS 토큰은 만료 시각이 응답에 포함된다. 만료 1시간 전을 TTL로 설정.

```java
LocalDateTime expiredAt = LocalDateTime
    .parse(response.accessTokenExpired().replace(" ", "T"))
    .minusHours(1);                                          // 1시간 여유

long ttlSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), expiredAt);

redisTemplate.opsForValue().set(REDIS_KEY, response.accessToken(), ttlSeconds, TimeUnit.SECONDS);
```

**왜 1시간 전인가?**  
만료 직전까지 쓰면 토큰이 만료된 채로 요청이 나갈 수 있다.  
TTL을 실제 만료보다 짧게 잡아서 갱신 여유를 확보.

---

## StringRedisTemplate vs RedisTemplate

| | StringRedisTemplate | RedisTemplate\<Object, Object\> |
|---|---|---|
| 키/값 타입 | String 고정 | 임의 타입 |
| 직렬화 | StringRedisSerializer | JdkSerializationRedisSerializer (기본) |
| 가독성 | redis-cli에서 바로 읽힘 | 바이너리로 저장돼서 안 읽힘 |
| 적합한 경우 | 토큰, 세션 등 문자열 값 | 복잡한 객체 캐싱 |

토큰처럼 String 값만 저장할 때는 `StringRedisTemplate`이 적합.

---

## opsForValue()

Redis의 String 타입(단순 키-값)을 다루는 인터페이스.

```java
// 저장 (TTL 포함)
redisTemplate.opsForValue().set("key", "value", 3600, TimeUnit.SECONDS);

// 조회
String value = redisTemplate.opsForValue().get("key");

// 조회 결과가 null이면 키 없음 (만료됐거나 처음부터 없음)
```

Redis 자료구조별 ops:
- `opsForValue()` — String
- `opsForList()` — List
- `opsForSet()` — Set
- `opsForHash()` — Hash
- `opsForZSet()` — Sorted Set

---

## 메모리 캐싱 vs Redis 캐싱 비교

기존 KisAuthService는 JVM 메모리(인스턴스 필드)에 토큰을 캐싱했다.

| | 메모리 캐싱 (이전) | Redis 캐싱 (현재) |
|---|---|---|
| 저장 위치 | JVM Heap | Redis 서버 |
| 서버 재시작 시 | 토큰 날아감 → 재발급 | 토큰 유지 |
| 다중 인스턴스 | 인스턴스마다 각자 발급 | 공유 |
| TTL 관리 | 코드로 직접 (`isTokenValid()`) | Redis가 자동 만료 |
| 테스트 | 필드 직접 접근 어려움 | Mock 주입 가능 |

단일 서버, 단일 계정이라도 Redis 캐싱이 더 안정적이다.  
재시작 시 불필요한 API 재호출을 막고, 추후 수평 확장 시 코드 변경 없음.
