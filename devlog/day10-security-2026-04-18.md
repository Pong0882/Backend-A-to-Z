# Day 11 — 2026-04-18 (2)

> day10-2026-04-18.md에서 분리. day10은 KisAuthService Redis 캐싱 + TDD + 버그 수정.
> 이 파일은 Issue #20 appkey 암호화 + Issue #37 StockCandle Entity 작업.

---

## 오늘 한 것

- Issue #20 — BrokerAccount appkey/appsecret AES-256 암호화 적용
- Issue #37 — StockCandle Entity + Repository 구현 (분봉/시간봉 전용 테이블)
- 보안 설계 논의 — AES 서버 관리 키 방식의 한계 및 향후 TODO 정리

---

## 구현 — Issue #20 appkey/appsecret AES-256 암호화

### 배경 및 선택 이유

BrokerAccount의 appkey, appsecret이 DB에 평문으로 저장되어 있었다.
DB가 유출될 경우 사용자의 KIS API 키가 그대로 노출되는 문제.

**선택지:**
- Jasypt — application.yml 설정값 암호화 특화. DB 컬럼 암호화엔 어색함
- JPA AttributeConverter + AES-256 — DB 컬럼 단위 암복호화. 외부 의존성 없이 JDK 내장 `javax.crypto` 사용

→ **AttributeConverter 방식 선택.** `@Convert` 어노테이션 하나로 Entity에 선언적으로 적용 가능하고, 서비스 레이어 코드 변경 없이 투명하게 동작.

### AesEncryptor 구현

파일: `security/AesEncryptor.java`

**AES/CBC/PKCS5Padding 방식 선택 이유:**
- ECB 모드는 같은 평문이 항상 같은 암호문 → 패턴 분석 가능
- CBC 모드는 앞 블록 암호문이 다음 블록에 영향 → 패턴 노출 방지
- IV(초기화 벡터)를 매 암호화마다 랜덤 생성 → 같은 appkey도 저장할 때마다 다른 암호문

**저장 포맷:** `Base64(IV(16바이트) + 암호문)`
- IV를 암호문 앞에 붙여서 같이 저장 → 복호화 시 분리해서 사용
- DB 컬럼 한 칸에 담기므로 별도 IV 컬럼 불필요

```java
// 암호화: 랜덤 IV 생성 → 암호화 → IV+암호문 합쳐서 Base64 저장
byte[] iv = new byte[16];
new SecureRandom().nextBytes(iv);
cipher.init(ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
byte[] encrypted = cipher.doFinal(plainText.getBytes(UTF_8));

byte[] combined = new byte[16 + encrypted.length];
System.arraycopy(iv, 0, combined, 0, 16);
System.arraycopy(encrypted, 0, combined, 16, encrypted.length);
return Base64.getEncoder().encodeToString(combined);

// 복호화: Base64 디코딩 → IV(앞 16바이트) 분리 → 복호화
byte[] combined = Base64.getDecoder().decode(cipherText);
byte[] iv = Arrays.copyOf(combined, 16);
byte[] encrypted = Arrays.copyOfRange(combined, 16, combined.length);
cipher.init(DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
return new String(cipher.doFinal(encrypted), UTF_8);
```

**키 관리:** 32바이트(AES-256) 필수. `.env`의 `ENCRYPT_SECRET_KEY`에서 주입.
키가 32바이트가 아니면 생성자에서 즉시 예외 발생 — 잘못된 키로 서버가 뜨는 것 방지.

### BrokerAccount 변경

```java
// 변경 전
@Column(nullable = false, length = 200)
private String appkey;

// 변경 후 — AES 암호화 적용, Base64 문자열이 원문보다 길어서 length 200 → 500
@Convert(converter = AesEncryptor.class)
@Column(nullable = false, length = 500)
private String appkey;
```

서비스 레이어(`BrokerAccountService`)는 변경 없음. JPA가 저장/조회 시 자동으로 암복호화.

### application.yaml 추가

```yaml
encrypt:
  secret-key: ${ENCRYPT_SECRET_KEY:local-dev-encrypt-key-32byte!!}
```

로컬 개발용 기본값 32바이트로 맞춤. 운영 서버 `.env`에 `ENCRYPT_SECRET_KEY` 별도 설정 필요.

### 보안 수준 논의 — AES 서버 관리 키 방식의 한계

작업 중 "관리자(운영자)도 복호화할 수 있는 거 아니냐"는 질문에서 나온 논의.

**현재 방식으로 막을 수 있는 위협:**
- DB 파일 직접 탈취 / SQL Injection으로 DB 덤프 → 암호문만 보임 ✅

**현재 방식으로 막을 수 없는 위협:**
- 서버 전체 침투 시 `ENCRYPT_SECRET_KEY` 환경변수도 함께 노출 → 복호화 가능 ❌
- 서버 운영자(나)가 키를 알고 있어서 원칙적으로 복호화 가능 ❌

**판단:** 현재 MVP/학습 단계에서는 AES로 "DB 유출" 위협을 막는 것으로 충분.
실서비스 전환 시 아래 TODO 처리 필요 — `AesEncryptor.java` 주석에도 동일하게 기록해둠.

```
TODO: 실서비스 전환 시 교체 필요
  1. 사용자 비밀번호 기반 암호화 (PBKDF2)
     - 암호화 키를 사용자 PW에서 파생 → 관리자도 복호화 불가
     - 단점: 비밀번호 분실 시 appkey 영구 복호화 불가 → 재입력 유도 UX 필요
  2. AWS KMS / HashiCorp Vault
     - 키를 외부 서비스에서 관리 → 서버에 키 없음, IAM으로 접근 제어
     - 비용 발생, 인프라 복잡도 증가
```

---

## 구현 — Issue #37 StockCandle Entity

### 배경 및 분리 이유

`stock_prices` 테이블은 `trade_date DATE` 타입 — 일봉 전용 구조.
분봉/시간봉을 같은 테이블에 넣으면:

- 삼성전자 15분봉 1년치만 약 6,500건 → 종목 수 × 봉 종류 늘면 수억 건
- 일봉 단순 조회도 인덱스 범위가 넓어져 느려짐
- 파티셔닝 전략을 봉 종류별로 다르게 가져가야 할 수도 있음

→ `stock_candles` 테이블로 완전 분리.

### Interval Enum 선택 이유

`interval` 컬럼을 `String`이 아닌 `Enum`으로 관리:
- `"1M"`, `"15m"` 같은 오타나 잘못된 값이 DB에 적재되는 것을 컴파일 타임에 방지
- 코드에서 `Interval.FIFTEEN_MIN`으로 의미 명확

```java
public enum Interval {
    ONE_MIN, THREE_MIN, FIVE_MIN, FIFTEEN_MIN, THIRTY_MIN, ONE_HOUR, FOUR_HOUR
}
```

### trade_time 타입

`StockPrice`의 `tradeDate`는 `LocalDate` (날짜만).
`StockCandle`의 `tradeTime`은 `LocalDateTime` (날짜 + 시간) — 분봉은 시간까지 필요.

### UNIQUE 제약

```java
uniqueConstraints = @UniqueConstraint(columnNames = {"stock_id", "interval", "trade_time"})
```

같은 종목의 같은 봉 종류에서 같은 시각 데이터 중복 적재 방지.

### Repository 쿼리 메서드

```java
// 종목 + 봉 종류 전체 조회 (최신순)
findByStockAndIntervalOrderByTradeTimeDesc(Stock, Interval)

// 기간 범위 조회 (차트 조회 시 사용)
findByStockAndIntervalAndTradeTimeBetweenOrderByTradeTimeDesc(Stock, Interval, from, to)
```

### 분봉 데이터 수집 방법

pykrx는 분봉 과거 데이터 미지원.
→ 한투 API 실시간 웹소켓으로 수집 예정 (별도 이슈로 관리).
현재 이슈 #37은 Entity/테이블 구조만 잡는 것으로 범위 한정.

---

## 깨달은 점

- `AttributeConverter`는 서비스 레이어를 건드리지 않고 DB 저장 방식만 바꿀 수 있어서 암호화 적용에 이상적인 위치다. 관심사 분리가 잘 된 구조.
- AES 암호화는 "DB만 털렸을 때"를 막는 것이고, 서버 전체가 털리면 키도 같이 털린다는 한계를 명확히 인식하고 TODO로 남겨두는 것이 중요하다. 모든 보안 조치는 위협 모델을 전제로 평가해야 한다.
- `stock_candles` 분리 결정은 지금 당장 분봉 기능이 없어도 맞는 판단이다. 나중에 테이블 분리하려면 마이그레이션 비용이 크다.

---

## 다음에 할 것

- feat/issue-20-37-security-candle → main PR 머지
- Issue #38 로그인 프론트 페이지 구현
- Issue #39 OAuth2 소셜 로그인 백엔드
- Issue #32 KIS API 실제 주문 전송 연동
- 서버 타임존 UTC → KST 수정
