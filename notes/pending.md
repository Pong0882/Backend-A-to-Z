# 정리 필요 목록

구현하면서 나왔지만 아직 notes에 정리하지 못한 개념들.
요청하면 하나씩 해당 Phase 폴더에 정리한다.

---

| 개념 | 설명 | 발생일 | 정리 여부 |
|------|------|--------|-----------|
| `ResponseEntity` | HTTP 상태코드/헤더/body 제어, Spring 제공 클래스 | day01 | ✅ [정리](phase-7-spring-core/response-entity.md) |
| Jackson 직렬화 | Spring이 Map/객체를 JSON으로 자동 변환하는 원리 | day01 | ✅ [정리](phase-7-spring-core/jackson-serialization.md) |
| `@RestController` vs `@Controller` | 두 어노테이션의 차이 | day01 | ✅ [정리](phase-7-spring-core/rest-controller.md) |
| `Map.of()` | Java 9+ 불변 Map 생성 메서드, KisAuthService 요청 body에서 사용 | day02 | ✅ [정리](phase-6-java-jvm/map-of.md) |
| 메서드 체이닝 | 각 메서드가 자신을 반환해서 `.`으로 이어서 호출하는 방식, RestClient에서 사용 | day02 | ✅ [정리](phase-6-java-jvm/method-chaining.md) |
| 캐싱 패턴 | 캐시 히트/미스 기반 토큰 재사용, KisAuthService에서 사용 | day02 | ✅ [정리](phase-7-spring-core/caching-pattern.md) |
| 빌더 패턴 | 객체 생성 시 단계별로 값을 설정하는 패턴, RestClient 체이닝이 대표 예 | day02 | ✅ [정리](phase-17-design-pattern/builder-pattern.md) |
| Zero Trust | Cloudflare 터널 사용 중 언급됨. "절대 믿지 말고 항상 검증" 보안 모델. | day02-infra | ✅ [정리](phase-14-security-jwt/zero-trust.md) |
| Refresh Token 재발급 속도 측정 | Access Token 만료 시 두 번 왕복하는 비용이 실제로 얼마인지 Postman으로 측정. 이후 Redis 전환 시 DB vs Redis 응답속도 비교까지 | day03 | ✅ [정리](phase-28-performance/bottleneck-types.md) |
| Swagger 고도화 + AOP 로깅 | 테이블/Controller 늘어난 후 태그별 그룹화, 응답 스키마 명시. AOP 기반 공통 로깅(@Loggable)과 함께 진행 | day03 | 🔲 |
| AES-256 암호화 + 위협 모델 | BrokerAccount appkey/appsecret 암호화. CBC/IV 원리, 위협 모델 분석, PBKDF2/KMS TODO | day11 | ✅ [정리](phase-14-security-jwt/aes-encryption.md) |
| CORS | SecurityConfig CorsConfigurationSource 빈 추가. SOP 원리, Preflight, allowCredentials와 와일드카드 제한, Vite 프록시 우회 | day12 | ✅ [정리](phase-14-security-jwt/cors.md) |
| JPA AttributeConverter | DB 저장 시 자동 변환 인터페이스. @Convert 적용, @Component Bean 주입, 서비스 투명성 | day11 | ✅ [정리](phase-11-db-jpa/attribute-converter.md) |
| TDD — Red/Green/Refactor | KisAuthService에서 첫 적용. @Mock/@InjectMocks, Given/When/Then, RestClient 체이닝 Mock 방법 | day10 | ✅ [정리](phase-10-tdd/tdd-basics.md) |
| Redis Cache-Aside 패턴 | 캐시 히트/미스 흐름, TTL 계산, StringRedisTemplate vs RedisTemplate, 메모리 캐싱과 비교 | day10 | ✅ [정리](phase-16-redis/redis-cache-pattern.md) |
| Java 시간 API | LocalDateTime, ChronoUnit.SECONDS.between() — KIS TTL 계산에서 사용 | day10 | ✅ [정리](phase-6-java-jvm/java-time-api.md) |
| Mockito verify / ArgumentMatcher | verify/never 호출 검증, longThat 커스텀 조건 검증 | day10 | ✅ [정리](phase-10-tdd/mockito-verify.md) |
| Mockito Strictness | STRICT_STUBS vs LENIENT — @BeforeEach 공통 stub 패턴에서 LENIENT 사용 이유 | day10 | ✅ [정리](phase-10-tdd/mockito-strictness.md) |
| Bean Validation | @Valid, @NotBlank/@Email/@Size, MethodArgumentNotValidException 처리 흐름 | day10 | ✅ [정리](phase-7-spring-core/bean-validation.md) |
| 소유자 검증 패턴 | validateOwner() 분리, 403 vs 404 선택, Authentication.getName() | day10 | ✅ [정리](phase-7-spring-core/ownership-validation-pattern.md) |
| Soft Delete vs 비활성화 | 삭제 전략 선택 기준 — User(Soft Delete) vs BrokerAccount(deactivate) | day10 | ✅ [정리](phase-11-db-jpa/soft-delete-vs-deactivate.md) |
| PATCH vs DELETE | HTTP 메서드 선택 기준 — 상태 변경 vs 삭제 | day10 | ✅ [정리](phase-7-spring-core/patch-vs-delete.md) |
| @SQLRestriction | Entity 레벨 조회 필터 — Soft Delete 탈퇴 유저 자동 필터링, @Where deprecated 대체 | day10 | ✅ [정리](phase-11-db-jpa/sql-restriction.md) |
| @Transactional 원자성 | User + Portfolio 두 테이블 저장 시 원자성 보장, rollbackFor, readOnly | day10 | ✅ [정리](phase-11-db-jpa/transactional-atomicity.md) |
| `@PrePersist` / `@PreUpdate` | 모든 Entity의 createdAt/updatedAt 자동 관리 — JPA 생명주기 콜백 | day09 | ✅ [정리](phase-11-db-jpa/jpa-lifecycle-callbacks.md) |
| Soft Delete | User.softDelete() — deletedAt으로 논리 삭제, Hard Delete와 차이 | day09 | ✅ [정리](phase-11-db-jpa/soft-delete.md) |
| `BigDecimal` | StockPrice/Holding/Order 가격 타입 — 소수점 정밀도 (미국 주식 대응) | day09 | ✅ [정리](phase-11-db-jpa/bigdecimal.md) |
| JPA 연관관계 (`@OneToOne` / `@ManyToOne`) | Portfolio-User 1:1, Order-User N:1 등 — LAZY/EAGER, N+1 문제 | day09 | ✅ [정리](phase-11-db-jpa/jpa-relationships.md) |
| MySQL JSON 타입 | StrategyCondition.params — indicator VARCHAR + params JSON 혼합 방식 | day09 | ✅ [정리](phase-11-db-jpa/mysql-json-type.md) |
| 복합 UNIQUE (`@UniqueConstraint`) | Stock(code,market), BrokerAccount(user,broker,type) 등 | day09 | ✅ [정리](phase-11-db-jpa/unique-constraint.md) |
| 더티 체킹 (Dirty Checking) | softDelete/updateNickname 등 save() 없이 UPDATE되는 원리 | day09 | ✅ [정리](phase-11-db-jpa/dirty-checking.md) |
| `@Enumerated(EnumType.STRING)` | 모든 Enum 컬럼 — ORDINAL vs STRING 차이, 데이터 오염 위험 | day09 | ✅ [정리](phase-11-db-jpa/enum-type.md) |
| Entity 생성자 패턴 | `@NoArgsConstructor(PROTECTED)` + `@Builder` — JPA 스펙 + 불완전 객체 차단 | day09 | ✅ [정리](phase-11-db-jpa/entity-constructor.md) |
| `@GeneratedValue` 전략 | IDENTITY/SEQUENCE/TABLE/AUTO 차이 — MySQL은 IDENTITY | day09 | ✅ [정리](phase-11-db-jpa/generated-value.md) |
| `@Column(updatable = false)` | createdAt에 적용 — JPA 레벨 수정 차단 | day09 | ✅ [정리](phase-11-db-jpa/column-updatable-false.md) |
| LAZY + 프록시 객체 | FetchType.LAZY 동작 원리, LazyInitializationException, @ToString 주의 | day09 | ✅ [정리](phase-11-db-jpa/lazy-proxy.md) |
| JpaRepository 기본 메서드 | save/findById/findAll/Query Method 자동 생성 원리 | day09 | ✅ [정리](phase-11-db-jpa/jpa-repository.md) |
| 영속성 컨텍스트 | 비영속/영속/준영속/삭제 상태, 1차 캐시, 쓰기 지연, flush | day09 | ✅ [정리](phase-11-db-jpa/persistence-context.md) |
| Entity 설계 원칙 | pong-to-rich 전체 Entity에 적용된 10가지 원칙 총정리 | day09 | ✅ [정리](phase-11-db-jpa/entity-design-principles.md) |
