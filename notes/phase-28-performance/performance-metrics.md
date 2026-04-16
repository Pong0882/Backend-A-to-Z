# 주요 성능 지표

## 핵심 지표 한눈에 보기

| 지표 | 설명 | 중요도 |
|------|------|--------|
| **P95** | 상위 5% 제외한 최대 응답시간 | ★★★ |
| **P99** | 상위 1% 제외한 최대 응답시간 | ★★★ |
| **avg** | 전체 평균 응답시간 | ★★ |
| **TTFB** | 서버가 응답을 시작하기까지 걸린 시간 | ★★★ |
| **Throughput** | 초당 처리 요청 수 (RPS/TPS) | ★★★ |
| **Error Rate** | 전체 요청 중 실패 비율 | ★★★ |
| **P50 (중앙값)** | 전체의 50%가 이 값 이하 | ★★ |

---

## 각 지표 상세

### P95 / P99 — 왜 평균보다 중요한가

```
요청 1000건 중:
- 900건: 10ms
- 90건:  50ms
- 10건:  2000ms

avg = (900×10 + 90×50 + 10×2000) / 1000 = 33.5ms
P95 = 50ms  (상위 5% = 50건 제외)
P99 = 2000ms (상위 1% = 10건 제외)
```

평균(33.5ms)만 보면 "빠르다"고 착각하지만, 10명의 유저는 2초를 기다리고 있다.
P95/P99는 "최악의 상황에서 유저가 경험하는 응답시간"을 나타낸다.

**실무 기준:**
- P95 < 200ms — 빠른 API (토큰 재발급, 조회 API)
- P95 < 500ms — 허용 가능한 API (로그인, 일반 쓰기)
- P95 > 1s — 개선 필요

---

### TTFB (Time To First Byte)

k6에서는 `http_req_waiting`으로 측정된다.

```
전체 응답시간 (http_req_duration)
= TCP 연결 (http_req_connecting)
+ TLS 핸드셰이크 (http_req_tls_handshaking)
+ 요청 전송 (http_req_sending)
+ 서버 처리 대기 (http_req_waiting) ← TTFB
+ 응답 수신 (http_req_receiving)
```

**TTFB가 중요한 이유:**
- TTFB ≈ http_req_duration 이면 → 병목이 서버 내부 처리
- TTFB << http_req_duration 이면 → 병목이 네트워크 또는 응답 크기

pong-to-rich 500VU 테스트에서:
```
http_req_duration  p(95) = 533ms
http_req_waiting   p(95) = 532ms  ← 거의 동일
```
→ 네트워크 구간은 거의 없고, 서버 내부에서 기다리는 시간이 대부분.

---

### Throughput (처리량) — RPS vs TPS

**RPS (Requests Per Second):** 초당 HTTP 요청 수. k6의 `http_reqs` 값.

**TPS (Transactions Per Second):** 초당 완료된 트랜잭션 수. 트랜잭션 하나가 여러 요청으로 구성될 수 있음.
- 예: 로그인 시나리오 = "로그인 요청 1건 + 프로필 조회 1건" → 2 RPS = 1 TPS

단순 API 테스트에서는 RPS = TPS로 봐도 무방하다. k6에서는 `iterations`(스크립트 1회 실행)가 TPS에 가깝다.

```
http_reqs:   1004  31.96/s   ← RPS
iterations:  1004  31.96/s   ← TPS (요청 1개 = 트랜잭션 1개인 경우)
```

```
50VU,  refresh API → ~400 req/s
500VU, refresh API → ~1,100~1,400 req/s
```

VU를 10배 늘렸는데 처리량은 3배만 증가한 경우 → 병목 존재.
이상적인 수평 확장이면 VU 10배 = 처리량 10배여야 한다.

---

### Error Rate

전체 요청 중 4xx/5xx 응답 비율.

k6에서 두 가지로 측정:
- `http_req_failed` — HTTP 레벨 실패 (4xx/5xx)
- `error_rate` (커스텀) — check() 실패 (응답 내용 검증 포함)

**에러율 0%인데 느린 경우:**
에러는 없지만 응답이 느리면 HikariCP 대기, 스레드풀 포화, GC pause 등이 원인일 수 있다.
실제 pong-to-rich 500VU 테스트에서 에러율 0%지만 P95 570ms가 나온 케이스가 이에 해당.

---

## 워밍업 (Cold Start) 효과

첫 번째 테스트 결과를 신뢰하면 안 되는 이유:

| 원인 | 설명 |
|------|------|
| JVM JIT 컴파일 | 처음 실행되는 코드 경로는 인터프리터 모드로 실행. 반복 후 JIT 컴파일되면 빨라짐 |
| MySQL 버퍼 캐시 | 처음 쿼리는 디스크에서 읽음. 이후엔 메모리 캐시에서 읽어서 빠름 |
| HikariCP 초기화 | 커넥션풀이 처음 요청 시 커넥션을 생성. 초기화 비용 발생 |
| Lettuce(Redis) 초기화 | Redis 클라이언트도 첫 연결 시 초기화 비용 있음 |

**규칙:** 첫 번째 실행은 `00` 번으로 저장 (워밍업 런). 비교는 `01`번 이후 기준.

pong-to-rich 실측:
```
Redis-vu500-01: P95 816ms  ← 워밍업 영향
Redis-vu500-02: P95 549ms
Redis-vu500-03: P95 445ms  ← 안정화
```

---

## 지표 간 관계로 병목 읽기

| 패턴 | 의미 |
|------|------|
| P95 높음 + Error Rate 0% | 느리지만 처리는 됨 → 커넥션 대기, 스레드풀 포화 |
| P95 높음 + Error Rate 높음 | 처리 못 하고 실패 → 완전한 병목 또는 서버 다운 |
| avg 낮음 + P99 높음 | 대부분 빠르지만 일부 요청이 극단적으로 느림 → GC pause, 특정 쿼리 Slow Query |
| Throughput 정체 + VU 증가 | VU 늘려도 처리량이 안 늘어남 → 병목 존재 (커넥션풀, CPU, 스레드풀) |
| TTFB ≈ http_req_duration | 병목이 서버 내부 처리 |
| TTFB << http_req_duration | 병목이 네트워크 또는 응답 크기 |

---

## pong-to-rich에서 사용된 곳

### refresh API 500VU 테스트 (2026-04-16)

```
DB 모드:
http_req_duration  avg=265ms  p(95)=572ms  p(99)=679ms
http_req_waiting   p(95)=572ms  ← TTFB와 거의 동일 → 서버 내부 병목

Redis 모드:
http_req_duration  avg=191ms  p(95)=445ms  p(99)=531ms
```

TTFB가 http_req_duration과 거의 동일 → 네트워크 구간 없음, 순수 서버 처리 병목 확인.
HikariCP 로그에서 `waiting=1` 확인 → 커넥션 고갈이 P95 폭등 원인.
