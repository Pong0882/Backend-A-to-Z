# login-load-test SUMMARY

## 테스트 목적

DB vs Redis Refresh Token 저장 방식이 로그인 API 성능에 영향을 주는지 측정.

---

## 테스트 조건

- 엔드포인트: `POST /api/auth/login`
- VU: 50 / duration: 30s
- 더미 유저: test001~test100@pongtest.com
- 비밀번호: Test1234!
- 측정일: 2026-04-15 ~ 2026-04-16

---

## 전체 결과 비교표

| 모드 | 회차 | P95 | avg | 처리량 | 에러율 | 비고 |
|------|------|-----|-----|--------|--------|------|
| DB | RDB-00 | - | - | - | - | 워밍업 런 |
| DB | RDB-01 | 2.99s | 1.77s | 26/s | 0.85% | 워밍업 영향, 에러 7건 |
| DB | RDB-02 | 2.96s | 1.71s | 27/s | 0.00% | 안정화 |
| DB | RDB-03 | 2.68s | 1.51s | 30/s | 0.00% | 안정화 |
| Redis | Redis-01 | 2.85s | 1.66s | 28/s | 0.00% | |
| Redis | Redis-02 | 2.61s | 1.47s | 31/s | 0.00% | |
| Redis | Redis-03 | 2.52s | 1.43s | 31/s | 0.00% | |

threshold `p(95)<500ms` 전부 실패.

---

## 분석 및 결론

**Redis로 전환해도 로그인 성능 차이 없음.**

로그인 API 병목은 Refresh Token 저장소가 아니라 **bcrypt**다.

```
1. email로 DB 유저 조회    → 수ms
2. bcrypt 패스워드 검증     → 200~300ms  ← 병목
3. Refresh Token 저장      → DB: 수ms / Redis: ~1ms
4. JWT 발급                → 수ms
```

Spring Security 기본 bcrypt strength=10 → 2^10 = 1024번 해싱 반복.
Refresh Token 저장이 전체 응답시간의 1% 미만이라 저장소를 바꿔도 체감 차이 없음.

---

## 미확인 / 추후 검증 필요

- bcrypt strength를 낮추면 P95가 얼마나 개선되는지 직접 측정
- 500VU 이상에서 로그인 테스트 시 에러율 변화
