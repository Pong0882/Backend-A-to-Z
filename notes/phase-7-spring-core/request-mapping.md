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

## @RequestBody vs @RequestParam vs @PathVariable

세 어노테이션은 **데이터를 어디서 꺼내느냐**가 다르다.

### HTTP 요청 구조

```
POST /api/stocks/005930/fetch?startDate=20240101&endDate=20241231
Header: Authorization: Bearer eyJ...
                                         ← 빈 줄
Body: { "memo": "테스트" }
```

| 어노테이션 | 데이터 위치 | HTTP 메서드 |
|-----------|-----------|------------|
| `@PathVariable` | URL 경로 (`/005930`) | GET / POST / PUT / DELETE 모두 |
| `@RequestParam` | URL 쿼리스트링 (`?startDate=...`) | 주로 GET |
| `@RequestBody` | HTTP Body (JSON) | POST / PUT |

### @PathVariable — 리소스 식별자

"어떤 리소스인지"를 특정할 때 사용한다.

```java
@GetMapping("/{stockCode}")
public ResponseEntity<?> getStockPrice(@PathVariable String stockCode) { ... }
// GET /api/stocks/005930 → stockCode = "005930"
```

### @RequestParam — 조회 조건 / 필터

GET 요청에서 검색 조건, 필터, 페이징 등 부가 정보를 전달할 때 사용한다.

```java
@PostMapping("/{stockCode}/fetch")
public ResponseEntity<?> fetch(
        @PathVariable String stockCode,
        @RequestParam String startDate,
        @RequestParam String endDate) { ... }
// POST /api/stocks/005930/fetch?startDate=20240101&endDate=20241231
```

`required = false` + `defaultValue`로 선택적 파라미터도 처리 가능하다.

```java
@RequestParam(required = false, defaultValue = "20") int size
```

### @RequestBody — 민감하거나 복잡한 데이터

POST / PUT 요청에서 JSON body를 객체로 받을 때 사용한다. Jackson이 JSON → Java 객체로 역직렬화한다.

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) { ... }
// Body: { "email": "pong@test.com", "password": "1234" }
```

**@RequestParam 대신 @RequestBody를 쓰는 이유:**
- URL에 담으면 서버 접근 로그에 그대로 찍힘 (`?password=1234`)
- 브라우저 히스토리, 프록시 서버 로그에도 노출될 수 있음
- Body는 HTTPS 암호화 범위에 포함되어 안전

### 세 가지를 동시에 쓰는 경우

```java
// PUT /api/orders/42?notify=true  + Body: { "quantity": 5 }
@PutMapping("/orders/{orderId}")
public ResponseEntity<?> updateOrder(
        @PathVariable Long orderId,         // 경로: 어떤 주문인지
        @RequestParam boolean notify,        // 쿼리: 알림 여부
        @RequestBody OrderUpdateRequest req) // body: 수정할 내용
{ ... }
```

## pong-to-rich에서 사용된 곳

- `@PathVariable String stockCode` — `StockController.getStockPrice()` / `fetchDailyPrices()`
- `@RequestParam String startDate, endDate` — `StockController.fetchDailyPrices()`
- `@RequestBody LoginRequest` — `AuthController.login()`
- `@RequestBody SignUpRequest` — `AuthController.signUp()`
