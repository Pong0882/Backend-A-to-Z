# AES-256 암호화 + AttributeConverter 보안 설계

## AES란

AES(Advanced Encryption Standard) — 대칭키 암호화 알고리즘.
암호화와 복호화에 **같은 키**를 사용한다.

| 키 길이 | 이름 | 보안 수준 |
|--------|------|---------|
| 128bit | AES-128 | 충분 |
| 192bit | AES-192 | 높음 |
| **256bit** | **AES-256** | **가장 강함 (pong-to-rich 사용)** |

---

## ECB vs CBC — 운영 모드

AES는 데이터를 16바이트 블록으로 나눠서 암호화한다. 블록을 어떻게 연결하느냐가 운영 모드.

### ECB (Electronic Codebook) — 사용 금지

```
평문 블록1 → [AES] → 암호문 블록1
평문 블록2 → [AES] → 암호문 블록2
```

같은 평문 블록은 항상 같은 암호문 블록이 된다.
→ **패턴 분석 가능** — "이 암호문이 자주 나오면 이 평문일 것이다"

### CBC (Cipher Block Chaining) — pong-to-rich 사용

```
평문 블록1 XOR IV → [AES] → 암호문 블록1
평문 블록2 XOR 암호문 블록1 → [AES] → 암호문 블록2
```

앞 블록의 암호문이 다음 블록 암호화에 영향을 준다.
→ **같은 평문이라도 다른 암호문** (IV가 다르면)

---

## IV (초기화 벡터)

CBC의 첫 번째 블록은 XOR할 "이전 암호문"이 없다. 그 역할을 IV가 한다.

**핵심:** IV를 매번 랜덤으로 생성하면 같은 appkey를 두 사용자가 입력해도 DB에 저장되는 암호문이 다르다.

```java
byte[] iv = new byte[16];
new SecureRandom().nextBytes(iv);  // 매 암호화마다 랜덤
```

**IV 저장 방법:** 복호화할 때 같은 IV가 필요하므로 암호문과 함께 저장.
pong-to-rich는 `Base64(IV 16바이트 + 암호문)`을 하나의 문자열로 DB에 저장.

```
DB 저장값: Base64( [IV 16바이트] + [암호문] )
복호화 시: 앞 16바이트 분리 → IV 추출 → 나머지로 복호화
```

---

## 위협 모델 분석

AES-256으로 막을 수 있는 것과 없는 것을 명확히 구분해야 한다.

| 위협 시나리오 | AES-256 적용 후 |
|-------------|----------------|
| DB 파일 직접 탈취 | 암호문만 보임 ✅ |
| SQL Injection으로 DB 덤프 | 암호문만 보임 ✅ |
| 서버 전체 침투 (SSH 등) | ENCRYPT_SECRET_KEY도 노출 → 복호화 가능 ❌ |
| 서버 운영자(관리자) 열람 | 키를 알고 있으므로 복호화 가능 ❌ |
| 애플리케이션 메모리 덤프 | 복호화된 값이 메모리에 존재 ❌ |

**결론:** AES 서버 관리 키 방식은 "DB만 털렸을 때"를 막는 것이다.

---

## TODO — 실서비스 전환 시 교체 필요

### 옵션 1: 사용자 비밀번호 기반 암호화 (PBKDF2)

```
암호화 키 = PBKDF2(사용자 비밀번호 + salt)
→ 서버는 키를 저장하지 않음
→ 관리자도 복호화 불가
```

- 장점: 서버/관리자 열람 원천 차단
- 단점: 비밀번호 분실 시 appkey 영구 복호화 불가 → 재입력 UX 필요
- 단점: 비밀번호 변경 시 기존 암호문 전체 재암호화 필요

### 옵션 2: AWS KMS / HashiCorp Vault

```
암호화 요청 → KMS → KMS가 키 관리 (서버에 키 없음)
복호화 요청 → KMS → IAM 권한 검증 후 복호화
```

- 장점: 서버에 키가 없어서 서버 전체 침투에도 안전
- 장점: 키 로테이션, 접근 감사 로그 지원
- 단점: AWS 비용 발생, 인프라 복잡도 증가

---

## pong-to-rich에서 사용된 곳

### AesEncryptor.java

```java
// security/AesEncryptor.java
@Converter
@Component
public class AesEncryptor implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;

    private final SecretKeySpec secretKey;

    public AesEncryptor(@Value("${encrypt.secret-key}") String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("encrypt.secret-key must be exactly 32 bytes");
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }
    // convertToDatabaseColumn: 암호화
    // convertToEntityAttribute: 복호화
}
```

### BrokerAccount.java

```java
// appkey, appsecret에 @Convert 적용
@Convert(converter = AesEncryptor.class)
@Column(nullable = false, length = 500)  // Base64 암호문이 원문보다 길어서 500
private String appkey;
```

### application.yaml

```yaml
encrypt:
  secret-key: ${ENCRYPT_SECRET_KEY:local-dev-encrypt-key-32byte!!}
```

키는 반드시 32바이트. 운영 서버 `.env`의 `ENCRYPT_SECRET_KEY`로 주입.
