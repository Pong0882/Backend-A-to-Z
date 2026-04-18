# Soft Delete vs 비활성화 — 삭제 전략 선택 기준

## 두 가지 삭제 전략

### Hard Delete
```sql
DELETE FROM broker_accounts WHERE id = 1;
```
실제로 row를 삭제한다.

### Soft Delete
```java
public void softDelete() {
    this.deletedAt = LocalDateTime.now();
    this.isActive = false;
}
```
row는 유지하고 `deleted_at` 컬럼으로 논리 삭제 표시.

### 비활성화 (Deactivate)
```java
public void deactivate() {
    this.isActive = false;
}
```
삭제 없이 상태만 비활성화. `deleted_at` 없이 `is_active` 플래그만 변경.

---

## 선택 기준

| 상황 | 선택 | 이유 |
|------|------|------|
| 다른 테이블에서 FK로 참조하는 데이터 | Soft Delete 또는 비활성화 | Hard Delete 시 FK 제약 위반 |
| 감사 로그 / 이력이 필요한 데이터 | Soft Delete | 언제 삭제됐는지 추적 가능 |
| 복구 가능성이 필요한 데이터 | Soft Delete | 복구 시 deletedAt = null 처리 |
| 단순 상태 변경 (활성/비활성) | 비활성화 | 삭제 이력이 필요 없을 때 |
| 완전히 독립적인 데이터 | Hard Delete | 공간 절약, 단순함 |

---

## pong-to-rich에서의 선택

### User — Soft Delete 선택
```java
@SQLRestriction("deleted_at IS NULL")
public class User {
    private LocalDateTime deletedAt;  // null = 정상, 값 있음 = 탈퇴

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }
}
```
**이유:** 탈퇴 유저도 주문/전략 이력은 보존해야 한다. 언제 탈퇴했는지 추적 필요.

### BrokerAccount — 비활성화 선택
```java
public class BrokerAccount {
    private boolean isActive = true;

    public void deactivate() {
        this.isActive = false;
    }
}
```
**이유:**
- `orders`, `strategies` 테이블이 `broker_account_id`를 FK로 참조 → Hard Delete 불가
- 언제 삭제됐는지 이력보다 "현재 사용 가능한가"가 더 중요
- 재활성화 가능성 고려 (비활성화는 `isActive = true`로 되돌릴 수 있음)

---

## Soft Delete와 비활성화의 차이

| | Soft Delete | 비활성화 |
|---|---|---|
| 삭제 시각 추적 | O (`deletedAt`) | X |
| 복구 | `deletedAt = null` | `isActive = true` |
| 용도 | 탈퇴, 영구 삭제에 가까운 상황 | 일시적 비활성화, 재활성화 가능 |
| 필드 | `deleted_at` (DateTime) | `is_active` (Boolean) |

---

## 주의사항

Soft Delete나 비활성화를 쓰면 **조회 시 필터 조건**을 항상 신경 써야 한다.

- User: `@SQLRestriction("deleted_at IS NULL")` — JPA 레벨 자동 필터
- BrokerAccount: 현재 필터 없음 → 목록 조회 시 비활성 계좌도 포함됨

```java
// TODO: 비활성 계좌 필터링 — findAllByUser → findAllByUserAndIsActiveTrue 전환 검토
List<BrokerAccount> findAllByUser(User user);
```

> 이 부분은 향후 개선 대상. 현재는 isActive 상관없이 전체 반환.
