# Mockito — verify / never / ArgumentMatcher

## verify란

Mock 객체의 특정 메서드가 **호출됐는지, 몇 번 호출됐는지** 검증하는 것.

`assertThat`은 반환값을 검증하고, `verify`는 **행동(호출 여부)**을 검증한다.

```java
// assertThat — 결과값 검증
assertThat(token).isEqualTo(FAKE_TOKEN);

// verify — 행동 검증
verify(restClient, never()).post();  // post()가 한 번도 호출되지 않았는지
verify(redisTemplate.opsForValue()).set(anyString(), anyString(), anyLong(), any());  // set()이 호출됐는지
```

---

## verify 기본 사용법

```java
// 1번 호출됐는지 (기본값)
verify(mock).someMethod();
verify(mock, times(1)).someMethod();  // 위와 동일

// 한 번도 호출 안 됐는지
verify(mock, never()).someMethod();

// 정확히 N번 호출됐는지
verify(mock, times(3)).someMethod();

// 최소 1번 이상 호출됐는지
verify(mock, atLeastOnce()).someMethod();

// 최대 N번 이하 호출됐는지
verify(mock, atMost(2)).someMethod();
```

---

## pong-to-rich에서 쓴 패턴

### 캐시 히트 테스트 — KIS API를 호출하지 않았는지

```java
@Test
void getAccessToken_cacheHit() {
    given(valueOperations.get(REDIS_KEY)).willReturn(FAKE_TOKEN);

    kisAuthService.getAccessToken();

    // Redis에 토큰이 있으면 KIS API를 호출하면 안 됨
    verify(restClient, never()).post();
}
```

반환값(`token`)을 검증하는 것만으로는 부족하다.
`return FAKE_TOKEN`을 하드코딩해도 반환값 검증은 통과해버린다.
`never()`로 KIS API 호출 자체를 안 했다는 걸 함께 검증해야 테스트가 의미 있다.

### 캐시 미스 테스트 — Redis에 올바르게 저장했는지

```java
@Test
void getAccessToken_cacheMiss() {
    given(valueOperations.get(REDIS_KEY)).willReturn(null);
    given(responseSpec.body(KisTokenResponse.class)).willReturn(
        new KisTokenResponse(FAKE_TOKEN, "Bearer", 86400L, "2099-12-31 23:59:59")
    );

    kisAuthService.getAccessToken();

    verify(valueOperations).set(
        eq(REDIS_KEY),
        eq(FAKE_TOKEN),
        longThat(ttl -> ttl > 0),  // TTL이 양수인지
        eq(TimeUnit.SECONDS)
    );
}
```

---

## ArgumentMatcher — 정확한 값 대신 조건으로 검증

`verify`나 `given`에서 인자를 검증할 때 쓰는 도구.

### 기본 Matcher

```java
eq("exact")          // 정확히 이 값
any()                // 아무 값이나 (null 포함)
anyString()          // 아무 String
anyLong()            // 아무 Long
anyInt()             // 아무 int
isNull()             // null
isNotNull()          // null 아닌 것
```

### longThat / argThat — 커스텀 조건

정확한 값을 모르거나 범위 조건이 필요할 때 사용한다.

```java
// TTL이 정확히 몇 초인지는 테스트 실행 시각에 따라 달라짐
// 정확한 값 대신 "양수인지"만 검증
verify(valueOperations).set(
    eq(REDIS_KEY),
    eq(FAKE_TOKEN),
    longThat(ttl -> ttl > 0),  // 람다로 조건 정의
    eq(TimeUnit.SECONDS)
);

// argThat — 모든 타입에 사용 가능
verify(mock).someMethod(argThat(arg -> arg.startsWith("kis:")));
```

**왜 TTL을 `longThat(ttl -> ttl > 0)`으로 검증했나:**

TTL은 `ChronoUnit.SECONDS.between(LocalDateTime.now(), expiredAt)`으로 계산된다.
`LocalDateTime.now()`는 테스트를 실행하는 시각에 따라 매번 달라진다.
만료 시각을 `"2099-12-31 23:59:59"`로 넉넉하게 잡았어도 정확한 초 값은 알 수 없다.
"양수인지"만 검증하면 충분하고, 매번 바뀌는 값에 의존하지 않아서 테스트가 안정적이다.

---

## verify vs assertThat — 언제 무엇을 쓰나

| | assertThat | verify |
|---|---|---|
| 검증 대상 | 반환값, 상태 | 호출 여부, 호출 횟수 |
| 질문 | "결과가 이 값인가?" | "이 메서드가 호출됐는가?" |
| 예시 | `assertThat(token).isEqualTo(...)` | `verify(mock, never()).post()` |

둘을 같이 써야 테스트가 완전해지는 경우가 많다.

캐시 히트 테스트처럼:
- `assertThat(token).isEqualTo(FAKE_TOKEN)` → 올바른 값을 반환했는지
- `verify(restClient, never()).post()` → KIS API를 호출하지 않았는지

반환값만 맞아도 내부에서 불필요한 API를 호출했을 수 있다. 둘 다 검증해야 한다.
