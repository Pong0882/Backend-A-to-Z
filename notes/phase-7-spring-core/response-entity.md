# ResponseEntity

## 개념

- HTTP 응답 전체(상태코드 + 헤더 + body)를 직접 제어할 수 있는 Spring 제공 클래스
- `@RestController`에서 단순 객체를 반환하면 상태코드는 항상 200 — 세밀한 제어 불가
- `ResponseEntity`를 반환하면 상태코드, 헤더, body를 자유롭게 설정 가능

## 기본 사용법

```java
// 단순 객체 반환 — 상태코드 200 고정
@GetMapping("/health")
public Map<String, String> health() {
    return Map.of("status", "ok");
}

// ResponseEntity — 상태코드 직접 지정
@GetMapping("/health")
public ResponseEntity<Map<String, String>> health() {
    return ResponseEntity.ok(Map.of("status", "ok"));  // 200 OK
}
```

## 상태코드 지정 방법

```java
// 200 OK
ResponseEntity.ok(body);

// 201 Created — 리소스 생성 성공
ResponseEntity.status(HttpStatus.CREATED).body(body);

// 204 No Content — 응답 body 없음 (삭제 성공 등)
ResponseEntity.noContent().build();

// 400 Bad Request
ResponseEntity.badRequest().body(errorMessage);

// 404 Not Found
ResponseEntity.notFound().build();
```

## 헤더 추가

```java
HttpHeaders headers = new HttpHeaders();
headers.add("Custom-Header", "value");

return ResponseEntity.ok()
        .headers(headers)
        .body(body);
```

## 언제 쓰는가

| 상황 | 사용 여부 |
|------|----------|
| 단순 데이터 조회 (항상 200) | 단순 객체 반환으로 충분 |
| 생성 API (201 반환해야 함) | ResponseEntity 필요 |
| 조건에 따라 상태코드가 달라짐 | ResponseEntity 필요 |
| 응답 헤더 추가가 필요 | ResponseEntity 필요 |

## pong-to-rich에서

- 현재 `HealthController`, `KisAuthController`는 단순 200 응답 → 일반 객체 반환
- 주문 생성, 에러 응답 등 상태코드 제어가 필요한 API 구현 시 `ResponseEntity` 사용 예정
