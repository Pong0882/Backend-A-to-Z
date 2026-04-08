# 빌더 패턴 (Builder Pattern)

## 개념

복잡한 객체를 단계별로 조립하는 생성 패턴.
생성자에 파라미터가 많아질 때, 또는 선택적 파라미터가 많을 때 사용한다.

## 왜 필요한가

생성자 파라미터가 많아지면 어떤 값이 어떤 필드인지 알기 어렵다.

```java
// 생성자 방식 — 파라미터 순서를 외워야 함
new User("pong", "pong@example.com", 30, "Seoul", true, false);

// 빌더 패턴 — 각 값이 무엇인지 명확
User user = User.builder()
        .name("pong")
        .email("pong@example.com")
        .age(30)
        .city("Seoul")
        .active(true)
        .build();
```

## 구현 방식

### 직접 구현

```java
public class User {
    private final String name;
    private final String email;

    private User(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
    }

    public static class Builder {
        private String name;
        private String email;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
```

### Lombok @Builder

```java
@Builder
public class User {
    private String name;
    private String email;
    private int age;
}

// 사용
User user = User.builder()
        .name("pong")
        .email("pong@example.com")
        .age(30)
        .build();
```

## pong-to-rich에서 사용된 곳

`KisAuthService` — RestClient 요청 구성

RestClient 자체가 빌더 패턴으로 설계되어 있다.
`post()` → `uri()` → `header()` → `body()` → `retrieve()` 순서로 HTTP 요청 객체를 단계별로 조립한 뒤 `body()` 로 최종 실행한다.

```java
KisTokenResponse tokenResponse = restClient.post()
        .uri(kisConfig.baseUrl() + "/oauth2/tokenP")
        .header("Content-Type", "application/json")
        .body(requestBody)
        .retrieve()
        .body(KisTokenResponse.class);
```

## 메서드 체이닝과의 관계

빌더 패턴의 각 메서드는 `return this` 로 자기 자신을 반환한다.
이것이 메서드 체이닝을 가능하게 하는 원리다.

→ [메서드 체이닝](../phase-6-java-jvm/method-chaining.md)
