# @Column(updatable = false) — 수정 불가 컬럼

## 개념

```java
@Column(nullable = false, updatable = false)
private LocalDateTime createdAt;
```

`updatable = false` — 이 컬럼은 최초 INSERT 시에만 값이 들어가고, 이후 UPDATE 쿼리에서 제외된다.

## 왜 createdAt에 쓰는가

`createdAt`은 생성 시각이라 한 번 저장되면 절대 바뀌면 안 된다.

```java
// updatable = false 없으면
@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
    // createdAt도 실수로 건드리면 UPDATE 쿼리에 포함될 수 있음
}

// updatable = false 있으면
// JPA가 UPDATE 쿼리 생성 시 createdAt 컬럼 자체를 제외
// 실수로 createdAt 값을 바꿔도 DB에 반영 안 됨
```

## DDL 생성 시 동작

```sql
-- updatable = false는 DDL에는 영향 없음
-- DB 컬럼 자체에 제약이 걸리는 게 아니라 JPA 레벨에서만 동작
ALTER TABLE users ADD COLUMN created_at DATETIME NOT NULL;
-- DB에서 직접 UPDATE하면 바뀜 (JPA를 통하지 않으면)
```

JPA를 통한 UPDATE에서만 제외되는 것이지 DB 레벨 제약이 아니다.

## insertable = false

```java
@Column(insertable = false, updatable = false)
private LocalDateTime systemColumn;
```

- `insertable = false` — INSERT 쿼리에서도 제외 (DB 기본값이나 트리거로 채워지는 컬럼)
- `updatable = false` — UPDATE 쿼리에서 제외

둘 다 false면 JPA가 해당 컬럼을 읽기 전용으로 취급.

## pong-to-rich에서 사용된 곳

모든 Entity의 `createdAt` 컬럼에 적용.

```java
// User, Stock, Order, Strategy 등 모든 Entity
@Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@Column(nullable = false)
private LocalDateTime updatedAt;  // updatedAt은 updatable = false 없음 — 수정 가능해야 함
```
