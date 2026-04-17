# JpaRepository — 기본 제공 메서드

## 상속 구조

```
JpaRepository<T, ID>
  └── PagingAndSortingRepository
        └── CrudRepository
              └── Repository
```

`JpaRepository`를 extends하는 것만으로 기본 CRUD + 페이징 + 정렬 메서드를 전부 사용할 수 있다.

## 기본 제공 메서드

### 저장

```java
S save(S entity)              // INSERT or UPDATE (id 있으면 UPDATE, 없으면 INSERT)
List<S> saveAll(Iterable<S>)  // 여러 개 한번에
S saveAndFlush(S entity)      // save 후 즉시 flush (트랜잭션 안에서 즉시 DB 반영)
```

### 조회

```java
Optional<T> findById(ID id)         // id로 단건 조회
List<T> findAll()                   // 전체 조회 (주의: 대용량 테이블엔 절대 쓰지 마라)
List<T> findAllById(Iterable<ID>)   // id 목록으로 조회
boolean existsById(ID id)           // 존재 여부
long count()                        // 전체 개수
```

### 삭제

```java
void deleteById(ID id)
void delete(T entity)
void deleteAll()
void deleteAllById(Iterable<ID>)
```

### 페이징/정렬

```java
Page<T> findAll(Pageable pageable)         // 페이징
List<T> findAll(Sort sort)                 // 정렬
```

## 메서드 이름으로 쿼리 생성 (Query Method)

JPA가 메서드명을 파싱해서 자동으로 JPQL을 생성한다.

```java
// findBy + 필드명
Optional<User> findByEmail(String email);
// SELECT u FROM User u WHERE u.email = ?

// existsBy + 필드명
boolean existsByNickname(String nickname);
// SELECT COUNT(u) > 0 FROM User u WHERE u.nickname = ?

// findAllBy + 필드명 + OrderBy + 필드명 + Desc
List<Order> findAllByUserOrderByCreatedAtDesc(User user);
// SELECT o FROM Order o WHERE o.user = ? ORDER BY o.createdAt DESC

// findBy + 필드명 + And + 필드명
Optional<Stock> findByCodeAndMarket(String code, Stock.Market market);
// SELECT s FROM Stock s WHERE s.code = ? AND s.market = ?

// findAllBy + 필드명
List<Strategy> findAllByStatus(Strategy.Status status);
```

**주요 키워드:**

| 키워드 | 설명 | 예시 |
|--------|------|------|
| `And` | AND 조건 | `findByEmailAndNickname` |
| `Or` | OR 조건 | `findByEmailOrNickname` |
| `OrderBy...Asc/Desc` | 정렬 | `findAllByUserOrderByCreatedAtDesc` |
| `IsNull` / `IsNotNull` | NULL 체크 | `findByDeletedAtIsNull` |
| `In` | IN 절 | `findByStatusIn(List<Status>)` |
| `GreaterThan` | > | `findByCreatedAtGreaterThan` |
| `Between` | BETWEEN | `findByTradeDateBetween` |

## @Query — 복잡한 쿼리

메서드명으로 표현하기 어려운 쿼리는 `@Query`로 직접 작성.

```java
@Query("SELECT sp FROM StockPrice sp WHERE sp.stock = :stock AND sp.tradeDate BETWEEN :start AND :end ORDER BY sp.tradeDate DESC")
List<StockPrice> findByStockAndDateRange(
    @Param("stock") Stock stock,
    @Param("start") LocalDate start,
    @Param("end") LocalDate end
);
```

## save() 동작 원리 — SimpleJpaRepository

```java
// SimpleJpaRepository.save() 내부
public <S extends T> S save(S entity) {
    if (entityInformation.isNew(entity)) {
        em.persist(entity);   // id가 없으면 INSERT
        return entity;
    } else {
        return em.merge(entity);  // id가 있으면 UPDATE
    }
}
```

id가 null이면 새 Entity로 판단 → `persist()` (INSERT)
id가 있으면 기존 Entity로 판단 → `merge()` (UPDATE)

## pong-to-rich에서 사용된 곳

```java
// UserRepository.java
Optional<User> findByEmail(String email);
boolean existsByEmail(String email);
Optional<User> findByNickname(String nickname);
boolean existsByNickname(String nickname);

// StockRepository.java
Optional<Stock> findByCodeAndMarket(String code, Stock.Market market);
List<Stock> findAllByMarket(Stock.Market market);

// OrderRepository.java
List<Order> findAllByUserOrderByCreatedAtDesc(User user);
List<Order> findAllByStatus(Order.OrderStatus status);
```
