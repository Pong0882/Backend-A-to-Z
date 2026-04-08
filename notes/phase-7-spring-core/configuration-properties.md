# @ConfigurationProperties vs @Value

## @Value

- yml/properties의 단일 값을 필드에 직접 주입
- SpEL(Spring Expression Language) 사용 가능

```java
@Service
public class SomeService {

    @Value("${kis.app-key}")
    private String appKey;

    @Value("${kis.base-url:https://default.url}")  // 기본값 지정 가능
    private String baseUrl;
}
```

**단점:**
- 관련 설정이 여러 클래스에 흩어짐
- 오타가 있어도 컴파일 타임에 잡히지 않음 (런타임 에러)
- 테스트 시 주입하기 불편

## @ConfigurationProperties

- yml의 특정 prefix 하위 설정을 **하나의 클래스에 묶어서** 바인딩
- 관련 설정을 한 곳에서 관리 → 응집도 높음
- 컴파일 타임 타입 체크 가능

```yaml
# application-local.yml
kis:
  app-key: ${KIS_APP_KEY}
  app-secret: ${KIS_APP_SECRET}
  base-url: https://openapivts.koreainvestment.com:29443
```

```java
@ConfigurationProperties(prefix = "kis")
public record KisConfig(
        String appKey,      // kis.app-key → camelCase 자동 변환
        String appSecret,   // kis.app-secret → camelCase 자동 변환
        String baseUrl      // kis.base-url → camelCase 자동 변환
) {}
```

## @EnableConfigurationProperties

- `@ConfigurationProperties` 클래스를 Spring Bean으로 등록
- 메인 클래스 또는 `@Configuration` 클래스에 선언

```java
@SpringBootApplication
@EnableConfigurationProperties(KisConfig.class)
public class PongToRichApplication { ... }
```

> 대안: `@ConfigurationProperties` 클래스에 `@Component`를 붙여도 되지만,
> `@EnableConfigurationProperties`를 쓰는 게 더 명시적이고 테스트하기 쉬움

## 비교 정리

| 항목 | @Value | @ConfigurationProperties |
|------|--------|--------------------------|
| 대상 | 단일 값 | 관련 설정 묶음 |
| 위치 | 주입받는 클래스 내부 | 별도 설정 클래스 |
| 타입 안전성 | 낮음 (런타임 에러) | 높음 (컴파일 타임 체크) |
| 응집도 | 낮음 (여기저기 흩어짐) | 높음 (한 곳에서 관리) |
| 테스트 | 불편 | 편리 (생성자로 직접 주입) |
| 추천 상황 | 단순한 값 1~2개 | 관련 설정이 3개 이상 |

## pong-to-rich에서

- KIS API 설정(appKey, appSecret, baseUrl)이 3개 → `@ConfigurationProperties` 적합
- `KisConfig`를 record로 선언 → 불변 객체, 설정값은 런타임 중 바뀔 일 없음
