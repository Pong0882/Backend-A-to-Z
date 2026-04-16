# 병목 지점 유형 정리

## 병목이란

시스템에서 가장 느린 구간. 전체 성능은 항상 가장 느린 구간에 의해 결정된다.

```
요청 → [Spring 스레드풀] → [비즈니스 로직] → [DB 커넥션풀] → [DB 쿼리] → 응답
                                   ↑
                              여기가 막히면 전체가 막힘
```

병목 하나를 해결하면 다음 병목이 드러난다. 하나씩 측정하고 개선하는 것이 핵심.

---

## 병목 유형별 정리

### 1. DB 커넥션풀 고갈 (HikariCP)

**원인:** 동시 요청 수 > 커넥션풀 크기. 커넥션을 받으려는 스레드가 대기.

**증상:**
- P95 급등, 에러율 0% (처리는 되지만 느림)
- Grafana HikariCP `pending` 발생
- HikariCP 로그: `Connection not added, stats (waiting=N)`

**pong-to-rich 실측 (2026-04-16):**
```
커넥션풀 10개 / 500VU → P95 572ms
HikariCP 로그: waiting=1 확인
50VU에서는 P95 6ms — 커넥션 여유 있어서 문제 없었음
```

**해결 방법:**
- 커넥션풀 크기 증가 (`maximum-pool-size`)
- 쿼리 최적화 → 커넥션 점유 시간 단축
- Redis 등으로 DB 조회 자체를 줄임

**HikariCP 권장 공식:**
```
커넥션 수 = (CPU 코어 수 × 2) + 디스크 스핀들 수
pong-server 2코어 → 2×2+1 = 5 (이론값, 실제는 부하 테스트로 튜닝)
```

---

### 2. bcrypt (CPU bound)

**원인:** bcrypt는 의도적으로 느리게 설계된 해싱 알고리즘. CPU 연산이 많음.

**증상:**
- 로그인 API가 다른 API 대비 압도적으로 느림
- CPU 사용률 높음
- VU 늘려도 처리량이 선형으로 안 늘어남

**pong-to-rich 실측 (2026-04-16):**
```
로그인 P95: 2.68s (50VU)
refresh P95: 6ms (50VU)
→ bcrypt가 차지하는 비중이 압도적
```

**Spring Security 기본 strength = 10:**
```
2^10 = 1024번 해싱 반복 → 요청당 200~300ms
strength 낮추면 빠르지만 보안 약해짐
```

**해결 방법:**
- bcrypt strength 조정 (보안과 성능 트레이드오프)
- 로그인 API 전용 스레드풀 분리
- 캐싱 (로그인 결과는 캐싱 불가, 근본 해결 아님)

---

### 3. Spring 스레드풀 포화 (Tomcat)

**원인:** Tomcat 기본 스레드 200개. 요청이 스레드보다 많으면 대기 큐 발생.

**증상:**
- VU 수가 스레드 수 초과 시 응답 지연
- P95 급등, 에러율 낮음
- 스레드 덤프에서 `WAITING` 상태 스레드 다수

**pong-to-rich 500VU 테스트:**
```
Tomcat 기본 스레드 200개 / 500VU
→ 300VU는 스레드 대기 가능성
→ DB 커넥션 병목과 겹쳐서 정확한 원인 분리가 어려웠음
```

**해결 방법:**
```yaml
server:
  tomcat:
    threads:
      max: 400  # 기본 200
```
- 무한정 늘리면 메모리 증가 (스레드당 약 1MB)
- Spring WebFlux (비동기/논블로킹)로 전환하면 스레드 수 문제 자체를 해결

---

### 4. Slow Query (DB 쿼리 병목)

**원인:** 인덱스 없는 Full Table Scan, 복잡한 JOIN, 대량 데이터 조회.

**증상:**
- 특정 API만 느림
- HikariCP active 커넥션이 오래 유지됨
- MySQL Slow Query 로그에 기록됨

**예상 pong-to-rich 실측 (인덱스 실습 예정):**
```
stock_prices 100만 건 / 인덱스 없음 → Full Scan → 수초
stock_prices 100만 건 / 인덱스 있음 → Index Scan → 수ms
```

**해결 방법:**
- 인덱스 추가
- 쿼리 최적화 (EXPLAIN 분석)
- 페이지네이션 (대량 조회 분할)
- 캐싱 (자주 조회되는 데이터)

---

### 5. 메모리 / GC pause

**원인:** 힙 메모리 부족 → GC 빈번 → Stop-the-World pause → 순간 응답 지연.

**증상:**
- P99가 P95보다 훨씬 높음 (일부 요청만 극단적으로 느림)
- Grafana JVM 힙 사용량이 지속적으로 증가 (메모리 누수)
- GC 로그에서 Full GC 빈번

**해결 방법:**
- 힙 크기 조정 (`-Xmx`)
- 메모리 누수 원인 제거 (객체 참조 해제)
- GC 알고리즘 튜닝 (G1GC, ZGC)

---

### 6. 네트워크 레이턴시

**원인:** 서버 간 거리, 외부 API 호출, DNS 조회 등.

**증상:**
- TTFB(http_req_waiting)는 낮은데 http_req_duration이 높음
- 외부 API 호출 구간에서 응답 지연

**pong-to-rich에서 해당하는 경우:**
- KIS API 호출 → 외부 네트워크 왕복
- Cloudflare 터널 경유 요청 → 터널 레이턴시 추가

**k6 테스트에서 내부망 사용하는 이유:**
네트워크 레이턴시 제거 → 순수 서버 성능만 측정.

---

## 병목 찾는 순서

```
1. k6로 부하 테스트 → P95/P99 측정
2. Grafana에서 확인:
   - HikariCP pending → DB 커넥션 병목
   - CPU 100% → CPU bound (bcrypt, 복잡한 연산)
   - JVM Heap 지속 증가 → 메모리 누수
3. TTFB vs http_req_duration 비교:
   - TTFB ≈ duration → 서버 내부 병목
   - TTFB << duration → 네트워크/응답 크기
4. HikariCP DEBUG 로그:
   - waiting=N → 커넥션 고갈 확정
5. MySQL Slow Query 로그:
   - 느린 쿼리 확정
```

---

## pong-to-rich에서 사용된 곳

### 2026-04-16 refresh API 500VU 테스트

확인된 병목:
- **HikariCP 커넥션 고갈** — `waiting=1` 로그 확인, P95 572ms
- **bcrypt** — 로그인 P95 2.68s vs refresh P95 6ms로 간접 확인

의심되는 병목 (미확인):
- **Spring 스레드풀** — 500VU > Tomcat 기본 200 스레드, `docker stats`로 CPU 확인 필요
- **userRepository.findByEmail** — Redis 모드에서도 DB 조회 발생, 완전한 커넥션 절약 안 됨
