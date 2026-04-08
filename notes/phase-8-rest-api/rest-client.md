# RestClient (Spring 6.1+)

## 개념

- Spring 6.1 (Boot 3.2+) 에서 도입된 동기 HTTP 클라이언트
- 기존 `RestTemplate`을 대체하는 최신 API — 더 직관적인 fluent 인터페이스
- 동기(Blocking) 방식 — 응답이 올 때까지 스레드가 대기

## RestTemplate vs RestClient vs WebClient

| 항목 | RestTemplate | RestClient | WebClient |
|------|-------------|------------|-----------|
| 도입 | Spring 3 | Spring 6.1 | Spring 5 |
| 방식 | 동기 | 동기 | 비동기 (Reactive) |
| 상태 | Deprecated 예정 | 현재 권장 | Reactive 환경 권장 |
| API 스타일 | 메서드 기반 | Fluent 체이닝 | Fluent 체이닝 |

## 기본 사용법

```java
RestClient restClient = RestClient.create();

// GET 요청
String result = restClient.get()
        .uri("https://api.example.com/data")
        .retrieve()
        .body(String.class);

// POST 요청 (JSON body)
Map<String, String> requestBody = Map.of("key", "value");

Map response = restClient.post()
        .uri("https://api.example.com/data")
        .header("Content-Type", "application/json")
        .body(requestBody)
        .retrieve()
        .body(Map.class);
```

## 에러 처리

```java
restClient.get()
        .uri("https://api.example.com/data")
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
            throw new RuntimeException("클라이언트 에러: " + response.getStatusCode());
        })
        .body(String.class);
```

## pong-to-rich에서

```java
// KisAuthService — 한투 토큰 발급
Map<String, Object> response = restClient.post()
        .uri(kisConfig.baseUrl() + "/oauth2/tokenP")
        .header("Content-Type", "application/json")
        .body(requestBody)
        .retrieve()
        .body(Map.class);
```

- 한투 API는 동기 호출로 충분 → `RestClient` 사용
- 나중에 여러 API를 동시에 호출해야 하면 `WebClient`로 전환 검토
