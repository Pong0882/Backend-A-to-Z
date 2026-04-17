# MySQL JSON 타입 — JPA에서 사용하는 법

## 왜 JSON 컬럼을 쓰는가

정형화되지 않은 데이터, 또는 자주 구조가 바뀌는 데이터를 저장할 때 쓴다.

**대안과 비교:**

| 방법 | 장점 | 단점 |
|------|------|------|
| 별도 테이블 분리 | 정규화, 인덱스 가능 | 테이블 수 증가, JOIN 비용 |
| VARCHAR로 JSON 문자열 | 단순 | DB가 내용을 모름, 유효성 검증 없음 |
| **JSON 타입** | DB가 구조 인식, `JSON_EXTRACT` 쿼리 가능 | 인덱스 제한적 |

## JPA에서 JSON 컬럼 매핑

```java
@Column(columnDefinition = "JSON")
private String params;
```

`columnDefinition = "JSON"` — JPA에게 이 컬럼의 DDL을 직접 지정.
Java에서는 `String`으로 받고, 실제 JSON 파싱은 ObjectMapper로 한다.

```java
// 저장 시
ObjectMapper objectMapper = new ObjectMapper();
String params = objectMapper.writeValueAsString(Map.of("period", 14, "threshold", 30));

StrategyCondition.builder()
    .indicator("RSI")
    .params(params)  // {"period":14,"threshold":30}
    .build();

// 조회 시
Map<String, Object> paramsMap = objectMapper.readValue(condition.getParams(), Map.class);
int period = (int) paramsMap.get("period");
```

## Hibernate Types 라이브러리 방법

`String` 대신 `Map`이나 `JsonNode`로 바로 매핑하고 싶으면 Hypersistence Utils 라이브러리를 쓴다.

```groovy
// build.gradle
implementation 'io.hypersistence:hypersistence-utils-hibernate-62:3.7.0'
```

```java
@Type(JsonBinaryType.class)
@Column(columnDefinition = "JSON")
private Map<String, Object> params;
```

pong-to-rich는 현재 `String` 방식으로 단순하게 처리. 추후 전략 조건이 복잡해지면 전환 검토.

## MySQL JSON 타입의 장점

```sql
-- JSON 내부 값으로 조회 가능
SELECT * FROM strategy_conditions
WHERE JSON_EXTRACT(params, '$.period') = 14;

-- JSON_VALUE (MySQL 8.0.21+)
SELECT JSON_VALUE(params, '$.threshold') FROM strategy_conditions;
```

## pong-to-rich에서 사용된 곳

```java
// StrategyCondition.java
// indicator VARCHAR + params JSON 혼합 방식 (2026-04-16 결정)
// — 지표명은 고정(indicator), 파라미터는 지표마다 구조가 달라서 JSON으로 유연하게 저장
@Column(nullable = false, length = 50)
private String indicator;  // ex. "RSI", "MACD", "MA"

@Column(columnDefinition = "JSON")
private String params;     // ex. {"period": 14, "threshold": 30}
```

RSI는 `period`와 `threshold`가 파라미터고, MACD는 `fast`, `slow`, `signal`이 파라미터다.
지표마다 파라미터 구조가 달라서 별도 컬럼으로 정규화하기 어렵고, JSON이 가장 유연한 선택이다.
