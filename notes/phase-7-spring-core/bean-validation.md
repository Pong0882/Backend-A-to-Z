# Bean Validation — @Valid, @NotBlank, MethodArgumentNotValidException

## 왜 필요한가

Controller에서 요청 데이터를 받을 때 값이 비어있거나 형식이 잘못된 경우를 걸러야 한다.

```java
// 검증 없으면 email이 null이어도 서비스 로직까지 진행됨
public void signUp(SignUpRequest request) {
    userRepository.findByEmail(request.getEmail()); // NPE 또는 잘못된 데이터 저장
}
```

매번 서비스 레이어에서 `if (email == null || email.isEmpty())` 체크하는 것은 반복 코드다.
Bean Validation을 쓰면 DTO 선언부에서 한 번에 처리할 수 있다.

---

## 의존성 추가

```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

Spring Boot Starter Web에는 포함되지 않으므로 별도 추가 필요.

---

## 주요 어노테이션

| 어노테이션 | 설명 | 예시 |
|-----------|------|------|
| `@NotNull` | null 불가 | 객체, Enum |
| `@NotBlank` | null + 빈 문자열 + 공백만 있는 경우 불가 | String |
| `@NotEmpty` | null + 빈 문자열 불가 (공백은 허용) | String, Collection |
| `@Email` | 이메일 형식 검증 | String |
| `@Size(min, max)` | 문자열/컬렉션 길이 범위 | String |
| `@Min(value)` | 최솟값 | Integer, Long |
| `@Max(value)` | 최댓값 | Integer, Long |
| `@Pattern(regexp)` | 정규식 검증 | String |

---

## DTO에 어노테이션 적용

```java
@Getter
public class SignUpRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하로 입력해주세요.")
    private String nickname;
}
```

---

## Controller에서 @Valid 적용

```java
@PostMapping("/signup")
public ResponseEntity<ApiResult<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
    authService.signUp(request);
    return ResponseEntity.ok(ApiResult.ok());
}
```

`@Valid`가 없으면 DTO의 어노테이션이 무시된다. 반드시 붙여야 한다.

---

## 검증 실패 시 흐름

```
클라이언트 요청
    ↓
@Valid 검증 수행
    ↓
검증 실패 → MethodArgumentNotValidException 발생
    ↓
GlobalExceptionHandler에서 처리
    ↓
400 Bad Request + 오류 메시지 반환
```

---

## GlobalExceptionHandler 처리

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
    log.warn("[ValidationException] {}", message);
    return ResponseEntity
            .status(400)
            .body(new ErrorResponse("VALIDATION_ERROR", message));
}
```

- `getBindingResult().getFieldErrors()` — 실패한 필드 전체 목록
- `getDefaultMessage()` — `message = "..."` 에 작성한 메시지
- 여러 필드가 동시에 실패하면 `, `로 이어서 반환

---

## @NotBlank vs @NotNull vs @NotEmpty 차이

```
값          @NotNull  @NotEmpty  @NotBlank
null          ❌        ❌         ❌
""            ✅        ❌         ❌
"  " (공백)   ✅        ✅         ❌
"hello"       ✅        ✅         ✅
```

String 필드에는 거의 항상 `@NotBlank`를 쓴다. null과 공백 모두 차단하기 때문.

---

## pong-to-rich에서 사용된 곳

`SignUpRequest.java`, `LoginRequest.java` — 회원가입/로그인 요청 검증

```java
@NotBlank(message = "이메일을 입력해주세요.")
@Email(message = "올바른 이메일 형식이 아닙니다.")
private String email;
```

`GlobalExceptionHandler.java` — 검증 실패 시 400 반환

`BrokerAccountCreateRequest`, `StrategyCreateRequest`, `OrderCreateRequest` — 각 도메인 API 요청 검증
