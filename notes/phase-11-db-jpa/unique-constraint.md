# 복합 UNIQUE 제약 (@UniqueConstraint)

## 단일 vs 복합 UNIQUE

```java
// 단일 컬럼 UNIQUE — @Column에서 처리
@Column(nullable = false, unique = true)
private String email;

// 복합 컬럼 UNIQUE — @Table에서 처리
@Table(
    name = "stocks",
    uniqueConstraints = @UniqueConstraint(columnNames = {"code", "market"})
)
```

단일은 `@Column(unique = true)`, 복합은 반드시 `@Table`의 `uniqueConstraints` 사용.

## 복합 UNIQUE를 쓰는 이유

**예시: stocks 테이블**

```
code = "005930", market = "KRX"    → 삼성전자 (한국)
code = "005930", market = "NASDAQ" → 이론상 다른 종목
```

code 단독으로는 UNIQUE를 걸 수 없다. (code, market) 조합이 유니크해야 한다.

**예시: broker_accounts 테이블**

```
user_id = 1, broker = "KIS", account_type = "MOCK"  → 1번 유저의 KIS 모의투자 계좌
user_id = 1, broker = "KIS", account_type = "REAL"  → 1번 유저의 KIS 실투자 계좌
```

같은 유저가 같은 증권사에 모의/실투자 계좌를 각각 하나씩만 가질 수 있다.

## 여러 개 복합 UNIQUE 설정

```java
@Table(
    name = "example",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_code_market", columnNames = {"code", "market"}),
        @UniqueConstraint(name = "uk_name", columnNames = {"name"})
    }
)
```

`name`을 지정하면 DB 제약 이름이 명시적으로 설정된다. 에러 메시지에서 어떤 UNIQUE가 위반됐는지 알 수 있어서 실무에서 권장.

## 위반 시 예외

복합 UNIQUE 위반 시 `DataIntegrityViolationException` 발생. GlobalExceptionHandler에서 잡거나, 저장 전 `existsByXxx` 로 미리 체크하는 게 일반적.

```java
// 저장 전 체크
if (brokerAccountRepository.existsByUserAndBrokerAndAccountType(user, broker, accountType)) {
    throw new BusinessException(ErrorCode.DUPLICATE_BROKER_ACCOUNT);
}
```

## pong-to-rich에서 사용된 곳

| Entity | 복합 UNIQUE |
|--------|------------|
| `Stock` | `(code, market)` |
| `OAuthAccount` | `(provider, provider_id)` |
| `BrokerAccount` | `(user_id, broker, account_type)` |
| `Watchlist` | `(user_id, stock_id)` |
| `StockPrice` | `(stock_id, trade_date)` |
