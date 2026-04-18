# TDD 기초 — Red-Green-Refactor

## TDD란

테스트를 먼저 작성하고, 그 테스트를 통과시키는 코드를 나중에 작성하는 개발 방식.

```
Red   → 실패하는 테스트 작성
Green → 테스트를 통과하는 최소한의 코드 작성
Refactor → 코드 정리 (테스트는 계속 통과해야 함)
```

## 언제 TDD가 의미 있나

모든 코드에 TDD가 필요하진 않다. 아래 조건이 맞을 때 효과가 크다:

- **분기 로직이 있을 때** — 캐시 히트/미스, 성공/실패 경로
- **외부 시스템에 의존할 때** — KIS API, Redis 등 Mock이 필요한 경우
- **리팩토링 예정인 코드** — 테스트가 안전망 역할

단순 CRUD나 위임만 하는 코드는 TDD보다 통합 테스트가 더 실용적이다.

## pong-to-rich 첫 TDD 적용: KisAuthService

KisAuthService는 아래 세 분기가 있어서 TDD가 의미 있었다:

| 분기 | 테스트 |
|------|--------|
| Redis에 토큰 있음 | KIS API 호출 안 함 |
| Redis에 토큰 없음 | KIS API 호출 → Redis 저장 |
| KIS API null 응답 | BusinessException 던짐 |

---

## 단위 테스트 구조 — Given / When / Then

```java
@Test
@DisplayName("캐시 히트 — Redis에 토큰이 있으면 KIS API를 호출하지 않는다")
void getAccessToken_cacheHit() {
    // given — 테스트 전제 조건 설정
    given(valueOperations.get(REDIS_KEY)).willReturn(FAKE_TOKEN);

    // when — 테스트 대상 실행
    String token = kisAuthService.getAccessToken();

    // then — 결과 검증
    assertThat(token).isEqualTo(FAKE_TOKEN);
    verify(restClient, never()).post();  // KIS API 호출 안 했는지 확인
}
```

---

## @ExtendWith(MockitoExtension.class)

JUnit 5에서 Mockito를 사용하기 위한 확장.  
`@Mock`, `@InjectMocks`를 자동으로 처리해준다.

```java
@ExtendWith(MockitoExtension.class)
class KisAuthServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;  // 가짜 객체 생성

    @InjectMocks
    private KisAuthService kisAuthService;      // Mock을 주입해서 생성
}
```

`@MockitoSettings(strictness = Strictness.LENIENT)` — setUp에서 설정한 stub 중 특정 테스트에서 사용 안 해도 에러 안 남.  
기본값(STRICT_STUBS)은 사용하지 않은 stub을 에러로 처리한다.

---

## Mock vs Stub vs Verify

| 용어 | 의미 | 예시 |
|------|------|------|
| Mock | 가짜 객체 | `@Mock RestClient restClient` |
| Stub | Mock의 동작 정의 | `given(valueOperations.get(...)).willReturn(token)` |
| Verify | Mock이 호출됐는지 검증 | `verify(restClient, never()).post()` |

---

## RestClient 체이닝 Mock 방법

RestClient는 메서드 체이닝 구조라 각 단계마다 Mock이 필요하다.

```java
@Mock RestClient restClient;
@Mock RestClient.RequestBodyUriSpec requestBodyUriSpec;
@Mock RestClient.RequestBodySpec requestBodySpec;
@Mock RestClient.ResponseSpec responseSpec;

// setUp에서 체이닝 연결
given(restClient.post()).willReturn(requestBodyUriSpec);
given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
given(requestBodySpec.body(any())).willReturn(requestBodySpec);
given(requestBodySpec.retrieve()).willReturn(responseSpec);

// 테스트별로 최종 응답만 stub
given(responseSpec.body(KisTokenResponse.class)).willReturn(mockResponse);
```

---

## assertThatThrownBy — 예외 검증

```java
assertThatThrownBy(() -> kisAuthService.getAccessToken())
    .isInstanceOf(BusinessException.class)
    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
        .isEqualTo(ErrorCode.KIS_AUTH_FAILED));
```

`satisfies`는 예외 객체를 직접 꺼내서 필드까지 검증할 때 사용.
