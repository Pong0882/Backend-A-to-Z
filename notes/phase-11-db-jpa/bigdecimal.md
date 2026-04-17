# BigDecimal — 소수점 정밀도

## 왜 필요한가

Java에서 소수점 계산에 `double`이나 `float`을 쓰면 정밀도 손실이 발생한다.

```java
double a = 0.1;
double b = 0.2;
System.out.println(a + b);  // 0.30000000000000004 ← 오차 발생
```

이진수로 소수를 표현할 때 무한 소수가 되는 경우가 있어서 반올림 오차가 생긴다.
금융 데이터(주가, 잔액, 거래금액)에서 이 오차는 절대 허용 불가다.

## BigDecimal 사용법

```java
BigDecimal a = new BigDecimal("0.1");  // 문자열로 생성해야 정확함
BigDecimal b = new BigDecimal("0.2");
System.out.println(a.add(b));  // 0.3 ← 정확

// double로 생성하면 안 됨
BigDecimal bad = new BigDecimal(0.1);  // 0.1000000000000000055511151231... 저장됨
```

## 주요 메서드

```java
BigDecimal a = new BigDecimal("100.50");
BigDecimal b = new BigDecimal("30.25");

a.add(b);           // 130.75
a.subtract(b);      // 70.25
a.multiply(b);      // 3040.1250
a.divide(b, 4, RoundingMode.HALF_UP);  // 나눗셈은 반드시 scale과 반올림 모드 지정
a.compareTo(b);     // 0: 같음, 1: a > b, -1: a < b (equals 대신 compareTo 사용)
```

## JPA에서 DB 매핑

```java
@Column(precision = 12, scale = 4)
private BigDecimal closePrice;
```

- `precision` — 전체 자릿수 (정수 + 소수 합산)
- `scale` — 소수점 이하 자릿수

`DECIMAL(12, 4)` → 최대 `99999999.9999` 까지 저장 가능

## pong-to-rich 선택 기준

| 컬럼 | 타입 | 이유 |
|------|------|------|
| stock_prices 가격 | `DECIMAL(12,4)` | 미국 주식 소수점 대응 (ex. 182.5700) |
| holdings.average_price | `DECIMAL(12,4)` | 평균 매수가 소수점 발생 |
| orders.price | `DECIMAL(12,4)` | 지정가 주문 소수점 |
| broker_accounts.balance | `DECIMAL(15,2)` | 예수금 — 소수점 2자리면 충분, 금액 크므로 precision 높임 |

## pong-to-rich에서 사용된 곳

```java
// StockPrice.java
@Column(nullable = false, precision = 12, scale = 4)
private BigDecimal closePrice;

// StockService.java — Long.parseLong 대신 new BigDecimal 사용
stockPriceRepository.save(StockPrice.builder()
    .closePrice(new BigDecimal(daily.closePrice()))
    .build());
```
