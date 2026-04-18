# 소유자 검증 패턴 (validateOwner)

## 왜 필요한가

인증된 사용자라도 **다른 사람의 리소스**에 접근하면 안 된다.

```
유저 A가 로그인 후 /api/broker-accounts/99 DELETE 요청
→ 99번 계좌가 유저 B 것이라면? → 차단해야 한다
```

JWT 인증은 "누구인가"를 검증하지, "이 리소스가 내 것인가"는 검증하지 않는다.
서비스 레이어에서 별도로 소유자 검증이 필요하다.

---

## 패턴 구조

```java
// 1. 리소스 조회
BrokerAccount account = brokerAccountRepository.findById(accountId)
        .orElseThrow(BrokerAccountNotFoundException::new);

// 2. 소유자 검증
validateOwner(email, account);

// 3. 비즈니스 로직 수행
account.deactivate();
```

`validateOwner()`를 private 메서드로 분리해서 반복 코드를 줄인다.

```java
private void validateOwner(String email, BrokerAccount account) {
    if (!account.getUser().getEmail().equals(email)) {
        log.warn("[증권사계좌] 권한 없음: {} → accountId: {}", email, account.getId());
        throw new BrokerAccountForbiddenException();
    }
}
```

---

## 응답 코드 선택 — 403 vs 404

소유자가 아닌 경우 두 가지 선택지가 있다:

| 응답 | 의미 | 언제 쓰나 |
|------|------|-----------|
| `403 Forbidden` | 리소스는 존재하지만 접근 권한 없음 | 내부 시스템, 관리자 도구 |
| `404 Not Found` | 리소스가 없는 것처럼 처리 | 외부 공개 API (보안 강화) |

**pong-to-rich에서는 403을 선택했다.**
학습 목적으로 명확한 에러 구분이 필요하고, 외부 공개 서비스가 아니기 때문.
실제 금융 서비스라면 타인의 계좌 존재 여부 자체를 숨기기 위해 404를 쓰는 것이 더 안전하다.

---

## 도메인별 적용

모든 도메인 서비스에 동일한 패턴 적용:

```java
// BrokerAccountService
private void validateOwner(String email, BrokerAccount account) {
    if (!account.getUser().getEmail().equals(email)) {
        throw new BrokerAccountForbiddenException();
    }
}

// WatchlistService
private void validateOwner(String email, Watchlist watchlist) {
    if (!watchlist.getUser().getEmail().equals(email)) {
        throw new WatchlistForbiddenException();
    }
}

// StrategyService
private void validateOwner(String email, Strategy strategy) {
    if (!strategy.getUser().getEmail().equals(email)) {
        throw new StrategyForbiddenException();
    }
}
```

---

## Controller에서 email을 받는 방법

```java
@DeleteMapping("/{accountId}")
public ResponseEntity<ApiResult<Void>> deactivate(
        @PathVariable Long accountId,
        Authentication authentication) {   // Security Context에서 꺼냄
    brokerAccountService.deactivate(authentication.getName(), accountId);
    return ResponseEntity.ok(ApiResult.ok());
}
```

`authentication.getName()` — JWT 토큰에서 파싱된 이메일. Spring Security가 필터에서 주입해준다.
컨트롤러가 직접 토큰을 파싱하지 않는다.

---

## pong-to-rich에서 사용된 곳

| 서비스 | 검증 대상 |
|--------|-----------|
| `BrokerAccountService` | BrokerAccount.user.email |
| `WatchlistService` | Watchlist.user.email |
| `StrategyService` | Strategy.user.email |
| `OrderService` | Order.user.email |
| `PortfolioService` | Holding.portfolio.user.email |
