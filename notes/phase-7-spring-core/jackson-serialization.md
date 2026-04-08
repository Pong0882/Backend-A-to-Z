# Jackson 직렬화

## 개념

- **직렬화(Serialization)**: Java 객체 → JSON 문자열
- **역직렬화(Deserialization)**: JSON 문자열 → Java 객체
- Spring Boot는 Jackson을 기본 JSON 라이브러리로 자동 설정

## Spring에서 언제 동작하는가

```
@RestController 메서드가 객체를 반환
    ↓
Jackson ObjectMapper가 객체를 JSON으로 변환 (직렬화)
    ↓
HTTP 응답 Body에 JSON 문자열로 전송

@RequestBody가 붙은 파라미터
    ↓
HTTP 요청 Body의 JSON 문자열을 Jackson이 객체로 변환 (역직렬화)
```

## 기본 동작 규칙

```java
public class Stock {
    private String symbol;      // → "symbol"
    private String companyName; // → "companyName" (camelCase 유지)
    private int price;          // → "price"
}
// {"symbol":"005930","companyName":"삼성전자","price":75000}
```

## 주요 어노테이션

```java
// 필드명을 JSON 키로 변경
@JsonProperty("company_name")
private String companyName;
// → "company_name": "삼성전자"

// 직렬화 시 해당 필드 제외
@JsonIgnore
private String password;

// null 값인 필드 제외
@JsonInclude(JsonInclude.Include.NON_NULL)
private String optionalField;

// 날짜 포맷 지정
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime createdAt;
```

## ObjectMapper 직접 사용

```java
ObjectMapper mapper = new ObjectMapper();

// 객체 → JSON 문자열
String json = mapper.writeValueAsString(stock);

// JSON 문자열 → 객체
Stock stock = mapper.readValue(json, Stock.class);
```

## Map을 반환하면 왜 JSON이 되는가

```java
@GetMapping("/health")
public Map<String, String> health() {
    return Map.of("status", "ok");
}
```

- `Map<String, String>` → Jackson이 key-value를 JSON 오브젝트로 변환
- `{"status":"ok"}` 로 응답

## pong-to-rich에서

- `KisAuthService`에서 한투 API 응답을 `Map<String, Object>`로 받아서 파싱
- 앞으로 DTO 클래스 만들면 Jackson이 자동으로 JSON ↔ 객체 변환
