# Mockito Strictness — STRICT_STUBS vs LENIENT

## Strictness란

Mockito가 stub(given으로 설정한 동작)을 얼마나 엄격하게 관리하는지의 설정이다.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)  // 여기서 설정
class KisAuthServiceTest {
```

---

## STRICT_STUBS (기본값)

`@ExtendWith(MockitoExtension.class)`의 기본 strictness.

**두 가지를 검사한다:**

### 1. 사용하지 않은 stub → 테스트 실패

```java
@BeforeEach
void setUp() {
    given(restClient.post()).willReturn(requestBodyUriSpec);  // 공통 stub
    given(kisConfig.appKey()).willReturn("test-key");          // 공통 stub
}

@Test
void getAccessToken_cacheHit() {
    given(valueOperations.get(REDIS_KEY)).willReturn(FAKE_TOKEN);

    kisAuthService.getAccessToken();

    // 캐시 히트라서 KIS API를 호출하지 않음
    // → restClient.post(), kisConfig.appKey() stub이 사용되지 않음
    // → STRICT_STUBS이면 여기서 UnnecessaryStubbingException 발생
}
```

캐시 히트 테스트는 KIS API를 호출하지 않으니까 `restClient.post()`와 `kisConfig.appKey()` stub이 실행되지 않는다. STRICT_STUBS는 이걸 "불필요한 stub"으로 판단하고 실패시킨다.

### 2. 같은 stub을 중복 정의 → 경고

```java
given(mock.method()).willReturn("a");
given(mock.method()).willReturn("b");  // 중복 → 경고
```

---

## LENIENT

사용하지 않은 stub이 있어도 무시한다.

```java
@MockitoSettings(strictness = Strictness.LENIENT)
```

`setUp`에서 공통 stub을 설정해두고 테스트별로 일부만 사용해도 에러가 나지 않는다.

---

## 언제 LENIENT를 쓰나

`@BeforeEach setUp`에 공통 stub을 모아두는 패턴을 쓸 때.

```java
@BeforeEach
void setUp() {
    given(redisTemplate.opsForValue()).willReturn(valueOperations);

    // RestClient 체이닝 공통 설정
    given(restClient.post()).willReturn(requestBodyUriSpec);
    given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
    given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
    given(requestBodySpec.body(any())).willReturn(requestBodySpec);
    given(requestBodySpec.retrieve()).willReturn(responseSpec);

    given(kisConfig.appKey()).willReturn("test-appkey");
    given(kisConfig.appSecret()).willReturn("test-appsecret");
    given(kisConfig.baseUrl()).willReturn("https://...");
}
```

이 공통 stub들은 캐시 미스/실패 테스트에서만 쓰인다. 캐시 히트 테스트에서는 사용되지 않는다.

- STRICT_STUBS → 캐시 히트 테스트에서 `UnnecessaryStubbingException` 발생
- LENIENT → 사용 안 해도 무시, 테스트 통과

---

## STRICT_STUBS가 나쁜 건 아니다

오히려 STRICT_STUBS가 더 좋은 경우가 많다.

```java
given(mock.method()).willReturn("value");
// 근데 실제로 이 stub이 쓰이지 않는다면?
// → 테스트가 의도한 대로 동작하지 않고 있다는 신호일 수 있음
```

"설정했는데 쓰이지 않는 stub"은 두 가지 가능성:
1. 테스트가 예상과 다른 경로로 실행되고 있다
2. stub이 필요 없어졌는데 정리를 안 했다

STRICT_STUBS는 이런 상황을 잡아준다.

**결론:**
- 각 테스트마다 필요한 stub만 따로 설정 → STRICT_STUBS (더 엄격, 더 안전)
- `@BeforeEach`에 공통 stub을 모아두는 패턴 → LENIENT 필요

pong-to-rich KisAuthServiceTest는 `@BeforeEach`에 RestClient 체이닝 공통 설정을 모아뒀기 때문에 LENIENT를 사용했다.
