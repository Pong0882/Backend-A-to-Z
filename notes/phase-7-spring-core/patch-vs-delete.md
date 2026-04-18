# PATCH vs DELETE — HTTP 메서드 선택 기준

## HTTP 메서드 의미

| 메서드 | 의미 | 멱등성 | 바디 |
|--------|------|--------|------|
| `GET` | 조회 | O | X |
| `POST` | 생성 | X | O |
| `PUT` | 전체 수정 | O | O |
| `PATCH` | 부분 수정 | △ | O |
| `DELETE` | 삭제 | O | 보통 X |

---

## PATCH를 쓰는 경우

리소스를 **완전히 제거하지 않고 상태만 변경**할 때.

```java
// 주문 취소 — 삭제가 아니라 status를 CANCELLED로 변경
@PatchMapping("/{orderId}/cancel")
public ResponseEntity<ApiResult<OrderResponse>> cancel(@PathVariable Long orderId, ...) {
    return ResponseEntity.ok(ApiResult.ok(orderService.cancel(...)));
}
```

```java
// 전략 활성화 — status를 ACTIVE로 변경
@PatchMapping("/{strategyId}/activate")

// 전략 일시정지 — status를 PAUSED로 변경
@PatchMapping("/{strategyId}/pause")
```

**상태가 바뀌었다는 것을 응답으로 돌려줄 수 있다** — 변경된 리소스를 body에 담아 반환.

---

## DELETE를 쓰는 경우

리소스를 **실제로 제거(또는 논리 삭제)** 할 때.

```java
// 관심 종목 삭제 — DB에서 row 제거
@DeleteMapping("/{watchlistId}")
public ResponseEntity<ApiResult<Void>> delete(@PathVariable Long watchlistId, ...) {
    watchlistService.delete(...);
    return ResponseEntity.ok(ApiResult.ok());
}

// 증권사 계좌 비활성화 — 삭제에 가까운 동작 (복구 가능하지만 "제거" 의미)
@DeleteMapping("/{accountId}")
```

응답 body가 없거나 간단한 성공 메시지만 반환.

---

## 경계가 애매한 경우

**증권사 계좌 비활성화**는 실제로 row를 지우지 않지만 `DELETE`를 선택했다.

이유:
- 사용자 입장에서 "삭제하는" 동작이기 때문
- 재활성화 API가 없으면 사실상 영구 삭제와 동일

반대로 **전략 상태 변경**은 `PATCH`를 선택했다.

이유:
- ACTIVE ↔ PAUSED ↔ INACTIVE 사이를 왔다갔다 할 수 있음
- 상태 변경 결과를 응답으로 받아야 함 (현재 상태 확인 필요)

---

## pong-to-rich에서 사용된 곳

| API | 메서드 | 이유 |
|-----|--------|------|
| `DELETE /api/watchlist/{id}` | DELETE | row 실제 삭제 |
| `DELETE /api/broker-accounts/{id}` | DELETE | 비활성화 (삭제에 가까운 동작) |
| `DELETE /api/strategies/{id}` | DELETE | row 실제 삭제 |
| `PATCH /api/strategies/{id}/activate` | PATCH | 상태 변경 (되돌릴 수 있음) |
| `PATCH /api/strategies/{id}/pause` | PATCH | 상태 변경 |
| `PATCH /api/orders/{id}/cancel` | PATCH | 상태 변경 (CANCELLED), 응답 필요 |
| `PATCH /api/watchlist/{id}` | PATCH | alertPrice 부분 수정 |
| `PATCH /api/portfolio/holdings/{id}/toggle-hidden` | PATCH | isHidden 토글 |
