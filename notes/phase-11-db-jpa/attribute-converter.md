# JPA AttributeConverter

## 개념

JPA Entity의 필드를 DB에 저장하거나 읽어올 때 **자동으로 변환**하는 인터페이스.
서비스 레이어 코드 변경 없이 저장 방식만 바꿀 수 있다.

```
Entity 필드값 → convertToDatabaseColumn() → DB 저장값
DB 저장값 → convertToEntityAttribute() → Entity 필드값
```

---

## 인터페이스 구조

```java
public interface AttributeConverter<X, Y> {
    Y convertToDatabaseColumn(X attribute);  // Entity → DB
    X convertToEntityAttribute(Y dbData);    // DB → Entity
}
```

- `X`: Entity 필드 타입
- `Y`: DB 컬럼 타입

---

## 언제 쓰나

| 상황 | 예시 |
|------|------|
| 암호화/복호화 | appkey → AES 암호문으로 저장 |
| 커스텀 직렬화 | List → JSON 문자열로 저장 |
| 단위 변환 | 원화(Long) → 달러(BigDecimal)로 저장 |
| 레거시 DB 매핑 | `"Y"/"N"` → `boolean`으로 매핑 |

---

## @Convert 적용 방법

### 방법 1 — 필드에 직접 선언 (pong-to-rich 사용)

```java
@Convert(converter = AesEncryptor.class)
@Column(nullable = false, length = 500)
private String appkey;
```

### 방법 2 — @Converter(autoApply = true)

Converter 클래스에 `autoApply = true`를 설정하면 해당 타입의 모든 필드에 자동 적용.

```java
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, Long> { ... }
```

같은 타입이 여러 Entity에 흩어져 있을 때 유용. 단, 의도치 않은 필드에도 적용될 수 있어서 명시적 선언을 선호하는 경우도 많다.

---

## Spring Bean 주입 주의점

`@Converter` + `@Component`를 함께 쓰면 Spring Bean으로 등록되어 `@Value`, `@Autowired` 주입이 가능하다.

```java
@Converter
@Component  // Spring Bean으로 등록 → @Value 주입 가능
public class AesEncryptor implements AttributeConverter<String, String> {

    private final SecretKeySpec secretKey;

    public AesEncryptor(@Value("${encrypt.secret-key}") String key) {
        this.secretKey = new SecretKeySpec(key.getBytes(), "AES");
    }
}
```

`@Component` 없이 `@Converter`만 쓰면 JPA가 직접 인스턴스를 생성 → `@Value` 주입 안 됨 → 키를 외부에서 못 받음.

---

## 서비스 레이어 투명성

AttributeConverter의 핵심 장점 — **서비스/컨트롤러 코드가 변환을 전혀 모른다.**

```java
// BrokerAccountService — 암호화 코드 없음. 그냥 문자열로 다룸
BrokerAccount account = BrokerAccount.builder()
    .appkey(request.getAppkey())   // 평문 그대로 넣음
    .appsecret(request.getAppsecret())
    .build();
brokerAccountRepository.save(account);  // JPA가 저장 시 자동 암호화
```

나중에 암호화 방식을 바꿔야 할 때도 `AesEncryptor`만 수정하면 된다.

---

## pong-to-rich에서 사용된 곳

- `security/AesEncryptor.java` — `String → AES-256 암호문(Base64)` 변환
- `domain/broker/BrokerAccount.java` — appkey, appsecret 필드에 `@Convert(converter = AesEncryptor.class)` 적용
