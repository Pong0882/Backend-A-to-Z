# @GeneratedValue — 기본키 생성 전략

## 4가지 전략

### AUTO (기본값)

```java
@GeneratedValue(strategy = GenerationType.AUTO)
```

JPA가 DB 방언(Dialect)에 맞게 자동 선택. MySQL이면 IDENTITY, Oracle이면 SEQUENCE.
예측이 어렵고 DB 바꿀 때 동작이 달라질 수 있어서 **명시적으로 지정하는 게 낫다.**

### IDENTITY

```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

DB의 AUTO_INCREMENT에 위임. INSERT 후 DB가 생성한 id를 반환받는다.

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ...
)
```

**MySQL에서는 IDENTITY를 쓴다.** pong-to-rich가 이 방식.

**주의**: IDENTITY 전략은 INSERT 전에 id를 알 수 없다. 그래서 `em.persist()` 시점에 즉시 INSERT 쿼리가 나간다 (쓰기 지연 버퍼링 불가).

### SEQUENCE

```java
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
@SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 50)
```

DB 시퀀스 객체를 사용. Oracle, PostgreSQL에서 주로 사용.
`allocationSize`로 시퀀스를 미리 여러 개 가져와서 INSERT마다 DB를 호출하지 않아도 됨 → 성능 유리.

MySQL은 시퀀스를 지원하지 않아서 사용 불가.

### TABLE

```java
@GeneratedValue(strategy = GenerationType.TABLE)
```

별도의 키 생성 테이블을 만들어서 관리. DB 독립적이지만 성능이 가장 나쁨. 거의 안 씀.

## 전략 비교

| 전략 | MySQL | 성능 | 권장 |
|------|-------|------|------|
| IDENTITY | ✅ | 보통 | MySQL이면 이걸 |
| SEQUENCE | ❌ | 좋음 (allocationSize) | Oracle/PostgreSQL이면 이걸 |
| TABLE | ✅ | 나쁨 | 거의 안 씀 |
| AUTO | ✅ | DB마다 다름 | 명시적 지정 권장 |

## id 타입은 왜 Long인가

```java
@Id
private Long id;
```

- `int` (32비트) — 최대 약 21억. 대용량 서비스에서 부족할 수 있음
- `Long` (64비트) — 최대 약 922경. 사실상 무한대
- `Integer` 대신 `Long`을 쓰는 게 실무 표준

## pong-to-rich에서 사용된 곳

모든 Entity에 `GenerationType.IDENTITY` 적용. MySQL AUTO_INCREMENT 사용.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
