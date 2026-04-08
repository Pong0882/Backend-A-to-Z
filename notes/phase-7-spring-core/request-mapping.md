# 요청 매핑 어노테이션

## @RequestMapping

- URL 패턴과 HTTP 메서드를 매핑하는 기본 어노테이션
- 클래스 레벨에서 공통 prefix 지정, 메서드 레벨에서 세부 경로 지정

```java
@RestController
@RequestMapping("/api/kis")   // 모든 메서드의 공통 prefix
public class KisAuthController {

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public Map<String, String> getToken() { ... }
}
```

## @GetMapping / @PostMapping / @PutMapping / @DeleteMapping / @PatchMapping

- `@RequestMapping`의 HTTP 메서드별 단축 어노테이션
- 실무에서는 이것들을 사용 (`@RequestMapping`은 클래스 레벨 prefix 용도로만)

```java
@GetMapping("/token")       // GET /api/kis/token
@PostMapping("/order")      // POST /api/kis/order
@PutMapping("/order/{id}")  // PUT /api/kis/order/1
@DeleteMapping("/order/{id}") // DELETE /api/kis/order/1
@PatchMapping("/order/{id}")  // PATCH /api/kis/order/1
```

## @PathVariable

- URL 경로의 변수를 메서드 파라미터로 받음

```java
@GetMapping("/order/{orderId}")
public Order getOrder(@PathVariable Long orderId) { ... }
// GET /api/kis/order/123 → orderId = 123
```

## @RequestParam

- URL 쿼리 파라미터를 메서드 파라미터로 받음

```java
@GetMapping("/stocks")
public List<Stock> getStocks(@RequestParam String symbol) { ... }
// GET /api/stocks?symbol=005930 → symbol = "005930"
```

## @RequestBody

- HTTP 요청 Body(JSON)를 객체로 역직렬화
- Jackson이 JSON → Java 객체 변환

```java
@PostMapping("/order")
public Order createOrder(@RequestBody OrderRequest request) { ... }
// Body: {"symbol":"005930","quantity":10} → OrderRequest 객체
```

## RESTful URL 설계 원칙

| 행위 | HTTP 메서드 | URL 예시 |
|------|------------|----------|
| 목록 조회 | GET | `/api/orders` |
| 단건 조회 | GET | `/api/orders/{id}` |
| 생성 | POST | `/api/orders` |
| 전체 수정 | PUT | `/api/orders/{id}` |
| 부분 수정 | PATCH | `/api/orders/{id}` |
| 삭제 | DELETE | `/api/orders/{id}` |

## pong-to-rich에서

- `@RequestMapping("/api/kis")` — KIS API 관련 공통 prefix
- `@GetMapping("/token")` — 토큰 발급 확인용 엔드포인트
