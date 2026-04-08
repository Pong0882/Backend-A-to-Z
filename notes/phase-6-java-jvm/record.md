# Java record

## 개념

- Java 16에서 정식 도입된 불변 데이터 클래스 선언 문법
- `final` 필드 + 생성자 + getter + `equals()` + `hashCode()` + `toString()` 자동 생성

## 기존 방식 vs record

```java
// 기존 방식 — 보일러플레이트 코드가 많음
public final class KisConfig {
    private final String appKey;
    private final String appSecret;
    private final String baseUrl;

    public KisConfig(String appKey, String appSecret, String baseUrl) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.baseUrl = baseUrl;
    }

    public String appKey() { return appKey; }
    public String appSecret() { return appSecret; }
    public String baseUrl() { return baseUrl; }
    // equals, hashCode, toString 도 직접 작성...
}

// record — 한 줄로 동일한 효과
public record KisConfig(String appKey, String appSecret, String baseUrl) {}
```

## 특징

- 모든 필드는 자동으로 `private final` — 불변 보장
- getter는 `getXxx()` 가 아닌 `xxx()` 형식 (예: `appKey()`)
- 상속 불가 (`extends` 불가, `implements`는 가능)
- 생성 후 필드 변경 불가

## 언제 쓰는가

- **설정값 바인딩** — `@ConfigurationProperties`와 함께 (런타임 중 변경 불필요)
- **DTO** — API 요청/응답 데이터 전달 객체
- **Value Object** — 값 자체가 의미를 가지는 객체

## pong-to-rich에서

```java
@ConfigurationProperties(prefix = "kis")
public record KisConfig(
        String appKey,    // kis.app-key
        String appSecret, // kis.app-secret
        String baseUrl    // kis.base-url
) {}
```

- KIS API 설정값은 앱 시작 후 변경되지 않음 → record의 불변성이 적합
- `kisConfig.appKey()` 형태로 접근
