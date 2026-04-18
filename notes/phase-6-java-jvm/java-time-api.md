# Java 8 시간 API — LocalDateTime / ChronoUnit

## 왜 Java 8에서 시간 API가 바뀌었나

Java 8 이전에는 `Date`와 `Calendar`를 썼다.

```java
// 구식 — Date
Date now = new Date();
Date expiredAt = new Date(now.getTime() + 86400000); // 1일 후, 밀리초 직접 계산

// 구식 — Calendar
Calendar cal = Calendar.getInstance();
cal.add(Calendar.HOUR, 1);
Date result = cal.getTime();
```

문제가 많았다:
- `Date`는 mutable(변경 가능) — 여러 곳에서 같은 객체를 쓰면 예기치 않게 값이 바뀜
- 월이 0부터 시작 (1월 = 0, 12월 = 11) — 실수 유발
- 스레드 안전하지 않음

Java 8에서 `java.time` 패키지가 새로 생겼다. **불변(immutable)** 이고 직관적이다.

---

## 주요 클래스

| 클래스 | 용도 | 예시 |
|--------|------|------|
| `LocalDate` | 날짜만 (시간 없음) | 2026-04-18 |
| `LocalTime` | 시간만 (날짜 없음) | 14:30:00 |
| `LocalDateTime` | 날짜 + 시간 | 2026-04-18T14:30:00 |
| `ZonedDateTime` | 날짜 + 시간 + 타임존 | 2026-04-18T14:30:00+09:00 |
| `Instant` | 유닉스 타임스탬프 | 1713430200 |

---

## LocalDateTime 기본 사용

```java
// 현재 시각
LocalDateTime now = LocalDateTime.now();

// 특정 시각 생성
LocalDateTime dt = LocalDateTime.of(2026, 4, 18, 14, 30, 0);

// 문자열 파싱
LocalDateTime parsed = LocalDateTime.parse("2026-04-18T14:30:00");

// KIS API 응답 파싱 — "2026-04-19 11:00:00" 형식
// 공백이 있어서 T로 교체 후 파싱
LocalDateTime expiredAt = LocalDateTime.parse(
    "2026-04-19 11:00:00".replace(" ", "T")
);

// 연산 — 불변이라 새 객체 반환
LocalDateTime oneHourBefore = expiredAt.minusHours(1);
LocalDateTime tomorrow = now.plusDays(1);
```

---

## ChronoUnit — 시간 단위 계산

`ChronoUnit`은 두 시각 사이의 차이를 특정 단위로 계산할 때 쓴다.

```java
LocalDateTime now = LocalDateTime.now();
LocalDateTime expiredAt = LocalDateTime.parse("2099-12-31T23:59:59");

// 두 시각 사이의 초 차이
long seconds = ChronoUnit.SECONDS.between(now, expiredAt);

// 두 시각 사이의 일 차이
long days = ChronoUnit.DAYS.between(now, expiredAt);

// 두 시각 사이의 시간 차이
long hours = ChronoUnit.HOURS.between(now, expiredAt);
```

`between(start, end)` — start에서 end까지의 차이. end가 start보다 과거면 음수가 나온다.

---

## pong-to-rich에서 사용한 패턴 — Redis TTL 계산

```java
// KIS API 응답: "2026-04-19 11:00:00"
LocalDateTime expiredAt = LocalDateTime
    .parse(response.accessTokenExpired().replace(" ", "T"))
    .minusHours(1);  // 만료 1시간 전을 TTL 기준으로

long ttlSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), expiredAt);

redisTemplate.opsForValue().set(REDIS_KEY, token, ttlSeconds, TimeUnit.SECONDS);
```

**왜 이 방식을 썼나:**
- KIS 토큰 만료 시각이 응답에 포함되어 있음
- 고정값(86400초)으로 TTL을 잡으면 실제 만료 시각과 어긋날 수 있음
- 실제 만료 시각 기준으로 동적으로 TTL을 계산하는 게 더 정확함

---

## Duration vs ChronoUnit

비슷한 역할이지만 쓰임새가 다르다.

```java
// ChronoUnit — 특정 단위 하나로 차이 계산
long seconds = ChronoUnit.SECONDS.between(start, end); // 총 초 차이

// Duration — 시/분/초 전체를 다 들고 있는 객체
Duration duration = Duration.between(start, end);
long seconds = duration.getSeconds();  // 총 초
long hours = duration.toHours();       // 총 시간
```

Redis TTL처럼 "초 단위 숫자 하나만 필요할 때"는 `ChronoUnit.SECONDS.between()`이 더 간결하다.
