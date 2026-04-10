# JPA와 Hibernate

## JPA란

JPA(Java Persistence API)는 자바에서 DB를 다루는 **표준 인터페이스(규격)** 다. 실제 구현체가 아니라 "이런 방식으로 DB를 다뤄라"는 약속이다.

```java
// JPA 인터페이스 — 개발자가 쓰는 것
userRepository.save(user);
userRepository.findByEmail(email);
```

---

## Hibernate란

Hibernate는 JPA 인터페이스를 실제로 구현한 **구현체**다. Spring Boot에서 `spring-boot-starter-data-jpa` 의존성을 추가하면 Hibernate가 기본으로 포함된다.

```
개발자 코드
    ↓
JPA 인터페이스 (규격)
    ↓
Hibernate (구현체) — 실제로 SQL 생성 & 실행
    ↓
DB
```

---

## JPA 메서드 → SQL 변환

Hibernate가 JPA 메서드를 SQL로 자동 변환한다.

```java
userRepository.save(user)
// Hibernate가 변환:
// INSERT INTO users (email, password, role) VALUES (?, ?, ?)

userRepository.findByEmail(email)
// Hibernate가 변환:
// SELECT * FROM users WHERE email = ? LIMIT ?

userRepository.existsByEmail(email)
// Hibernate가 변환:
// SELECT u.id FROM users u WHERE u.email = ? LIMIT ?
```

개발자가 SQL을 직접 쓰지 않아도 된다.

---

## show-sql 옵션

`application.yaml`에서 Hibernate가 생성한 SQL을 콘솔에 출력할 수 있다.

```yaml
jpa:
  show-sql: true       # Hibernate SQL 콘솔 출력
  properties:
    hibernate:
      format_sql: true # SQL 보기 좋게 포맷
```

로컬 개발 중 "JPA가 어떤 쿼리를 날리는지" 확인할 때 유용하지만, 로그가 너무 많아지므로 평소엔 `false`로 두는 게 좋다.

---

## ddl-auto 옵션

Hibernate가 엔티티 클래스를 보고 DB 테이블을 자동으로 생성/수정/삭제하는 옵션.

| 옵션 | 동작 |
|------|------|
| `create` | 서버 시작 시 테이블 삭제 후 재생성. **데이터 전부 날아감** |
| `create-drop` | 서버 시작 시 생성, 종료 시 삭제 |
| `update` | 엔티티 변경사항만 반영. 기존 데이터 유지 |
| `validate` | 엔티티와 테이블 구조가 맞는지 검증만. 변경 안 함 |
| `none` | 아무것도 안 함 |

**운영 환경에서는 반드시 `validate` 또는 `none`을 써야 한다.** `create`를 실수로 운영에 적용하면 모든 데이터가 삭제된다.

pong-to-rich 현재 설정:
- `local` — `create` (개발 중 스키마 자주 바뀌니까 매번 새로 생성)
- `prod` — 추후 `validate`로 변경 예정

---

## pong-to-rich에서 사용된 곳

```yaml
# application.yaml
jpa:
  hibernate:
    ddl-auto: create   # 서버 시작 시 테이블 재생성
  show-sql: false      # SQL 로그 끔 (필요할 때만 true로)
  properties:
    hibernate:
      format_sql: false
  open-in-view: false  # OSIV 패턴 비활성화 (성능 이슈 방지)
```

`open-in-view: false` — 뷰 렌더링까지 DB 커넥션을 열어두는 OSIV(Open Session In View) 패턴을 끄는 설정. REST API에서는 필요 없고 커넥션 낭비라 비활성화한다.
