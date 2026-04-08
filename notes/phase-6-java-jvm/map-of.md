# Map.of() / List.of() — 불변 컬렉션

## 개념

Java 9+에서 추가된 불변(Immutable) 컬렉션 생성 메서드.

```java
Map<String, String> map = Map.of(
    "key1", "value1",
    "key2", "value2"
);

List<String> list = List.of("a", "b", "c");
Set<String> set = Set.of("x", "y", "z");
```

## 특징

- **불변** — 생성 후 추가/삭제/수정 불가. 시도 시 `UnsupportedOperationException`
- **null 불허** — null 값 넣으면 `NullPointerException`
- **간결한 문법** — `new HashMap<>()` + `put()` 반복 불필요

## 기존 방식과 비교

```java
// 기존 방식
Map<String, String> map = new HashMap<>();
map.put("grant_type", "client_credentials");
map.put("appkey", appKey);

// Map.of() 방식
Map<String, String> map = Map.of(
    "grant_type", "client_credentials",
    "appkey", appKey
);
```

## 주의사항

- `Map.of()`는 최대 10개 키-값 쌍까지만 지원
- 10개 초과 시 `Map.ofEntries(Map.entry(...), ...)` 사용
- 순서 보장 안 됨 (순서 필요하면 `LinkedHashMap`)

## 불변 컬렉션 종류 비교

| 방법 | 특징 |
|------|------|
| `Map.of()` | 완전 불변, null 불허 |
| `Collections.unmodifiableMap()` | 원본 Map 래핑, 원본 변경 시 같이 변경됨 |
| `new HashMap<>()` | 가변, 자유롭게 수정 가능 |

## pong-to-rich에서 사용된 곳

`KisAuthService` — 한투 API 토큰 발급 요청 body 생성 시 사용
