# 메서드 체이닝 (Method Chaining)

## 개념

각 메서드가 자기 자신(또는 관련 객체)을 반환해서 `.` 으로 연속 호출하는 방식.

```java
// 메서드 체이닝 없이
RestClient client = RestClient.create();
RestClient.RequestBodySpec spec = client.post();
spec.uri("https://example.com");
spec.header("Content-Type", "application/json");
spec.body(requestBody);
RestClient.ResponseSpec response = spec.retrieve();
String result = response.body(String.class);

// 메서드 체이닝 사용
String result = RestClient.create()
        .post()
        .uri("https://example.com")
        .header("Content-Type", "application/json")
        .body(requestBody)
        .retrieve()
        .body(String.class);
```

## 왜 쓰는가

- 중간 변수 불필요 — 흐름이 코드에 그대로 드러남
- 가독성 — 무엇을 설정하고 무엇을 실행하는지 순서대로 읽힘
- 불변 객체 구성에 적합 — 각 단계에서 새 객체를 반환하므로 상태 변이 없음

## 동작 원리

각 메서드가 `return this` (또는 다음 단계 객체)를 반환하면 체이닝이 가능해진다.

```java
public class Builder {
    private String name;
    private int age;

    public Builder name(String name) {
        this.name = name;
        return this;  // ← 자기 자신 반환
    }

    public Builder age(int age) {
        this.age = age;
        return this;
    }
}

// 사용
new Builder().name("pong").age(30);
```

## pong-to-rich에서 사용된 곳

`KisAuthService` — RestClient로 한투 API 토큰 발급 요청 시 사용

```java
KisTokenResponse tokenResponse = restClient.post()
        .uri(kisConfig.baseUrl() + "/oauth2/tokenP")
        .header("Content-Type", "application/json")
        .body(requestBody)
        .retrieve()
        .body(KisTokenResponse.class);
```

각 메서드(`post()`, `uri()`, `header()`, `body()`, `retrieve()`)가 다음 단계 객체를 반환하면서 HTTP 요청을 단계적으로 구성한다.

## 빌더 패턴과의 관계

메서드 체이닝은 빌더 패턴의 핵심 구현 기법이다.
빌더 패턴은 "복잡한 객체를 단계별로 조립"하는 패턴이고, 메서드 체이닝은 그 조립 과정을 읽기 쉽게 만드는 문법적 수단이다.

→ [빌더 패턴](../../notes/phase-17-design-pattern/builder-pattern.md)
