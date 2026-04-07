# Spring 로드맵

> Spring Boot 4.x / Java 17 기반
> 각 항목은 **이론 정리 → 구현 실습 → 검증/비교** 3단계로 구성

---

## 진행 방식

- `[ ]` 미완료 / `[x]` 완료
- 각 단계는 순차 진행을 권장하나, 독립적인 챕터는 병렬 진행 가능

---

## PHASE 0-A — 네트워크 & 웹 기초

> 모든 백엔드 지식의 기반. HTTP/TCP를 이해 못 하면 Spring도 겉핥기가 됨

### 0A-1. 네트워크 기초
- [ ] OSI 7계층 정리 및 각 계층 역할 + 실제 사용 프로토콜 매핑
- [ ] TCP vs UDP 차이 정리 (연결 지향 / 신뢰성 / 순서 보장)
- [ ] TCP 3-way handshake / 4-way handshake 정리
- [ ] TCP 흐름 제어 / 혼잡 제어 정리
- [ ] IP / 서브넷 마스크 / CIDR 표기법 정리 (AWS VPC 설계 기반 지식)
- [ ] DNS 동작 원리 정리 (Recursive Query, 캐싱, TTL)
- [ ] 로드밸런서 L4 vs L7 차이 정리

### 0A-2. HTTP 프로토콜 심화
- [ ] HTTP/1.1 Keep-Alive / Pipelining 정리
- [ ] HTTP/2 멀티플렉싱 / 헤더 압축(HPACK) / 서버 푸시 정리
- [ ] HTTP/3 (QUIC 기반) 정리
- [ ] HTTP 캐시 정리 (`Cache-Control` / `ETag` / `Last-Modified` / `Expires`)
- [ ] 브라우저 → 서버까지 요청 전달 전체 흐름 정리 (DNS → TCP → TLS → HTTP → 응답)

---

## PHASE 0-B — 환경 설정 & 프로젝트 기반 설계

> 실무에서 첫날부터 쓰는 것들인데 보통 가르쳐주지 않음

### 0B-1. application.yml 설계 & 환경 분리
- [ ] `application.yml` vs `application.properties` 차이 정리
- [ ] Spring Profile 동작 원리 정리 (`@Profile`, `spring.profiles.active`)
- [ ] 환경별 설정 분리 전략 정리 (`application-dev.yml` / `application-prod.yml` / `application-test.yml`)
- [ ] 설정값 우선순위 정리 (환경변수 > 커맨드라인 > yml 파일 > 기본값)
- [ ] 민감 정보 관리 전략 정리 (환경변수 / AWS Secrets Manager / Vault / Jasypt 암호화)
- [ ] 실습: 환경별 DB / Redis / 외부 API URL 분리 설정 구현
- [ ] 실습: Jasypt로 yml 내 비밀번호 암호화
- [ ] 실습: Docker Compose 환경변수 주입 + Spring Profile 연동

### 0B-2. Gradle 빌드 시스템
- [ ] Gradle vs Maven 차이 정리
- [ ] `build.gradle` 구조 정리 (plugins / dependencies / tasks)
- [ ] 의존성 스코프 정리 (`implementation` / `compileOnly` / `runtimeOnly` / `testImplementation`)
- [ ] Gradle 빌드 캐시 및 incremental build 원리 정리
- [ ] 실습: 멀티 모듈 Gradle 설정 (`settings.gradle`, `subprojects` 공통 설정)
- [ ] 실습: Gradle Task 커스텀 작성 (빌드 후 Docker 이미지 자동 빌드)

---

## PHASE 0-C — Git & 협업 워크플로우

> 혼자 쓸 때와 팀에서 쓸 때 완전히 다름. 내부 원리를 모르면 사고가 남

### 0C-1. Git 내부 원리
- [ ] Git 오브젝트 모델 정리 (blob / tree / commit / tag — 모든 것이 SHA-1 해시)
- [ ] `.git` 디렉터리 구조 정리 (`HEAD` / `index` / `objects` / `refs`)
- [ ] **3가지 영역 완전 정리** (Working Directory → Staging Area → Repository)
- [ ] `git add` / `git commit` 내부에서 일어나는 일 정리
- [ ] Branch의 정체 정리 (특정 커밋을 가리키는 포인터에 불과함)
- [ ] `HEAD` 의 정체 정리 (현재 체크아웃된 커밋/브랜치를 가리키는 포인터)
- [ ] Fast-Forward merge vs 3-way merge 차이 정리
- [ ] `rebase` 동작 원리 정리 (커밋을 복사해서 재적용, merge와 결과는 같지만 히스토리가 다름)

### 0C-2. Git 핵심 명령어 & 실전 시나리오
- [x] **되돌리기 명령어 차이 완전 정리** → [정리](./notes/phase-0C-git/git-basics.md)
  - `git restore` — 워킹 디렉터리 변경사항 폐기
  - `git reset --soft/--mixed/--hard` — 커밋 취소 (soft: 스테이징 유지 / mixed: 워킹 디렉터리 유지 / hard: 전부 버림)
  - `git revert` — 되돌리는 커밋을 새로 만듦 (push된 커밋 취소할 때)
  - `reset` vs `revert` 언제 무엇을 써야 하는지 기준 정리
- [x] **`git stash`** — 작업 중간에 브랜치 전환해야 할 때 임시 저장 → [정리](./notes/phase-0C-git/git-basics.md)
- [ ] **`git cherry-pick`** — 특정 커밋만 골라서 현재 브랜치에 적용
- [ ] **`git reflog`** — 실수로 reset/rebase 날렸을 때 복구하는 법
- [ ] **`git bisect`** — 버그가 처음 생긴 커밋을 이진 탐색으로 찾는 법
- [ ] **`git blame`** — 특정 라인을 누가 언제 수정했는지 추적
- [x] **`git log` 활용** — `--oneline` / `--graph` / `--author` / `--since` / 특정 파일 히스토리 → [정리](./notes/phase-0C-git/git-basics.md)
- [ ] 실습: `reset --hard`로 날린 커밋을 `reflog`로 복구
- [ ] 실습: `bisect`로 버그 유발 커밋 찾기

### 0C-3. 브랜치 전략 & 협업 워크플로우
- [ ] **Git Flow 정리** (main / develop / feature / release / hotfix 브랜치 역할)
- [ ] **GitHub Flow 정리** (main + feature 브랜치, PR 기반 단순 플로우)
- [ ] **Trunk-Based Development 정리** (단일 main, 짧은 수명의 feature 브랜치, Feature Flag 활용)
- [ ] 세 전략 비교 — 팀 규모 / 배포 주기에 따른 선택 기준 정리
- [ ] **커밋 메시지 컨벤션 정리** (Conventional Commits: `feat:` / `fix:` / `chore:` / `refactor:` / `docs:`)
- [ ] **PR(Pull Request) 작성 원칙 정리** (작은 단위 / 리뷰어 배려 / 변경 이유 명시)
- [ ] 실습: Git Flow로 feature 개발 → develop 머지 → release → main 전체 흐름 실습
- [ ] 실습: Conventional Commits 기반 커밋 메시지 규칙 적용

### 0C-4. merge / rebase / 충돌 해결
- [ ] **`merge` vs `rebase` 언제 무엇을 써야 하는가** 정리
  - merge: 히스토리 보존, 협업 브랜치에서 안전
  - rebase: 히스토리 선형화, 로컬 작업 정리용 (push된 브랜치에 rebase는 위험)
- [ ] **`git rebase -i` (interactive rebase)** — 커밋 squash / 순서 변경 / 메시지 수정
- [ ] 충돌(conflict) 발생 원리 정리 (3-way merge에서 공통 조상 기준 비교)
- [ ] 실습: `rebase -i`로 WIP 커밋들을 의미있는 단위로 squash
- [ ] 실습: 의도적으로 충돌 만들고 해결하기 (양쪽 변경 모두 수용 / 한쪽만 수용 케이스)
- [ ] 실습: `merge --no-ff`로 머지 커밋 명시적 생성

### 0C-5. 원격 저장소 & 보안
- [x] `fetch` vs `pull` 차이 정리 (`pull` = `fetch` + `merge`) → [정리](./notes/phase-0C-git/git-basics.md)
- [ ] `push --force` vs `push --force-with-lease` 차이 정리 (force-with-lease가 더 안전한 이유)
- [x] **`.gitignore` 패턴 규칙 정리** + 이미 tracked된 파일 제거하는 법 (`git rm --cached`) → [정리](./notes/phase-0C-git/git-basics.md)
- [ ] **민감 정보 커밋 사고 대응** — 이미 push된 비밀번호/키를 git history에서 제거하는 법 (`git filter-repo`, BFG)
- [ ] Git Hooks 정리 (`pre-commit` / `commit-msg` / `pre-push`) — 커밋 전 lint / 테스트 자동 실행
- [ ] 실습: `pre-commit` hook으로 커밋 전 코드 포맷 자동 적용 (husky 또는 shell script)
- [ ] 실습: `commit-msg` hook으로 Conventional Commits 형식 강제

---

## PHASE 0 — Java & JVM 기반 체력

> Spring 이전에 반드시 잡아야 할 Java/JVM 수준 지식

### 0-1. JVM 구조 & 메모리
- [ ] JVM 구조 정리 (ClassLoader / Runtime Data Area / Execution Engine)
- [ ] 메모리 영역 정리 (Heap / Stack / Method Area / PC Register / Native Stack)
- [ ] Heap 세부 구조 정리 (Young Generation: Eden/Survivor, Old Generation, Metaspace)
- [ ] GC 종류 및 동작 방식 정리 (Serial / Parallel / CMS / G1 / ZGC / Shenandoah)
- [ ] Stop-The-World 문제 및 GC 튜닝 포인트 정리
- [ ] 실습: `-Xms` / `-Xmx` / `-XX:+UseG1GC` JVM 옵션 설정 및 GC 로그 분석
- [ ] 실습: VisualVM / IntelliJ Profiler로 Heap 덤프 분석

### 0-2. Java 동시성 & 스레드
- [ ] Thread 생명주기 정리 (NEW / RUNNABLE / BLOCKED / WAITING / TERMINATED)
- [ ] `synchronized` / `volatile` / `Atomic` 클래스 차이 정리
- [ ] ThreadLocal 동작 원리 및 Spring에서의 활용 정리 (SecurityContext, 트랜잭션)
- [ ] ExecutorService / ThreadPoolExecutor 동작 원리 정리
- [ ] Thread Pool 사이즈 결정 공식 정리 (CPU bound vs I/O bound)
- [ ] CompletableFuture 비동기 파이프라인 정리
- [ ] Virtual Thread (Java 21 Project Loom) 개념 정리
- [ ] 실습: ThreadPoolExecutor 직접 설정 후 스레드 수에 따른 성능 비교
- [ ] 실습: CompletableFuture로 병렬 외부 API 호출 구현
- [ ] 실습: `@Async` 내부가 ThreadPoolExecutor임을 추적하고 커스텀 Pool 적용

### 0-3. Java 핵심 문법 & 모던 Java
- [ ] Generic 타입 소거(Type Erasure) 및 와일드카드 정리
- [ ] Java 8+ Stream API 내부 동작 (지연 평가, 병렬 스트림 주의점) 정리
- [ ] Optional 올바른 사용법 및 안티패턴 정리
- [ ] Java 9~17 주요 변경사항 정리 (Records, Sealed Class, Text Blocks, Pattern Matching)
- [ ] Reflection 동작 원리 정리 (Spring이 내부적으로 사용하는 방식)
- [ ] 실습: Record + Sealed Class를 도메인 모델에 적용

### 0-4. Java 버전 선택 & LTS 전략
- [ ] **Java 버전 체계 정리** — LTS(Long-Term Support) vs 비LTS 릴리즈 주기 차이
- [ ] **버전별 주요 변경사항 정리**
  - Java 8: Lambda / Stream / Optional / 기본 메서드 / `LocalDate`
  - Java 11 (LTS): `var` 확장 / HTTP Client API / `String` 신규 메서드
  - Java 17 (LTS): Sealed Class / Pattern Matching / Records GA / 난수 API
  - Java 21 (LTS): Virtual Thread GA / Sequenced Collections / Pattern Matching switch GA
- [ ] **프로젝트에서 버전 선택 기준 정리** (LTS 여부 / Spring Boot 지원 범위 / 라이브러리 호환성)
- [ ] Spring Boot 4.x가 요구하는 최소 Java 버전 및 권장 버전 정리
- [ ] 실습: Java 17 → Java 21 마이그레이션 체크리스트 작성 (deprecated API, 동작 변경 확인)

### 0-5. Java 자료구조 & 컬렉션 프레임워크
- [ ] **컬렉션 계층 구조 정리** (`Iterable` → `Collection` → `List` / `Set` / `Queue`)
- [ ] **List 구현체 비교**
  - `ArrayList`: 내부 배열, 인덱스 조회 O(1), 중간 삽입/삭제 O(n)
  - `LinkedList`: 이중 연결 리스트, 중간 삽입/삭제 O(1), 인덱스 조회 O(n)
  - `ArrayList` vs `LinkedList` — 실제로 `ArrayList`가 거의 항상 빠른 이유 (캐시 지역성)
- [ ] **Map 구현체 비교**
  - `HashMap`: 해시 테이블, O(1) 평균 / 해시 충돌 시 O(n) → `treeify` (Java 8+)
  - `LinkedHashMap`: 삽입 순서 보장
  - `TreeMap`: Red-Black Tree, 정렬 보장, O(log n)
  - `ConcurrentHashMap`: 멀티스레드 환경, 세그먼트 락 vs `Hashtable` 전체 락 비교
- [ ] **Set 구현체 비교** (`HashSet` / `LinkedHashSet` / `TreeSet`)
- [ ] **Queue / Deque 구현체 비교** (`ArrayDeque` vs `LinkedList` / `PriorityQueue` 힙 구조)
- [ ] **HashMap 내부 동작 완전 정리**
  - `hashCode()` + `equals()` 계약 (둘 다 오버라이드해야 하는 이유)
  - 초기 capacity / load factor / resize(rehashing) 동작
  - Java 8+ 해시 충돌 시 LinkedList → Red-Black Tree 전환 (treeify threshold=8)
- [ ] **스택 / 큐 / 힙 자료구조를 Java로 구현하는 관용 패턴 정리**
  - 스택: `Deque<>(ArrayDeque)` (Stack 클래스 쓰면 안 되는 이유)
  - 우선순위 큐: `PriorityQueue` (최소 힙 기본, 최대 힙 커스텀)
- [ ] **불변 컬렉션 정리** (`List.of()` / `Map.of()` / `Collections.unmodifiableList()` 차이)
- [ ] **실습: 컬렉션 선택 기준 체감 실습**
  - 100만 건 데이터에서 `ArrayList` vs `LinkedList` 삽입/조회/삭제 성능 비교
  - `HashMap` vs `TreeMap` 정렬 필요 여부에 따른 선택 실습
  - `HashMap` 키로 커스텀 객체 쓸 때 `hashCode` / `equals` 미구현 버그 재현

---

## PHASE 0-D — Linux & Shell

> 서버는 결국 Linux 위에서 돌아감. CLI 못 다루면 장애 대응 불가

### 0D-1. Linux 기초
- [ ] Linux 파일 시스템 구조 정리 (`/etc` / `/var` / `/proc` / `/tmp` 각 역할)
- [ ] 파일 권한 정리 (`rwx`, `chmod`, `chown`, `umask`)
- [ ] 프로세스 관리 정리 (`ps` / `top` / `htop` / `kill` / `systemctl`)
- [ ] **`/proc` 파일시스템 정리** — `/proc/[pid]/` 로 프로세스 메모리 / fd / 스레드 확인하는 법
- [ ] 네트워크 명령어 정리 (`netstat` / `ss` / `curl` / `wget` / `nc` / `tcpdump`)
- [ ] 디스크 관련 명령어 정리 (`df` / `du` / `lsblk` / `iostat`)
- [ ] 로그 확인 명령어 정리 (`tail -f` / `grep` / `awk` / `sed` / `journalctl`)

### 0D-2. Shell 스크립트 & 자동화
- [ ] Bash 기본 문법 정리 (변수 / 조건문 / 반복문 / 함수)
- [ ] 실습: 배포 자동화 shell script 작성 (빌드 → 서버 전송 → 재시작)
- [ ] 실습: 로그 파일 정기 정리 스크립트 작성 (`cron` + `find -mtime`)
- [ ] 실습: 서비스 헬스체크 + 자동 재시작 스크립트 작성

### 0D-3. 서버 운영 실전
- [ ] `systemd` 서비스 등록 및 관리 정리 (Spring Boot 앱을 서비스로 등록)
- [ ] SSH 키 기반 인증 정리 (비밀번호 없이 서버 접속, `authorized_keys`)
- [ ] **실무 트러블슈팅 명령어 시나리오**
  - 포트 점유 프로세스 찾기: `lsof -i :8080` / `ss -tlnp`
  - 특정 프로세스 메모리 사용량 확인: `cat /proc/[pid]/status`
  - 디스크 어느 디렉터리가 큰지 찾기: `du -sh /* | sort -rh | head`
  - 실시간 로그에서 ERROR만 필터링: `tail -f app.log | grep --line-buffered ERROR`
  - 네트워크 연결 상태 확인: `ss -s` / TIME_WAIT 과다 확인
- [ ] 실습: EC2 서버에 SSH 접속 → Spring Boot 앱 수동 배포 및 운영

---

## PHASE 1 — Spring 핵심 이론 & 구조 이해

### 1-1. Spring 핵심 개념
- [ ] POJO(Plain Old Java Object) 개념 및 Spring이 POJO를 다루는 방식 정리
- [ ] IoC(Inversion of Control) 컨테이너 동작 원리 정리
- [ ] DI(Dependency Injection) — 생성자 주입 vs 필드 주입 vs 세터 주입 비교 정리
- [ ] Spring Bean 생명주기 (생성 → 의존관계 주입 → 초기화 → 소멸) 정리
- [ ] ApplicationContext vs BeanFactory 차이 정리
- [ ] Spring Boot 자동 구성(Auto Configuration) 동작 원리 정리 (`@SpringBootApplication`, `@EnableAutoConfiguration`)
- [ ] Tomcat 임베디드 서버 구조 및 요청 처리 흐름 정리
- [ ] 실습: BeanDefinition, @Configuration, @Bean, @Component 직접 등록하며 차이 확인

### 1-2. Spring 핵심 어노테이션 총정리
- [ ] **Bean 등록/설정**: `@Component` / `@Service` / `@Repository` / `@Controller` / `@Configuration` / `@Bean` 차이
- [ ] **DI 관련**: `@Autowired` / `@Qualifier` / `@Primary` / `@Lazy` / `@Value` / `@ConfigurationProperties`
- [ ] **요청 처리**: `@RequestMapping` / `@GetMapping` 등 / `@PathVariable` / `@RequestParam` / `@RequestBody` / `@ResponseBody` / `@ResponseStatus`
- [ ] **트랜잭션**: `@Transactional` 속성 전체 (propagation / isolation / readOnly / rollbackFor / timeout)
- [ ] **스케줄링**: `@Scheduled` / `@EnableScheduling` / cron 표현식
- [ ] **비동기**: `@Async` / `@EnableAsync` 동작 원리
- [ ] **이벤트**: `@EventListener` / `@TransactionalEventListener` 차이
- [ ] **캐시**: `@Cacheable` / `@CachePut` / `@CacheEvict` 동작
- [ ] **테스트**: `@SpringBootTest` / `@WebMvcTest` / `@DataJpaTest` / `@MockBean` / `@SpyBean`
- [ ] **조건부 Bean**: `@ConditionalOnProperty` / `@ConditionalOnMissingBean` / `@Profile`
- [ ] **JPA**: `@Entity` / `@Table` / `@Id` / `@GeneratedValue` / `@Column` / `@OneToMany` / `@ManyToOne` / `@JoinColumn` 정리
- [ ] 실습: 헷갈리는 어노테이션 직접 사용하며 동작 확인

### 1-3. MVC 패턴 & DispatcherServlet
- [ ] DispatcherServlet 동작 흐름 정리 (HandlerMapping → HandlerAdapter → ViewResolver)
- [ ] `@Controller` vs `@RestController` 차이 정리
- [ ] 요청/응답 처리 전체 흐름 정리 (Filter → Interceptor → AOP → Controller → ResponseBody)
- [ ] 실습: 순수 Servlet → Spring MVC 마이그레이션 직접 구현
- [ ] 실습: HandlerInterceptor 커스텀 구현
- [ ] 실습: HandlerMethodArgumentResolver 커스텀 구현

### 1-4. Filter / Interceptor / 쿠키 / 세션

#### 이론
- [ ] **Filter vs Interceptor 완전 정리** — 실행 시점 / 접근 객체 / 예외 처리 / 사용 기준 비교
- [ ] Filter 종류 정리 (`OncePerRequestFilter` vs `GenericFilterBean`)
- [ ] Spring Security Filter Chain과 일반 Filter가 분리되는 구조 정리
- [ ] 쿠키(Cookie) 동작 원리 정리 (`Set-Cookie`, `HttpOnly` / `Secure` / `SameSite` / `Path` / `Domain` / `Expires`)
- [ ] 세션(Session) 동작 원리 정리 (`JSESSIONID`, 서버 메모리 기반 세션 한계)
- [ ] 쿠키 vs 세션 vs JWT 비교 (저장 위치 / 보안 / 확장성)
- [ ] 세션 클러스터링 문제 정리 (Sticky Session vs 세션 복제 vs Redis 중앙화)

#### 실습
- [ ] 실습: `OncePerRequestFilter` 커스텀 필터 구현 (요청 로깅, 인증 토큰 추출)
- [ ] 실습: `FilterRegistrationBean`으로 필터 순서 / URL 패턴 지정
- [ ] 실습: `HandlerInterceptor`로 API 실행 시간 측정 구현 (`preHandle` / `postHandle` / `afterCompletion`)
- [ ] 실습: 쿠키 기반 장바구니 구현 (비로그인 상태 유지)
- [ ] 실습: 서버 세션 기반 로그인 구현 → 수평 확장 시 세션 공유 문제 재현
- [ ] 실습: Spring Session + Redis로 세션 클러스터링 해결

---

## PHASE 2 — RESTful API 설계 & 구현 & 문서화

### 2-1. REST 이론
- [ ] REST 6가지 제약 조건 정리 (Stateless, Uniform Interface, Layered System 등)
- [ ] Richardson Maturity Model (Level 0~3) 정리
- [ ] HTTP 메서드별 멱등성 / 안전성 정리
- [ ] HTTP 상태코드 체계적 정리 (2xx / 3xx / 4xx / 5xx 시나리오별)
- [ ] API Versioning 전략 비교 정리 (URL / Header / Media Type)

### 2-2. RESTful API 구현
- [ ] 실습: RESTful 리소스 설계 원칙에 따른 API 구현 (CRUD)
- [ ] 실습: 표준 응답 포맷 설계 (`ApiResponse` wrapper, 에러 포맷 통일)
- [ ] 실습: `@ControllerAdvice` + `@ExceptionHandler` 글로벌 예외 처리 구현
- [ ] 실습: `@Valid` + `BindingResult` 입력 검증 구현
- [ ] 실습: HATEOAS 적용 (Level 3 REST)
- [ ] 실습: API Versioning 구현 및 비교

### 2-3. 외부 API 연동
- [ ] `RestTemplate` vs `WebClient` vs `OpenFeign` 비교 정리 (동기/비동기, 사용 시점)
- [ ] 실습: `RestTemplate`으로 외부 API 호출 구현
- [ ] 실습: `WebClient`로 논블로킹 외부 API 호출 구현
- [ ] 실습: `OpenFeign`으로 선언적 HTTP 클라이언트 구현
- [ ] 실습: 외부 API 실패 시 Retry / Fallback / Timeout 처리 구현

### 2-4. API 문서화
- [ ] Swagger 2 vs SpringDoc OpenAPI 3 비교 정리
- [ ] 실습: SpringDoc OpenAPI 3 적용 및 커스터마이징
- [ ] 실습: API 문서에 JWT Bearer 인증 헤더 적용
- [ ] 실습: API Versioning별 문서 분리

---

## PHASE 2-B — 예외 처리 & 로깅 전략

### 2B-1. 예외 처리 이론
- [ ] Java 예외 계층 구조 정리 (Checked vs Unchecked, Error vs Exception)
- [ ] Spring 예외 처리 흐름 정리 (`HandlerExceptionResolver` 체인)
- [ ] 비즈니스 예외 vs 시스템 예외 분리 설계 원칙 정리
- [ ] HTTP 상태코드와 예외 매핑 전략 정리

### 2B-2. 예외 처리 구현
- [ ] 실습: 커스텀 비즈니스 예외 계층 설계 (`BaseException` → 도메인별 예외)
- [ ] 실습: `ErrorCode` Enum 설계 (code / message / httpStatus 통합 관리)
- [ ] 실습: `@ControllerAdvice` 글로벌 예외 핸들러 구현
- [ ] 실습: `MethodArgumentNotValidException` 처리 표준화
- [ ] 실습: Spring Security 인증/인가 예외 커스터마이징 (`AuthenticationEntryPoint`, `AccessDeniedHandler`)
- [ ] 실습: 외부 API 호출 예외 처리 패턴 (Retry, Circuit Breaker Fallback)

### 2B-3. 로깅 전략
- [ ] SLF4J / Logback / Log4j2 비교 정리
- [ ] 로그 레벨 전략 정리 (TRACE / DEBUG / INFO / WARN / ERROR 사용 기준)
- [ ] MDC(Mapped Diagnostic Context) 요청 추적 ID 전파 정리
- [ ] 실습: AOP 기반 요청 / 응답 / 예외 자동 로깅 구현
- [ ] 실습: MDC로 요청별 `traceId` 부여 및 로그 자동 포함
- [ ] 실습: 환경별 로그 레벨 분리 (dev=DEBUG, prod=WARN)
- [ ] 실습: Logback JSON 포맷 설정 (ELK 연동 대비)

---

## PHASE 3 — 테스트 주도 개발 (TDD)

### 3-1. 테스트 이론
- [ ] TDD 사이클 정리 (Red → Green → Refactor)
- [ ] 단위 테스트 vs 통합 테스트 vs E2E 테스트 차이 정리
- [ ] 테스트 더블 종류 정리 (Mock / Stub / Spy / Fake / Dummy)
- [ ] `@SpringBootTest` vs `@WebMvcTest` vs `@DataJpaTest` 차이 정리
- [ ] 테스트 픽스처 전략 정리 (Object Mother, Test Data Builder)

### 3-2. TDD 실습
- [ ] 실습: JUnit5 + AssertJ 기본 세팅 및 활용
- [ ] 실습: Mockito를 활용한 단위 테스트 작성
- [ ] 실습: `@WebMvcTest`로 Controller 슬라이스 테스트
- [ ] 실습: `@DataJpaTest`로 Repository 슬라이스 테스트
- [ ] 실습: TestContainers로 실제 DB 통합 테스트 구현
- [ ] 실습: RestAssured로 E2E API 테스트 구현
- [ ] 실습: TDD로 서비스 레이어 처음부터 구현 (테스트 먼저 작성)
- [ ] 실습: 아키텍처 테스트 (ArchUnit으로 레이어 의존성 규칙 검증)

---

## PHASE 4 — 데이터베이스 & JPA

### 4-0. RDBMS 핵심 원리
- [ ] RDBMS 개념 정리 (관계형 모델, 테이블 / 행 / 열 / 기본키 / 외래키)
- [ ] InnoDB 스토리지 엔진 구조 정리 (Buffer Pool / Redo Log / Undo Log / Change Buffer)
- [ ] **MVCC(Multi-Version Concurrency Control) 동작 원리 정리** (Undo Log 기반 버전 관리, 읽기 일관성)
- [ ] **InnoDB Lock 종류 완전 정리**
  - Row Lock: Shared Lock(S) vs Exclusive Lock(X)
  - Gap Lock / Next-Key Lock / Record Lock (팬텀 리드 방지 메커니즘)
  - Table Lock / Intention Lock
  - Auto-Increment Lock
- [ ] **실행 계획(EXPLAIN) 읽는 법 완전 정리**
  - `type` 컬럼: ALL / index / range / ref / eq_ref / const / system 의미
  - `key` / `rows` / `filtered` / `Extra` (Using index / Using filesort / Using temporary) 의미
  - 실행 계획이 나쁜 패턴 목록 정리 (ALL + rows 큰 경우, filesort, temporary)
- [ ] Join 알고리즘 정리 (Nested Loop / Hash Join / Sort Merge Join)
- [ ] 실습: 동일 쿼리를 EXPLAIN으로 분석하며 실행 계획 개선 전후 비교

### 4-1. 데이터베이스 설계 이론
- [ ] 정규화 (1NF ~ 3NF, BCNF) 이론 정리
- [ ] 역정규화 적용 기준 및 전략 정리
- [ ] 실습: 동일 데이터셋으로 정규화 / 역정규화 스키마 각각 설계
- [ ] 실습: 대용량 데이터 삽입 후 쿼리 성능 비교 (`EXPLAIN ANALYZE`)
- [ ] 파티셔닝(Partitioning) vs 샤딩(Sharding) 개념 정리 (수직 vs 수평 분할)
- [ ] Read Replica(읽기 전용 레플리카) 개념 및 Spring에서 라우팅 정리
- [ ] 실습: MySQL Replication 구성 및 `@Transactional(readOnly=true)` 라우팅 구현

### 4-1-B. 인덱스 심화
- [ ] **B-Tree 인덱스 내부 구조 정리** (페이지 / 리프 노드 / 루트 노드, 탐색 방식)
- [ ] **InnoDB 클러스터드 인덱스 정리** (PK가 곧 데이터 정렬 순서, 세컨더리 인덱스가 PK를 포함하는 이유)
- [ ] **인덱스 설계 기준 정리**
  - 카디널리티(Cardinality): 왜 선택도가 높은 컬럼에 인덱스를 걸어야 하는가
  - 복합 인덱스 컬럼 순서 결정 기준 (선택도 높은 것 먼저 vs 범위 조건 처리)
  - 인덱스가 오히려 느려지는 경우 (낮은 선택도, 쓰기 부하, 풀스캔이 나은 경우)
- [ ] **Covering Index 정리** (인덱스만으로 쿼리 완성, `Using index` 확인)
- [ ] **복합 인덱스(Composite Index) 정리**
  - 선두 컬럼 원칙 (Leftmost Prefix Rule)
  - `(a, b, c)` 인덱스에서 `WHERE b = ?` 가 인덱스를 못 타는 이유
- [ ] **인덱스 종류 비교 정리** (B-Tree / Hash / Full-Text / Spatial)
- [ ] **Index Merge 정리** (두 인덱스를 합치는 경우, 오히려 비효율적인 경우)
- [ ] **인덱스와 정렬 정리** (`ORDER BY` / `GROUP BY`가 인덱스를 활용하는 조건)
- [ ] **실습: 인덱스 성능 비교 시리즈**
  - 인덱스 없음 vs 단일 인덱스 vs 복합 인덱스 — 동일 쿼리 실행시간 비교
  - 복합 인덱스 컬럼 순서 바꾸었을 때 성능 차이 비교
  - Covering Index 적용 전후 비교 (`Extra: Using index` 확인)
  - 낮은 카디널리티 컬럼에 인덱스 걸었을 때 성능이 오히려 나쁜 케이스 재현
  - `FORCE INDEX` / `IGNORE INDEX` 힌트로 인덱스 강제 지정 실습
- [ ] 실습: 운영 중 인덱스 추가 (`ALTER TABLE ... ADD INDEX`, 락 영향 확인)

### 4-1-C. Pagination & 대용량 조회 전략
- [ ] **offset 페이징 vs cursor(keyset) 페이징 비교 정리**
  - offset 방식의 한계 (LIMIT 10 OFFSET 100000 → 100010행을 읽고 버리는 이유)
  - cursor 방식의 원리 (마지막 ID 기준으로 `WHERE id < ?` 조건)
- [ ] `Page<T>` vs `Slice<T>` vs `List` 차이 정리 (count 쿼리 발생 여부)
- [ ] **실습: offset 페이징 성능 저하 재현** (100만 건 데이터, OFFSET 증가에 따른 속도 저하 측정)
- [ ] **실습: cursor 기반 무한 스크롤 API 구현** 및 offset 대비 성능 비교
- [ ] 실습: `Pageable` + `@PageableDefault` 사용법 및 정렬 처리
- [ ] 실습: count 쿼리 분리 최적화 (`@Query` + `countQuery` 속성)
- [ ] 대용량 데이터 조회 최적화 전략 정리 (커버링 인덱스 + 서브쿼리 방식)

### 4-2. JPA 개념 & 영속성 컨텍스트
- [ ] **ORM(Object-Relational Mapping) 개념 정리** (SQL 직접 작성 vs ORM 방식 비교, 장단점)
- [ ] **JPA vs Hibernate 관계 정리** (JPA는 표준 인터페이스, Hibernate는 구현체)
- [ ] **Entity 상태 4가지 정리** (비영속 / 영속 / 준영속 / 삭제) 및 상태 전환 흐름
- [ ] **영속성 컨텍스트 동작 원리 정리**
  - 1차 캐시 (같은 트랜잭션 내 동일 ID 재조회 시 SQL 안 나가는 이유)
  - 변경 감지 (Dirty Checking): 스냅샷 vs 현재 상태 비교
  - 쓰기 지연 (Write-Behind): 플러시 시점
  - 지연 로딩 (Lazy Loading): 프록시 객체 동작
- [ ] **플러시(Flush) 시점 정리** (트랜잭션 커밋 / JPQL 실행 / 명시적 flush())
- [ ] 즉시 로딩(EAGER) vs 지연 로딩(LAZY) 및 N+1 문제 정리
- [ ] 실습: N+1 문제 재현 → fetch join / `@EntityGraph`로 해결
- [ ] **연관관계 매핑 정리** (`@OneToMany` / `@ManyToOne` / `@ManyToMany` / `@OneToOne` + `mappedBy` / `cascade` / `orphanRemoval`)
- [ ] **연관관계 주인(Owner) 정리** (외래키를 가진 쪽이 주인, `mappedBy`는 읽기 전용)
- [ ] JPA 상속 매핑 전략 정리 (SINGLE_TABLE / JOINED / TABLE_PER_CLASS)
- [ ] 실습: 상속 매핑 전략별 쿼리 / 성능 비교
- [ ] 복합키 매핑 정리 (`@IdClass` vs `@EmbeddedId`)
- [ ] `@Embedded` / `@Embeddable` 값 타입 정리
- [ ] Auditing 정리 (`@CreatedDate` / `@LastModifiedDate` / `@CreatedBy` / `@LastModifiedBy`)
- [ ] 실습: `BaseEntity`에 Auditing 적용
- [ ] **Spring Data JPA 정리** (`JpaRepository` 메서드 규칙, `@Query`, `Pageable`)
- [ ] JPQL vs QueryDSL vs Native Query 비교 정리
- [ ] 실습: QueryDSL 세팅 및 동적 쿼리 구현
- [ ] Batch Insert 전략 정리 (`saveAll` 성능 문제, JDBC Batch 설정)
- [ ] 실습: 대량 데이터 Insert 방식별 성능 비교 (JPA saveAll vs JDBC Batch vs JDBC Template)

### 4-3. 동시성 제어
- [ ] 동시성 문제 유형 정리 (Race Condition, Lost Update, Phantom Read)
- [ ] 낙관적 락(Optimistic Lock) vs 비관적 락(Pessimistic Lock) 이론 정리
- [ ] 실습: 재고 감소 동시 100명 요청 문제 재현
- [ ] 실습: `@Version` 기반 낙관적 락 구현 및 충돌 재시도 처리
- [ ] 실습: `@Lock(PESSIMISTIC_WRITE)` 비관적 락 구현
- [ ] 실습: Redisson 분산 락으로 멀티 인스턴스 환경 동시성 제어
- [ ] 실습: 방법별 성능 비교 (낙관적 vs 비관적 vs 분산 락)

### 4-4. 트랜잭션 관리
- [ ] 트랜잭션 ACID 속성 정리
- [ ] Spring `@Transactional` 동작 원리 정리 (프록시 기반)
- [ ] 트랜잭션 격리 수준 4단계 정리 (READ UNCOMMITTED ~ SERIALIZABLE)
- [ ] 실습: 격리 수준별 Dirty Read / Non-Repeatable Read / Phantom Read 재현
- [ ] 트랜잭션 전파(Propagation) 7가지 옵션 이론 정리
- [ ] 실습: REQUIRED / REQUIRES_NEW / NESTED 전파 옵션 동작 직접 확인
- [ ] 실습: 자기 호출(Self-Invocation) 문제 재현 및 해결

### 4-5. DB 마이그레이션 (Flyway)
- [ ] Flyway vs Liquibase 비교 정리
- [ ] Flyway 동작 원리 정리 (버전 관리, checksum, `flyway_schema_history`)
- [ ] 실습: Flyway 세팅 및 마이그레이션 스크립트 작성 (V1, V2...)
- [ ] 실습: 운영 중 컬럼 추가 / 이름 변경 안전하게 마이그레이션하는 전략 구현

### 4-6. 커넥션 풀 (Connection Pool)
- [ ] 커넥션 풀 개념 및 필요성 정리 (커넥션 생성 비용)
- [ ] HikariCP 내부 동작 원리 정리 (풀 사이즈 결정 공식 포함)
- [ ] HikariCP 주요 설정 항목 정리 (`maximumPoolSize` / `connectionTimeout` / `idleTimeout` / `maxLifetime`)
- [ ] 실습: HikariCP 세팅 및 커넥션 풀 모니터링 구현
- [ ] 실습: 풀 사이즈별 (5 / 10 / 20 / 50) 동시 요청 성능 비교 (k6)
- [ ] 실습: 커넥션 풀 고갈 상황 재현 및 타임아웃 처리 확인

### 4-7. 파일 업로드 & 스토리지
- [ ] Multipart 파일 업로드 처리 방식 정리
- [ ] 실습: Spring Multipart 파일 업로드 구현 (로컬 저장)
- [ ] 실습: AWS S3 파일 업로드 / 다운로드 구현
- [ ] 실습: S3 Presigned URL 발급으로 클라이언트 직접 업로드 구현
- [ ] 실습: 이미지 리사이징 처리 (Thumbnailator)

---

## PHASE 5 — AOP (관점 지향 프로그래밍)

### 5-1. AOP 이론
- [ ] AOP 핵심 개념 정리 (Aspect / Advice / Pointcut / JoinPoint / Weaving)
- [ ] Spring AOP vs AspectJ 차이 정리 (프록시 기반 vs 바이트코드 조작)
- [ ] JDK Dynamic Proxy vs CGLIB 프록시 차이 정리

### 5-2. AOP 구현
- [ ] 실습: `@Around` / `@Before` / `@After` 어드바이스 구현
- [ ] 실습: 메서드 실행 시간 측정 AOP 구현
- [ ] 실습: 커스텀 어노테이션 기반 로깅 AOP 구현 (`@Loggable`)
- [ ] 실습: 커스텀 어노테이션 기반 권한 체크 AOP 구현 (`@RequireRole`)
- [ ] 실습: 트랜잭션 AOP 흐름 직접 추적 (디버깅으로 프록시 확인)

---

## PHASE 6 — 커스텀 어노테이션

### 6-1. 어노테이션 동작 원리
- [ ] Java 어노테이션 메커니즘 정리 (`@Retention` / `@Target` / `@Documented` / `@Inherited`)
- [ ] 런타임 리플렉션으로 어노테이션 읽는 방식 정리
- [ ] Spring이 어노테이션을 처리하는 방식 정리 (BeanPostProcessor, AOP 프록시)
- [ ] 메타 어노테이션 / 합성 어노테이션(Composed Annotation) 개념 정리

### 6-2. 커스텀 어노테이션 구현
- [ ] 실습: 커스텀 유효성 검증 어노테이션 구현 (`@PhoneNumber`, `@BusinessNumber`, `@Enum`)
- [ ] 실습: AOP 연동 커스텀 어노테이션 구현 (`@Loggable`, `@RequireRole`, `@RateLimit`)
- [ ] 실습: `@ConfigurationProperties` 커스텀 설정 바인딩 구현

### 6-3. MSA 환경 커스텀 어노테이션 — @CurrentUser
- [ ] MSA 사용자 식별 흐름 정리 (Gateway JWT 검증 → `X-User-Id` Header 전파 → 각 서비스 수신)
- [ ] `HandlerMethodArgumentResolver` 동작 원리 정리
- [ ] 실습: Gateway에서 JWT 검증 후 `X-User-Id` / `X-User-Role` 헤더 주입 구현
- [ ] 실습: 각 서비스에서 `@CurrentUser` + `ArgumentResolver` 구현
  ```java
  // 목표
  public ResponseEntity<?> getMyOrders(@CurrentUser UserContext user) { ... }
  ```
- [ ] 실습: `@CurrentUser`를 공통 모듈(common)에 두고 재사용하는 구조 구현
- [ ] 실습: 모놀리식(SecurityContext) vs MSA(Header) 환경 전략 분기 구현

---

## PHASE 6-B — 멀티 모듈 프로젝트 구조

- [ ] 멀티 모듈 구조 필요성 및 설계 원칙 정리 (계층 간 의존성 방향)
- [ ] 실습: Gradle 멀티 모듈 프로젝트 세팅 (`api` / `domain` / `infra` / `common` 분리)
- [ ] 실습: 모듈 간 의존성 설정 및 순환 참조 방지
- [ ] 실습: 공통 모듈(common)에 예외 / 응답 포맷 / 유틸 / `@CurrentUser` 분리

---

## PHASE 7 — Spring Security & 인증/인가

### 7-1. Security 동작 원리
- [ ] Security Filter Chain 전체 흐름 정리 (주요 필터 역할 각각)
- [ ] Authentication / Authorization 차이 및 처리 흐름 정리
- [ ] `SecurityContext` / `SecurityContextHolder` 동작 정리
- [ ] `PasswordEncoder` 종류 및 BCrypt 동작 정리

### 7-2. JWT 기반 인증 구현
- [ ] JWT 구조 정리 (Header.Payload.Signature) 및 장단점
- [ ] Access Token / Refresh Token 전략 정리
- [ ] 실습: JWT 발급 / 검증 / 재발급 필터 구현
- [ ] 실습: Redis 기반 Refresh Token 저장 및 블랙리스트 구현
- [ ] 실습: Redis 기반 JWT 로그아웃 처리 구현

### 7-3. 보안 취약점 방어
- [ ] OWASP Top 10 각 항목 정리 및 Spring에서 방어 방법 정리
- [ ] CORS 동작 원리 정리 (Preflight, Simple Request, `@CrossOrigin` vs 글로벌 설정)
- [ ] CSRF 공격 원리 및 방어 정리 (Spring Security CSRF Token, SameSite 쿠키)
- [ ] XSS 공격 원리 및 방어 정리 (Content-Type 검증, 입력 이스케이프)
- [ ] SQL Injection 방어 정리 (PreparedStatement, JPA 파라미터 바인딩)
- [ ] 보안 HTTP 헤더 정리 (`X-Frame-Options` / `X-Content-Type-Options` / `Strict-Transport-Security` / `Content-Security-Policy`)
- [ ] 실습: Spring Security 설정으로 보안 헤더 일괄 적용
- [ ] 실습: CORS 설정 (허용 Origin / Method / Header 세분화)
- [ ] 실습: CSRF 토큰 적용 (REST API vs Form 기반 차이 확인)
- [ ] 실습: SQL Injection / XSS 공격 재현 및 방어 구현

### 7-4. Rate Limiting & API Throttling
- [ ] Rate Limiting 알고리즘 정리 (Token Bucket / Leaky Bucket / Fixed Window / Sliding Window)
- [ ] 실습: Bucket4j + Redis 기반 API Rate Limiting 구현
- [ ] 실습: `@RateLimit` 커스텀 어노테이션으로 엔드포인트별 제한
- [ ] 실습: Spring Cloud Gateway Rate Filter 구현 (MSA 환경)

### 7-5. 소셜 로그인 (OAuth2)
- [ ] OAuth2 Authorization Code Flow 이론 정리
- [ ] 실습: Google OAuth2 소셜 로그인 구현 (Spring Security OAuth2 Client)
- [ ] 실습: Kakao / Naver OAuth2 커스텀 Provider 구현
- [ ] 실습: 소셜 로그인 + JWT 연동 (OAuth2 인증 후 JWT 발급)

---

## PHASE 7-B — 알림 서비스 (Notification)

### 7B-1. 이론
- [ ] 알림 전송 방식 비교 정리 (이메일 / 푸시 / 실시간 / SMS)
- [ ] Redis Pub/Sub vs Kafka 이벤트 방식 비교 정리
- [ ] WebSocket vs SSE(Server-Sent Events) 비교 정리

### 7B-2. 이메일 알림
- [ ] 실습: JavaMailSender + SMTP 이메일 발송 구현
- [ ] 실습: Thymeleaf HTML 이메일 템플릿 구현
- [ ] 실습: 비동기 이메일 발송 처리 (`@Async`)

### 7B-3. 실시간 알림 (WebSocket / SSE)
- [ ] 실습: SSE로 서버 → 클라이언트 단방향 실시간 알림 구현
- [ ] 실습: WebSocket + STOMP로 양방향 채팅 구현
- [ ] 실습: Redis Pub/Sub으로 멀티 인스턴스 환경 실시간 알림 브로드캐스트

### 7B-4. FCM 푸시 알림
- [ ] 실습: Firebase Admin SDK 연동
- [ ] 실습: FCM 토큰 저장 및 개별 / 토픽 푸시 발송 구현
- [ ] 실습: Kafka 이벤트 기반 비동기 푸시 파이프라인 구현

---

## PHASE 7-C — Redis 심화

### 7C-1. 이론
- [ ] Redis 자료구조 전체 정리 (String / Hash / List / Set / ZSet / Stream)
- [ ] Redis 영속성 정리 (RDB vs AOF)
- [ ] Redis Cluster vs Sentinel 차이 정리
- [ ] 캐시 전략 정리 (Cache-Aside / Write-Through / Write-Behind)
- [ ] 캐시 문제 유형 정리 (Cache Stampede / Cache Penetration / Cache Avalanche)

### 7C-2. 실습
- [ ] 실습: Redis 자료구조별 활용 (랭킹 ZSet, 최근 본 상품 List, 태그 Set)
- [ ] 실습: 캐시 Stampede 방지 구현 (Mutex Lock, PER 알고리즘)
- [ ] 실습: Redis Streams 기반 간단한 이벤트 큐 구현
- [ ] 실습: 세션 클러스터링 구현 (Spring Session + Redis)
- [ ] 실습: Redisson 분산 락 구현 (4-3에서 이어서)

---

## PHASE 8 — 디자인 패턴

### 8-1. 이론
- [ ] GoF 23가지 패턴 분류 정리 (생성 / 구조 / 행위)
- [ ] Spring 내부에서 사용되는 패턴 목록 정리 (Template Method, Proxy, Strategy 등)

### 8-2. 실습
- [ ] 실습: Strategy 패턴 — 결제 수단 전략 구현
- [ ] 실습: Template Method 패턴 — 알림 발송 추상화
- [ ] 실습: Factory / Abstract Factory 패턴 구현
- [ ] 실습: Decorator 패턴 — 응답 가공 체인 구현
- [ ] 실습: Observer 패턴 — Spring Event 기반 구현 (`ApplicationEventPublisher`)
- [ ] 실습: Builder 패턴 — Lombok `@Builder` 없이 직접 구현 후 비교
- [ ] 실습: Proxy 패턴 — Spring AOP 없이 직접 프록시 구현
- [ ] 실습: Chain of Responsibility — 필터 / 검증 체인 구현

---

## PHASE 9 — 클린 코드 & 성능 최적화

### 9-1. 클린 코드 원칙
- [ ] SOLID 원칙 각각 정리 + Spring에서 위반 사례 / 적용 사례 분석
- [ ] Clean Code 핵심 원칙 정리 (명명 / 함수 / 주석 / 오류 처리)
- [ ] 코드 스멜(Code Smell) 종류 정리 및 리팩터링 기법 정리
- [ ] 실습: 기존 코드 SOLID 위반 지점 찾아 리팩터링

### 9-2. Java / JVM 성능 최적화
- [ ] String / StringBuilder / StringBuffer 성능 차이 정리
- [ ] Stream 병렬 처리 주의점 정리 (ForkJoinPool, 스레드 안전성)
- [ ] 불필요한 객체 생성 패턴 정리 (Autoboxing, 임시 객체)
- [ ] 실습: JVM 힙 사이즈 조정 및 GC 로그 분석으로 메모리 튜닝
- [ ] 실습: async-profiler로 CPU / 메모리 핫스팟 분석
- [ ] 실습: 비동기 처리 구현 (`@Async`, `CompletableFuture`) 전후 응답시간 비교

### 9-3. 애플리케이션 캐싱
- [ ] 캐시 적용 대상 선정 기준 정리 (읽기 빈도 / 데이터 변경 빈도 / 연산 비용)
- [ ] 실습: `@Cacheable` + 로컬 캐시(Caffeine) 적용
- [ ] 실습: Redis 분산 캐시 적용 전후 응답시간 비교
- [ ] 실습: 캐시 무효화 전략 구현 (`@CacheEvict`, TTL 설정)

### 9-4. 스케줄러 & 배치
- [ ] `@Scheduled` 주의점 정리 (단일 스레드 기본, 클러스터 환경 중복 실행 문제)
- [ ] Spring Batch 핵심 개념 정리 (Job / Step / Chunk / ItemReader / ItemWriter)
- [ ] 실습: `@Scheduled` + ShedLock으로 분산 환경 중복 실행 방지 구현
- [ ] 실습: Spring Batch로 대용량 데이터 마이그레이션 구현 (Chunk 처리)
- [ ] 실습: Batch 실패 시 재시작 / 재처리 전략 구현

---

## PHASE 10 — Docker & 컨테이너 환경

### 10-1. Docker 이론
- [ ] 컨테이너 vs VM 차이 정리
- [ ] Docker 이미지 레이어 구조 및 Dockerfile 최적화 원칙 정리
- [ ] Docker 네트워크 모드 정리 (bridge / host / overlay / none)
- [ ] Docker Compose 개념 및 서비스 오케스트레이션 정리

### 10-2. 실습
- [ ] 실습: Spring Boot 앱 Dockerfile 작성 (멀티 스테이지 빌드)
- [ ] 실습: Docker Compose로 Spring + MySQL + Redis 환경 구성
- [ ] 실습: 환경변수로 `application.yml` 설정값 외부화
- [ ] 실습: `.dockerignore` 최적화 및 이미지 경량화
- [ ] 실습: Docker 네트워크 직접 생성 및 컨테이너 간 통신 확인
- [ ] 실습: Docker 볼륨으로 MySQL 데이터 영속화

---

## PHASE 10-B — 컨테이너 오케스트레이션

### 10B-1. 이론
- [ ] 컨테이너 오케스트레이션 필요성 정리 (단순 Docker Compose 한계)
- [ ] Docker Swarm vs k3s vs k8s 비교 정리 (복잡도 / 기능 / 적합 규모)
- [ ] k8s 핵심 오브젝트 정리 (Pod / Deployment / Service / Ingress / ConfigMap / Secret / PVC)
- [ ] k8s 네트워킹 모델 정리 (ClusterIP / NodePort / LoadBalancer / Ingress)
- [ ] Liveness Probe vs Readiness Probe vs Startup Probe 차이 정리
- [ ] k8s Rolling Update / Rollback 동작 원리 정리

### 10B-2. Docker Swarm 실습
- [ ] 실습: Docker Swarm 클러스터 구성 (Manager 1 + Worker 2, 로컬 시뮬레이션)
- [ ] 실습: Stack 배포 (`docker stack deploy`) — Spring + MySQL + Redis 서비스 구성
- [ ] 실습: 서비스 스케일 아웃 / 인 (`docker service scale`)
- [ ] 실습: Rolling Update 및 Rollback 실습
- [ ] 실습: Swarm Overlay 네트워크로 서비스 간 통신 확인

### 10B-3. k3s 실습 (경량 k8s)
- [ ] 실습: k3s 단일 노드 설치 및 kubectl 연결
- [ ] 실습: Spring Boot 앱 Deployment + Service 배포
- [ ] 실습: ConfigMap / Secret으로 환경변수 외부화
- [ ] 실습: Ingress(Traefik) 설정으로 도메인 라우팅
- [ ] 실습: Liveness / Readiness Probe 설정 및 실패 시 재시작 확인
- [ ] 실습: HPA(Horizontal Pod Autoscaler)로 부하 기반 자동 스케일 아웃
- [ ] 실습: Rolling Update 전략 설정 및 무중단 배포 확인
- [ ] 실습: PersistentVolume으로 MySQL 데이터 영속화

### 10B-4. k8s 심화 (선택 — AWS EKS 연계)
- [ ] 실습: Helm Chart로 애플리케이션 패키징 및 배포
- [ ] 실습: AWS EKS 클러스터 생성 및 kubectl 연결
- [ ] 실습: EKS + ALB Ingress Controller 설정
- [ ] 실습: ArgoCD 연동으로 GitOps 자동 배포 파이프라인 구성
- [ ] 실습: Istio Service Mesh 설치 및 트래픽 관리 (Canary 배포)

---

## PHASE 11 — AI 연동 (Spring AI / LangChain)

### 11-1. 이론
- [ ] LLM API 호출 방식 정리 (Prompt Engineering 기초)
- [ ] LangChain4j vs Spring AI 비교 정리
- [ ] RAG(Retrieval Augmented Generation) 개념 정리
- [ ] 벡터 DB 개념 정리 (임베딩, 유사도 검색)

### 11-2. 실습
- [ ] 실습: Claude API / OpenAI API Spring Boot 연동
- [ ] 실습: Spring AI로 Chat API 엔드포인트 구현
- [ ] 실습: 문서 기반 RAG 파이프라인 구현 (벡터 DB 연동)
- [ ] 실습: 대화 이력 관리 (Redis 기반 세션)
- [ ] 실습: 스트리밍 응답 구현 (SSE 기반)

---

## PHASE 12 — NGINX & SSL & 네트워크

### 12-1. 이론
- [ ] NGINX 동작 원리 정리 (이벤트 기반 비동기 아키텍처)
- [ ] Reverse Proxy 개념 및 로드밸런싱 알고리즘 정리 (RR / Least Conn / IP Hash)
- [ ] TLS/SSL 핸드셰이크 과정 정리 (대칭키 / 비대칭키)
- [ ] HTTP/1.1 vs HTTP/2 vs HTTP/3 차이 정리 (멀티플렉싱, QUIC)

### 12-2. 실습
- [ ] 실습: NGINX Reverse Proxy 설정 (Spring Boot 앞단 배치)
- [ ] 실습: NGINX upstream 헬스체크 / keepalive / 버퍼 튜닝
- [ ] 실습: Certbot으로 Let's Encrypt SSL 인증서 발급 및 자동 갱신
- [ ] 실습: HTTPS 리다이렉트 및 HSTS 설정
- [ ] 실습: HTTP/2 활성화 및 성능 비교
- [ ] 실습: Cloudflare Tunnel로 로컬 PC와 외부 연결

---

## PHASE 13 — MSA & Spring Cloud

### 13-1. MSA 이론
- [ ] 모놀리식 vs MSA 장단점 정리
- [ ] MSA 분리 기준 정리 (도메인 중심 설계, Bounded Context)
- [ ] 서비스 간 통신 방식 정리 (동기: REST / gRPC, 비동기: 이벤트)
- [ ] 분산 트랜잭션 문제 정리 (왜 `@Transactional`이 MSA에서 안 통하는가)
- [ ] 2PC(Two-Phase Commit) 원리 및 한계 정리
- [ ] Saga 패턴 두 방식 비교 정리 (Choreography vs Orchestration)
- [ ] 보상 트랜잭션(Compensating Transaction) 설계 원칙 정리

### 13-1-B. Saga & 보상 트랜잭션 실습
> 시나리오: 주문 서비스 → 결제 서비스 → 재고 서비스 → 배송 서비스

- [ ] 실습: **Choreography Saga** — Kafka 이벤트 기반 보상 트랜잭션 구현
  - `OrderCreated` → 결제 시도 → `PaymentFailed` → 주문 취소 보상 이벤트 흐름
- [ ] 실습: **Orchestration Saga** — Orchestrator 서비스가 흐름 제어하는 방식 구현
- [ ] 실습: 보상 트랜잭션 멱등성 보장 구현 (중복 이벤트 처리 방지)
- [ ] 실습: Saga 실패 시 부분 완료 상태 추적 및 모니터링 구현
- [ ] 실습: Choreography vs Orchestration 복잡도 / 디버깅 난이도 비교 정리

### 13-2. Spring Cloud 구성
- [ ] 실습: Eureka Server 구성 및 서비스 등록 / 발견
- [ ] 실습: Spring Cloud Gateway로 API Gateway 구현
- [ ] 실습: Config Server로 설정 중앙화
- [ ] 실습: Feign Client로 서비스 간 통신 구현
- [ ] 실습: Circuit Breaker (Resilience4j) 구현
- [ ] 실습: 모놀리식 프로젝트를 MSA로 분할 (도메인별 서비스 분리)

### 13-3. 수평 확장 & 성능 비교
- [ ] 실습: Docker Compose로 동일 서비스 인스턴스 3개 실행
- [ ] 실습: NGINX 로드밸런싱으로 트래픽 분산
- [ ] 실습: k6로 단일 인스턴스 vs 3중화 성능 비교
- [ ] 실습: 병목 지점 분석 및 튜닝

---

## PHASE 14 — 검색 & 메시징 인프라

### 14-1. Elasticsearch
- [ ] ES 핵심 개념 정리 (Index / Document / Shard / Replica / Inverted Index)
- [ ] RDBMS 전문 검색 vs ES 검색 성능 비교 계획 수립
- [ ] 실습: Spring Data Elasticsearch 연동
- [ ] 실습: 형태소 분석기 적용 (nori analyzer)
- [ ] 실습: RDBMS LIKE 검색 vs ES 검색 성능 비교 (대용량 데이터)

### 14-2. Apache Kafka
- [ ] Kafka 아키텍처 정리 (Broker / Topic / Partition / Consumer Group / Offset)
- [ ] Kafka vs RabbitMQ 비교 정리
- [ ] Kafka 메시지 전달 보장 수준 정리 (At Most Once / At Least Once / Exactly Once)
- [ ] 실습: Spring Kafka 기반 Producer / Consumer 구현
- [ ] 실습: MSA 서비스 간 비동기 이벤트 통신 구현
- [ ] 실습: Kafka + Spark 스트리밍 파이프라인 구현 (로그 집계)
- [ ] 실습: Dead Letter Queue(DLQ) 패턴 구현
- [ ] 실습: Exactly Once Semantics(EOS) 트랜잭션 프로듀서 구현

---

## PHASE 15 — 모니터링 & 관찰 가능성 (Observability)

### 15-1. 이론
- [ ] Observability 3대 요소 정리 (Metrics / Logs / Traces)
- [ ] SLI / SLO / SLA / Error Budget 개념 정리
- [ ] 알림(Alert) 설계 원칙 정리 (증상 기반 vs 원인 기반)

### 15-2. 실습
- [ ] 실습: Spring Actuator + Micrometer + Prometheus 세팅
- [ ] 실습: Grafana 대시보드 구성 (JVM / DB 커넥션 풀 / API 응답시간 / 에러율)
- [ ] 실습: Liveness / Readiness Endpoint 설정 및 k8s Probe 연동
- [ ] 실습: ELK Stack 로그 파이프라인 구성 (Logstash → Elasticsearch → Kibana)
- [ ] 실습: Zipkin / Jaeger로 분산 추적(Distributed Tracing) 구현
- [ ] 실습: Grafana Alert → Slack 연동
- [ ] 실습: 커스텀 메트릭 등록 (Micrometer `Counter` / `Gauge` / `Timer`)

---

## PHASE 15-B — CI/CD 파이프라인

### 15B-1. 이론
- [ ] CI/CD 개념 정리 (Continuous Integration / Delivery / Deployment 차이)
- [ ] GitHub Actions vs Jenkins vs GitLab CI 비교 정리
- [ ] GitOps 개념 정리 (ArgoCD 기반)
- [ ] 브랜치 전략 정리 (Git Flow vs GitHub Flow vs Trunk-Based)
- [ ] 배포 전략 정리 (Blue-Green / Canary / Rolling / Recreate)

### 15B-2. GitHub Actions 실습
- [ ] 실습: PR 시 자동 테스트 실행 워크플로우 구성
- [ ] 실습: main 브랜치 머지 시 Docker 이미지 빌드 → ECR push 파이프라인
- [ ] 실습: ECS / EC2 자동 배포 파이프라인 구성
- [ ] 실습: 환경별 배포 분리 (dev / staging / prod)
- [ ] 실습: Slack 배포 알림 연동

### 15B-3. Jenkins 실습
- [ ] 실습: Jenkins 서버 Docker로 구성
- [ ] 실습: Jenkinsfile 기반 파이프라인 구성 (Build → Test → Deploy)
- [ ] 실습: GitHub Webhook 연동으로 자동 트리거

### 15B-4. 코드 품질 자동화
- [ ] 실습: SonarQube 연동 (정적 분석 / 코드 커버리지)
- [ ] 실습: Jacoco 코드 커버리지 리포트 생성 및 기준 설정
- [ ] 실습: PR 시 커버리지 미달이면 머지 차단하는 워크플로우 구성

---

## PHASE 16 — AWS 배포 & 인프라

### 16-1. AWS 기초 이론
- [ ] AWS 핵심 서비스 정리 (EC2 / RDS / ElastiCache / S3 / ECR / ECS / EKS / SQS / CloudFront / WAF)
- [ ] IAM 이론 정리 (User / Role / Policy / 최소 권한 원칙 / AssumeRole)
- [ ] VPC 구조 정리 (Public/Private Subnet / NAT Gateway / Security Group / NACL)
- [ ] AWS Well-Architected Framework 5대 원칙 정리

### 16-2. 핵심 인프라 실습
- [ ] 실습: IAM 사용자 및 역할 설정 (EC2 → RDS 접근 Role, 최소 권한)
- [ ] 실습: VPC / Subnet / Security Group 직접 설계
- [ ] 실습: EC2에 Docker 기반 Spring Boot 배포
- [ ] 실습: RDS (MySQL) + ElastiCache (Redis) 연결
- [ ] 실습: Parameter Store / Secrets Manager로 민감 정보 관리
- [ ] 실습: S3 파일 업로드 / 다운로드 구현
- [ ] 실습: CloudFront + S3로 정적 파일 CDN 배포

### 16-3. 컨테이너 배포 실습
- [ ] 실습: ECR + ECS(Fargate)로 컨테이너 배포
- [ ] 실습: GitHub Actions CI/CD 파이프라인 구성 (빌드 → ECR push → ECS 배포)
- [ ] 실습: Route53 + ACM으로 커스텀 도메인 + HTTPS 적용
- [ ] 실습: Auto Scaling Group으로 수평 확장 설정

### 16-4. 보안 & 고가용성
- [ ] 실습: AWS WAF로 IP 차단 / SQL Injection / Rate Limiting 규칙 설정
- [ ] 실습: ALB Access Log → S3 → Athena로 접근 로그 분석
- [ ] 실습: Multi-AZ RDS 설정 및 Failover 테스트

---

## PHASE 17 — 성능 테스트 & 분석

### 17-1. 이론
- [ ] 성능 테스트 종류 정리 (Load / Stress / Soak / Spike / Scalability)
- [ ] 주요 성능 지표 정리 (TPS / Latency / Throughput / Error Rate / Apdex)
- [ ] 병목 지점 유형 정리 (CPU / 메모리 / DB / 네트워크 / 스레드)

### 17-2. 실습
- [ ] 실습: k6 스크립트로 주요 API 부하 테스트
- [ ] 실습: k6 시나리오 작성 (점진적 부하 증가 / 스파이크)
- [ ] 실습: async-profiler / IntelliJ Profiler로 CPU / 메모리 핫스팟 분석
- [ ] 실습: DB Slow Query 로그 분석 및 인덱스 최적화
- [ ] 실습: 캐싱 적용 전후 성능 비교
- [ ] 실습: **단계별 성능 비교 리포트 작성**
  - 단일 인스턴스 → 수평 확장 → 캐시 도입 → ES 도입 → 비동기 처리 도입

---

## PHASE 18 — 장애 대응 & 트러블슈팅 & 유지보수

> 운영 환경에서 실제로 무엇을 보고, 어떻게 판단하고, 어떻게 조치하는지

### 18-1. 유지보수 관점 이론

#### 무엇을 보고 판단하는가
- [ ] **운영 대시보드 필수 지표 정리**
  - JVM: Heap 사용률 / GC 빈도 및 Stop-The-World 시간 / 스레드 수
  - API: 응답시간 P50 / P95 / P99 / 에러율 / TPS
  - DB: 커넥션 풀 사용률 / Slow Query 빈도 / Lock Wait 시간
  - 인프라: CPU / 메모리 / 디스크 I/O / 네트워크 대역폭
- [ ] **알림(Alert) 임계값 설계 원칙 정리** (무엇을 기준으로 PagerDuty/Slack 알림을 울리는가)
- [ ] **로그 레벨과 운영 로그 읽는 법 정리** (ERROR / WARN 로그를 보는 순서와 의미)
- [ ] **분산 추적(Trace ID)으로 요청 흐름 따라가는 법 정리**

#### 장애 대응 프로세스
- [ ] 장애 등급 분류 기준 정리 (P0 ~ P3, 서비스 영향도 기반)
- [ ] 장애 대응 절차 정리 (감지 → 확인 → 격리 → 임시 조치 → 근본 원인 분석 → 항구 조치)
- [ ] 사후 분석(Post-Mortem) 작성 방법 정리 (타임라인 / 원인 / 재발 방지)
- [ ] 런북(Runbook) 작성 방법 정리 (반복 장애 대응 절차 문서화)

### 18-2. 장애 유형별 트러블슈팅 실습

#### JVM / 애플리케이션 장애
- [ ] 실습: **OutOfMemoryError 재현 및 대응**
  - Heap 덤프 생성 (`-XX:+HeapDumpOnOutOfMemoryError`)
  - Eclipse MAT / VisualVM으로 메모리 누수 원인 분석
  - 원인별 대응: 캐시 크기 조정 / 커넥션 미반납 / 대용량 컬렉션
- [ ] 실습: **CPU 100% 장애 재현 및 대응**
  - jstack으로 스레드 덤프 분석 (RUNNABLE 상태 스레드 확인)
  - CPU 핫스팟 메서드 async-profiler로 찾기
  - 원인별 대응: 무한 루프 / 비효율 알고리즘 / GC Overhead
- [ ] 실습: **스레드 데드락 재현 및 탐지**
  - jstack에서 `BLOCKED` + `deadlock` 패턴 찾기
  - 락 획득 순서 수정으로 해결
- [ ] 실습: **응답 시간 급증(Latency Spike) 재현 및 분석**
  - Grafana에서 P99 급등 확인 → Trace ID로 느린 요청 추적 → Slow Query 확인

#### DB 장애
- [ ] 실습: **커넥션 풀 고갈 장애 재현 및 대응**
  - `HikariPool-1 - Connection is not available` 에러 확인
  - 커넥션 누수 원인 찾기 (트랜잭션 미종료, 슬로우 쿼리로 인한 점유)
  - HikariCP 모니터링 메트릭으로 실시간 풀 상태 확인
- [ ] 실습: **Dead Lock 재현 및 대응**
  - MySQL `SHOW ENGINE INNODB STATUS`로 데드락 로그 확인
  - 락 획득 순서 / 트랜잭션 범위 조정으로 해결
- [ ] 실습: **Slow Query 탐지 및 최적화**
  - MySQL Slow Query Log 활성화
  - `EXPLAIN ANALYZE`로 실행 계획 분석
  - 인덱스 추가 / 쿼리 재작성으로 개선

#### 인프라 / 네트워크 장애
- [ ] 실습: **OOM Killed 컨테이너 대응** (k8s `OOMKilled` 상태 확인 및 리소스 limit 조정)
- [ ] 실습: **Pod CrashLoopBackOff 대응** (로그 확인 → Liveness Probe 임계값 조정)
- [ ] 실습: **디스크 풀(Disk Full) 장애 재현 및 대응** (로그 로테이션, 오래된 Docker 이미지 정리)
- [ ] 실습: **외부 API 장애 전파 차단** (Circuit Breaker 동작 확인 및 Fallback 응답 검증)

### 18-3. 무중단 운영 & 유지보수 실습

#### 무중단 배포
- [ ] 실습: Blue-Green 배포 구현 및 롤백 (NGINX upstream 전환)
- [ ] 실습: k8s Rolling Update 중 트래픽 유실 없는 배포 확인 (`gracefulShutdownTimeout` 설정)
- [ ] 실습: Canary 배포 구현 (10% → 50% → 100% 트래픽 점진적 전환)

#### DB 무중단 변경
- [ ] 실습: Flyway + Expand-Contract 패턴으로 컬럼 이름 변경 무중단 마이그레이션
- [ ] 실습: 인덱스 무중단 추가 (`CREATE INDEX CONCURRENTLY` / `ALGORITHM=INPLACE`)
- [ ] 실습: 대용량 테이블 무중단 데이터 마이그레이션 (배치 + Dual Write 패턴)

#### 운영 자동화
- [ ] 실습: Grafana Alert → Slack 연동으로 장애 자동 탐지
- [ ] 실습: 런북 기반 장애 대응 절차 문서화 (Heap OOM / DB 커넥션 풀 고갈 / 디스크 풀 각각)
- [ ] 실습: 정기적인 의존성 취약점 스캔 자동화 (OWASP Dependency-Check / Trivy)

### 18-4. 장애 예방 설계 실습
- [ ] 실습: Chaos Engineering 기초 — 의도적 장애 주입 후 복구 확인 (Chaos Monkey / k8s pod 강제 종료)
- [ ] 실습: 헬스체크 엔드포인트 설계 (DB / Redis / 외부 API 연결 상태 포함)
- [ ] 실습: Graceful Shutdown 구현 (진행 중인 요청 완료 후 종료)
- [ ] 실습: 서킷브레이커 임계값 튜닝 (실패율 기반 자동 차단 및 Half-Open 복구)

---

## PHASE 19 — 결제 시스템 연동

### 19-1. 결제 이론
- [ ] PG(Payment Gateway) 동작 원리 정리 (클라이언트 → PG사 → 카드사 → 정산 흐름)
- [ ] 결제 상태 관리 정리 (PENDING → SUCCESS / FAIL / CANCEL / REFUND)
- [ ] 결제 멱등성 정리 (네트워크 오류로 중복 결제 요청 방지)
- [ ] 취소 / 부분 취소 / 환불 처리 흐름 정리
- [ ] 가상계좌 / 카드 / 간편결제 방식 비교 정리

### 19-2. PG 연동 실습 (토스페이먼츠 기준)
- [ ] 실습: 토스페이먼츠 테스트 환경 세팅 및 결제 위젯 연동
- [ ] 실습: 결제 승인 API 구현 (클라이언트 결제 완료 → 서버 최종 승인)
- [ ] 실습: 결제 금액 위변조 방지 구현 (서버에서 주문 금액 검증)
- [ ] 실습: 결제 취소 / 환불 API 구현
- [ ] 실습: 결제 Webhook 수신 처리 구현 (PG사 → 서버 이벤트 수신)
- [ ] 실습: 결제 실패 시 주문 보상 트랜잭션 구현 (Saga 패턴 연계)

### 19-3. 동시 구매 처리 실습
> 시나리오: 재고 1개 남은 상품에 100명이 동시에 구매 시도

- [ ] 실습: 동시 구매 문제 재현 (재고 마이너스 되는 상황)
- [ ] 실습: 비관적 락으로 해결 및 성능 측정
- [ ] 실습: 분산 락(Redisson)으로 해결 및 성능 측정
- [ ] 실습: Kafka 기반 주문 큐잉으로 순차 처리 구현
- [ ] 실습: 방법별 TPS / 응답시간 비교 리포트 작성

---

## 추가 추천 주제 (심화 / 선택)

- [ ] **Flyway 심화**: 대규모 무중단 마이그레이션 전략 (Expand-Contract 패턴)
- [ ] **gRPC**: 서비스 간 고성능 통신 (REST 대비 성능 비교, Protobuf 직렬화)
- [ ] **Reactive Programming**: WebFlux + R2DBC (Blocking vs Non-Blocking 비교)
- [ ] **CQRS + Event Sourcing**: 읽기/쓰기 분리 아키텍처 패턴
- [ ] **Helm Chart 심화**: 커스텀 Chart 작성 및 Chart Repository 운영
- [ ] **Istio 심화**: mTLS / Traffic Mirroring / Fault Injection
- [ ] **ArgoCD 심화**: App of Apps 패턴, Multi-cluster 배포
- [ ] **Vault (HashiCorp)**: 시크릿 중앙 관리 (DB 비밀번호, API 키)
- [ ] **OpenTelemetry**: 벤더 중립적 표준 관찰가능성 계측
- [ ] **Apache Flink**: Kafka 스트리밍 처리 (Spark 대비 저지연 비교)
- [ ] **CDC (Change Data Capture)**: Debezium으로 DB 변경 이벤트 → Kafka 파이프라인
- [ ] **SQS + SNS**: AWS 기반 메시지 큐 / 팬아웃 패턴 (Kafka 대안)
- [ ] **Virtual Thread (Java 21)**: 기존 스레드 풀 방식 대비 성능 비교

---

## 진행 현황 요약

| Phase | 주제 | 상태 |
|-------|------|------|
| 0-A | 네트워크 & HTTP 기초 (OSI / TCP / DNS / HTTP 캐시) | 🔲 진행 전 |
| 0-B | 환경 설정 & 빌드 (application.yml / Profile / Gradle) | 🔲 진행 전 |
| 0-C | Git (내부 원리 / 브랜치 전략 / rebase / 충돌 / Hooks / 보안) | 🔲 진행 전 |
| 0-D | Linux & Shell (파일시스템 / 프로세스 / 네트워크 / 트러블슈팅 명령어) | 🔲 진행 전 |
| 0 | Java & JVM / 버전 선택 / 자료구조 & 컬렉션 / 동시성 / 모던 Java | 🔲 진행 전 |
| 1 | Spring 핵심 (POJO / DI / IoC / 어노테이션 / Filter / 쿠키 / 세션) | 🔲 진행 전 |
| 2 | RESTful API + 외부 API 연동 + Swagger 문서화 | 🔲 진행 전 |
| 2-B | 예외 처리 & 로깅 전략 | 🔲 진행 전 |
| 3 | TDD (JUnit5 / Mockito / TestContainers / ArchUnit) | 🔲 진행 전 |
| 4 | DB 설계 / 인덱스 심화 / Pagination / JPA / 동시성 / 트랜잭션 / Flyway / 커넥션 풀 | 🔲 진행 전 |
| 5 | AOP | 🔲 진행 전 |
| 6 | 커스텀 어노테이션 (원리 / 검증 / @CurrentUser MSA 패턴) | 🔲 진행 전 |
| 6-B | 멀티 모듈 프로젝트 구조 | 🔲 진행 전 |
| 7 | Spring Security / JWT / 보안 취약점 방어 / Rate Limiting / OAuth2 | 🔲 진행 전 |
| 7-B | 알림 서비스 (이메일 / SSE / WebSocket / FCM) | 🔲 진행 전 |
| 7-C | Redis 심화 (자료구조 / 캐시 전략 / 분산 락) | 🔲 진행 전 |
| 8 | 디자인 패턴 | 🔲 진행 전 |
| 9 | 클린 코드 / JVM 성능 최적화 / 캐싱 / 스케줄러 & Spring Batch | 🔲 진행 전 |
| 10 | Docker | 🔲 진행 전 |
| 10-B | 컨테이너 오케스트레이션 (Swarm / k3s / k8s / EKS) | 🔲 진행 전 |
| 11 | AI 연동 (Spring AI / RAG) | 🔲 진행 전 |
| 12 | NGINX & SSL & Cloudflare Tunnel | 🔲 진행 전 |
| 13 | MSA & Spring Cloud + Saga / 보상 트랜잭션 | 🔲 진행 전 |
| 14 | Kafka (EOS / DLQ) & Elasticsearch | 🔲 진행 전 |
| 15 | 모니터링 & Observability (SLI/SLO / 커스텀 메트릭) | 🔲 진행 전 |
| 15-B | CI/CD + 배포 전략 (Blue-Green / Canary) | 🔲 진행 전 |
| 16 | AWS 배포 / IAM / VPC / WAF / CloudFront / Secrets Manager | 🔲 진행 전 |
| 17 | 성능 테스트 & 단계별 비교 리포트 | 🔲 진행 전 |
| 18 | 장애 대응 / 트러블슈팅 / 유지보수 / 무중단 운영 | 🔲 진행 전 |
| 19 | 결제 시스템 (PG 연동 / 환불 / 동시 구매 처리) | 🔲 진행 전 |
