# refresh-load-test SUMMARY

## 테스트 목적

DB vs Redis Refresh Token 저장 방식이 refresh API 성능에 영향을 주는지 측정.
50VU(정상 부하)와 500VU(고부하)로 나눠 조건별 차이를 확인.

---

## 테스트 조건

- 엔드포인트: `POST /api/auth/refresh`
- 더미 유저: test001~test100@pongtest.com (토큰 100개 사전 수집 후 공유)
- 비밀번호: Test1234!
- 측정일: 2026-04-16

---

## 전체 결과 비교표

### 50VU (30s)

| 모드 | 회차 | P95 | avg | 처리량 | 에러율 | 비고 |
|------|------|-----|-----|--------|--------|------|
| DB | RDB-00 | - | - | - | - | 워밍업 런 |
| DB | RDB-01 | 54.1ms | 15.1ms | 348/s | 0.00% | 워밍업 영향 (P95 높음) |
| DB | RDB-02 | 6.75ms | 3.36ms | 404/s | 0.00% | 안정화 |
| DB | RDB-03 | 5.92ms | 3.34ms | 404/s | 0.00% | 안정화 |
| Redis | Redis-01 | 13.51ms | 5.28ms | 400/s | 0.00% | 워밍업 영향 |
| Redis | Redis-02 | 5.47ms | 2.81ms | 410/s | 0.00% | 안정화 |
| Redis | Redis-03 | 5.64ms | 2.95ms | 411/s | 0.00% | 안정화 |

threshold `p(95)<200ms` 전부 통과.

### 500VU (30s)

| 모드 | 회차 | P95 | P99 | avg | 처리량 | 에러율 | 비고 |
|------|------|-----|-----|-----|--------|--------|------|
| DB | RDB-vu500-00 | - | - | - | - | 워밍업 런 |
| DB | RDB-vu500-01 | 533ms | 609ms | 240ms | 1,218/s | 0.00% | threshold 실패 |
| DB | RDB-vu500-02 | 582ms | 726ms | 274ms | 1,106/s | 0.00% | threshold 실패 |
| DB | RDB-vu500-03 | 572ms | 679ms | 264ms | 1,130/s | 0.00% | threshold 실패 |
| Redis | Redis-vu500-00 | - | - | - | - | 워밍업 런 |
| Redis | Redis-vu500-01 | 816ms | 1.01s | 391ms | 841/s | 0.00% | threshold 실패, 워밍업 영향 |
| Redis | Redis-vu500-02 | 549ms | 701ms | 250ms | 1,179/s | 0.00% | threshold 실패 |
| Redis | Redis-vu500-03 | 445ms | 531ms | 191ms | 1,426/s | 0.00% | threshold 실패, 최고 처리량 |

threshold `p(95)<200ms`, `p(99)<400ms` 전부 실패.

---

## 분석 및 결론

### 50VU — 차이 없음

```
DB   안정화 P95: ~6ms,  avg: ~3.3ms, 처리량: 404/s
Redis 안정화 P95: ~5.5ms, avg: ~2.9ms, 처리량: 411/s
```

HikariCP 커넥션 10개로 50VU 충분히 처리 가능.
토큰 조회 쿼리가 indexed column이라 DB 조회도 수ms 이내.
저장소 차이가 체감되지 않는다.

### 500VU — 둘 다 느리고, 원인 미확인

```
DB   안정화 P95: ~572ms, avg: ~265ms, 처리량: ~1,130/s
Redis 안정화 P95: ~445ms, avg: ~191ms, 처리량: ~1,426/s
```

Redis가 약 **20~25% 빠름**. 에러율은 둘 다 0%.

HikariCP `waiting=1` 로그 확인 → DB 모드에서 커넥션 고갈 발생.

**500VU에서 둘 다 느린 이유 (미확인, 추후 검증 필요):**

1. **Spring 스레드풀**: Tomcat 기본 200 스레드. 500 VU 동시 요청 → 300개 큐 대기
2. **CPU 한계**: pong-server 2코어 VM. 500 concurrent에서 context switching 급증
3. **userRepository.findByEmail**: Redis 모드에서도 유저 조회는 DB에 접근
4. **토큰 경합**: 100개 토큰을 500 VU가 공유 → 같은 토큰 동시 접근 시 직렬화

---

## 미확인 / 추후 검증 필요

- `docker stats`로 500VU 테스트 중 CPU 사용률 확인 (스레드풀 vs CPU 병목 구분)
- Tomcat `server.tomcat.threads.max` 500으로 올린 후 재측정
- 더미 유저 500명으로 늘려서 토큰 경합 없는 환경에서 재측정
- `userRepository.findByEmail` DB 쿼리 제거 시 Redis 모드 성능 변화 측정
