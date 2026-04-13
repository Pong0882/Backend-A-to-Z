# 공통 응답 포맷 (ApiResult)

## 왜 필요한가

공통 응답 포맷 없이 API를 만들면 각 엔드포인트마다 응답 구조가 제각각이 된다.

```json
// login 응답
{ "accessToken": "...", "refreshToken": "..." }

// fetch 응답
{ "stockCode": "005930", "savedCount": 100 }

// 에러 응답
{ "code": "INVALID_TOKEN", "message": "유효하지 않은 토큰입니다." }
```

프론트엔드 입장에서는 API마다 응답 구조를 다르게 파싱해야 하고, 성공/실패 여부를 HTTP 상태코드로만 판단해야 한다.

공통 포맷을 적용하면 **항상 같은 구조**로 내려간다.

```json
// 성공 (데이터 있음)
{ "success": true, "data": { "accessToken": "...", "refreshToken": "..." } }

// 성공 (데이터 없음 — 로그아웃, 삭제)
{ "success": true }

// 에러 — ErrorResponse가 별도로 처리 (GlobalExceptionHandler)
{ "code": "INVALID_TOKEN", "message": "유효하지 않은 토큰입니다." }
```

## ApiResult 구현

```java
@JsonInclude(JsonInclude.Include.NON_NULL)  // data가 null이면 필드 자체를 응답에서 제외
public record ApiResult<T>(
        boolean success,
        T data
) {
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(true, data);
    }

    public static ApiResult<Void> ok() {
        return new ApiResult<>(true, null);
    }
}
```

**제네릭 `<T>`를 쓰는 이유:**
각 API마다 `data`에 들어가는 타입이 다르다. `TokenResponse`, `FetchResult` 등 어떤 타입이든 감쌀 수 있도록 제네릭으로 설계한다.

**`@JsonInclude(NON_NULL)`을 쓰는 이유:**
로그아웃처럼 반환할 데이터가 없을 때 `ApiResult.ok()`를 쓰면 `data`가 `null`이다. 이 어노테이션 없이는 `{ "success": true, "data": null }` 로 내려가지만, 붙이면 `{ "success": true }` 로 깔끔하게 내려간다.

## Controller 적용 패턴

```java
// 데이터 있는 응답
public ResponseEntity<ApiResult<TokenResponse>> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(ApiResult.ok(authService.login(request)));
}

// 데이터 없는 응답
public ResponseEntity<ApiResult<Void>> logout(Authentication authentication) {
    authService.logout(authentication.getName());
    return ResponseEntity.ok(ApiResult.ok());
}
```

## 에러 응답은 별도 처리

성공 응답은 `ApiResult`, 에러 응답은 `ErrorResponse`로 분리되어 있다.
`GlobalExceptionHandler`가 `BusinessException`을 잡아서 `ErrorResponse`를 반환한다.

```
성공 → ApiResult   { "success": true, "data": { ... } }
실패 → ErrorResponse  { "code": "...", "message": "..." }
```

에러 응답에 `success: false`를 넣지 않는 이유 — HTTP 상태코드(4xx/5xx)가 이미 실패를 나타내고 있어서 중복이다. `ErrorResponse`는 **왜 실패했는지**에 집중한다.

## 네이밍 주의

처음에 `ApiResponse`로 만들었다가 Swagger 어노테이션 `@ApiResponse`(`io.swagger.v3.oas.annotations.responses.ApiResponse`)와 이름이 충돌해서 컴파일 에러가 발생했다. `ApiResult`로 변경해서 해결.

실무에서 자주 쓰는 이름: `ApiResult`, `ApiResponse`, `CommonResponse`, `BaseResponse`, `Result`

## pong-to-rich에서 사용된 곳

- `common/ApiResult.java` — 공통 응답 래퍼
- `AuthController` — signup / login / refresh / logout 전체 적용
- `StockController` — getStockPrice / fetchDailyPrices 전체 적용
