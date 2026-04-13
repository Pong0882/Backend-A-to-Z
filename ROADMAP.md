# Spring 로드맵

> Spring Boot 4.x / Java 17 기반
> 각 항목은 **이론 정리 → 구현 실습 → 검증/비교** 3단계로 구성

---

## 진행 현황

| 구분 | 완료 | 미완료 | 합계 |
|------|------|--------|------|
| 이론 정리 | 27 | 301 | 328 |
| 실습 | 12 | 297 | 309 |
| **전체** | **39** | **598** | **637** |

> 이 표는 항목 완료 시 수동으로 업데이트한다.

---

## 진행 방식

- `[ ]` 미완료 / `[x]` 완료
- 각 단계는 순차 진행을 권장하나, 독립적인 챕터는 병렬 진행 가능

---

## PHASE 1 — 네트워크 & 웹 기초

> 모든 백엔드 지식의 기반. HTTP/TCP를 이해 못 하면 Spring도 겉핥기가 됨

### 1-1. 네트워크 기초

- [ ] OSI 7계층 정리 및 각 계층 역할 + 실제 사용 프로토콜 매핑
- [ ] TCP vs UDP 차이 정리 (연결 지향 / 신뢰성 / 순서 보장)
- [ ] TCP 3-way handshake / 4-way handshake 정리
- [ ] TCP 흐름 제어 / 혼잡 제어 정리
- [ ] IP / 서브넷 마스크 / CIDR 표기법 정리 (AWS VPC 설계 기반 지식)
- [ ] **서브넷 계산 완전 정리** — 호스트 수 / 네트워크 주소 / 브로드캐스트 주소 계산
- [ ] **사설 IP vs 공인 IP / NAT 동작 원리 정리** (RFC 1918 사설 대역, SNAT/DNAT)
- [ ] **라우팅 프로토콜 정리** (정적 라우팅 vs 동적 라우팅 / RIP / OSPF / BGP 개념)
- [ ] **스위치 동작 원리 정리** (MAC 주소 테이블, VLAN, Trunk 포트)
- [ ] DNS 동작 원리 정리 (Recursive Query, 캐싱, TTL)
- [ ] 로드밸런서 L4 vs L7 차이 정리
- [ ] **ARP 동작 원리 정리** (IP → MAC 변환, ARP 테이블)
- [ ] **ICMP 정리** (`ping` / `traceroute` 동작 원리)

### 1-2. HTTP 프로토콜 심화

- [ ] HTTP/1.1 Keep-Alive / Pipelining 정리
- [ ] HTTP/2 멀티플렉싱 / 헤더 압축(HPACK) / 서버 푸시 정리
- [ ] HTTP/3 (QUIC 기반) 정리
- [ ] HTTP 캐시 정리 (`Cache-Control` / `ETag` / `Last-Modified` / `Expires`)
- [ ] 브라우저 → 서버까지 요청 전달 전체 흐름 정리 (DNS → TCP → TLS → HTTP → 응답)
- [ ] 실습: `Cache-Control` 헤더 직접 설정 후 브라우저 / Postman에서 캐시 동작 확인
- [ ] 실습: `ETag` 기반 조건부 요청 구현 (`If-None-Match` → 304 Not Modified 흐름 확인)

### 1-3. IO 모델

> WebFlux / Virtual Thread / Non-Blocking 이해의 기반. 모르면 왜 필요한지 체감 불가

- [ ] **Blocking I/O vs Non-Blocking I/O vs Async I/O 완전 정리**
  - Blocking: 스레드가 I/O 완료까지 대기 (전통적인 Spring MVC 방식)
  - Non-Blocking: I/O 완료 여부를 반복 확인하며 다른 작업 가능 (Selector 기반)
  - Async I/O: I/O 완료 시 콜백/이벤트로 통지 (Reactor 패턴, WebFlux)
- [ ] **Java NIO 동작 원리 정리** (Channel / Buffer / Selector)
- [ ] **Reactor 패턴 정리** (이벤트 루프 기반 비동기 처리, Node.js / Netty 동작 원리)
- [ ] **Thread-per-Request vs Event Loop 모델 비교** (Spring MVC vs WebFlux 선택 기준)
- [ ] **동기 vs 비동기 / 블로킹 vs 논블로킹 개념 분리 정리**
  - 동기/비동기: 결과를 기다리는 주체가 누구인가
  - 블로킹/논블로킹: 호출한 스레드가 멈추는가
- [ ] 실습: BIO vs NIO 소켓 서버 직접 구현 후 동시 연결 수 비교

---

## PHASE 2 — OS 기초

> JVM GC 튜닝, Thread Pool 설계, 컨테이너 리소스 설정의 기반 지식

### 2-1. 프로세스 vs 스레드

- [ ] **프로세스 vs 스레드 완전 정리**
  - 프로세스: 독립된 메모리 공간 (Code / Data / Heap / Stack), OS가 자원 단위로 관리
  - 스레드: 프로세스 내 실행 흐름, Heap/Data 공유, Stack만 독립
  - 멀티프로세스 vs 멀티스레드 장단점 비교 (안정성 vs 컨텍스트 스위칭 비용)
- [ ] **Context Switching 동작 원리 정리** (PCB/TCB 저장 및 복원, 비용이 발생하는 이유)
- [ ] **IPC(Inter-Process Communication) 방식 정리**
  - Pipe / Message Queue / Shared Memory / Socket / Signal
  - MSA 서비스 간 통신(HTTP / Kafka)이 IPC의 연장선임을 이해
- [ ] **멀티프로세스 환경 문제 정리** — 분산 락 / 세션 클러스터링 / 분산 트랜잭션이 필요한 이유

### 2-2. OS 메모리 관리

- [ ] **가상 메모리(Virtual Memory) 개념 정리** (물리 메모리 추상화, 프로세스별 독립 주소 공간)
- [ ] **페이징(Paging) 동작 원리 정리** (페이지 테이블, Page Fault, TLB)
- [ ] **스택 vs 힙 메모리 정리** (할당/해제 방식, 스택 오버플로우 원인)
- [ ] **JVM 메모리와 OS 메모리의 관계 정리** (`-Xmx`가 OS 가상 메모리와 어떻게 연결되는가)
- [ ] **메모리 누수 원인 정리** (OS 레벨 vs JVM 레벨 차이)

### 2-3. CPU 스케줄링

- [ ] CPU 스케줄링 알고리즘 정리 (Round Robin / Priority / CFS)
- [ ] **CPU bound vs I/O bound 작업 분류 정리** (Thread Pool 사이즈 결정 공식의 근거)
- [ ] **스핀락(Spinlock) vs 뮤텍스(Mutex) vs 세마포어(Semaphore) 정리**
  - Java `synchronized` / `ReentrantLock` / `Semaphore` 와 연결

---

## PHASE 3 — Linux & Shell

> 서버는 결국 Linux 위에서 돌아감. CLI 못 다루면 장애 대응 불가

### 3-1. Linux 기초

- [ ] Linux 파일 시스템 구조 정리 (`/etc` / `/var` / `/proc` / `/tmp` 각 역할)
- [ ] 파일 권한 정리 (`rwx`, `chmod`, `chown`, `umask`)
- [ ] 프로세스 관리 정리 (`ps` / `top` / `htop` / `kill` / `systemctl`)
- [ ] **`/proc` 파일시스템 정리** — `/proc/[pid]/` 로 프로세스 메모리 / fd / 스레드 확인하는 법
- [ ] 네트워크 명령어 정리 (`netstat` / `ss` / `curl` / `wget` / `nc` / `tcpdump`)
- [ ] 디스크 관련 명령어 정리 (`df` / `du` / `lsblk` / `iostat`)
- [ ] 로그 확인 명령어 정리 (`tail -f` / `grep` / `awk` / `sed` / `journalctl`)

### 3-2. Shell 스크립트 & 자동화

- [ ] Bash 기본 문법 정리 (변수 / 조건문 / 반복문 / 함수)
- [ ] 실습: 배포 자동화 shell script 작성 (빌드 → 서버 전송 → 재시작)
- [ ] 실습: 로그 파일 정기 정리 스크립트 작성 (`cron` + `find -mtime`)
- [ ] 실습: 서비스 헬스체크 + 자동 재시작 스크립트 작성

### 3-3. 서버 운영 실전

- [x] `systemd` 서비스 등록 및 관리 정리 (Spring Boot 앱을 서비스로 등록) → [정리](./notes/phase-19-docker/docker-basics.md)
- [x] SSH 키 기반 인증 정리 (비밀번호 없이 서버 접속, `authorized_keys`) → [정리](./notes/phase-3-linux/ssh-key-auth.md)
- [ ] **실무 트러블슈팅 명령어 시나리오**
  - 포트 점유 프로세스 찾기: `lsof -i :8080` / `ss -tlnp`
  - 특정 프로세스 메모리 사용량 확인: `cat /proc/[pid]/status`
  - 디스크 어느 디렉터리가 큰지 찾기: `du -sh /* | sort -rh | head`
  - 실시간 로그에서 ERROR만 필터링: `tail -f app.log | grep --line-buffered ERROR`
  - 네트워크 연결 상태 확인: `ss -s` / TIME_WAIT 과다 확인
- [ ] 실습: EC2 서버에 SSH 접속 → Spring Boot 앱 수동 배포 및 운영

---

## PHASE 4 — 환경 설정 & 빌드

> 실무에서 첫날부터 쓰는 것들인데 보통 가르쳐주지 않음

### 4-1. application.yml 설계 & 환경 분리

- [ ] `application.yml` vs `application.properties` 차이 정리
- [x] Spring Profile 동작 원리 정리 (`@Profile`, `spring.profiles.active`) → [정리](./notes/phase-7-spring-core/spring-profiles.md)
- [ ] 환경별 설정 분리 전략 정리 (`application-dev.yml` / `application-prod.yml` / `application-test.yml`)
- [ ] 설정값 우선순위 정리 (환경변수 > 커맨드라인 > yml 파일 > 기본값)
- [ ] 민감 정보 관리 전략 정리 (환경변수 / AWS Secrets Manager / Vault / Jasypt 암호화)
- [ ] 실습: 환경별 DB / Redis / 외부 API URL 분리 설정 구현
- [ ] 실습: Jasypt로 yml 내 비밀번호 암호화
- [ ] 실습: Docker Compose 환경변수 주입 + Spring Profile 연동

### 4-2. Gradle 빌드 시스템

- [ ] Gradle vs Maven 차이 정리
- [ ] `build.gradle` 구조 정리 (plugins / dependencies / tasks)
- [ ] 의존성 스코프 정리 (`implementation` / `compileOnly` / `runtimeOnly` / `testImplementation`)
- [ ] Gradle 빌드 캐시 및 incremental build 원리 정리
- [ ] 실습: 멀티 모듈 Gradle 설정 (`settings.gradle`, `subprojects` 공통 설정)
- [ ] 실습: Gradle Task 커스텀 작성 (빌드 후 Docker 이미지 자동 빌드)

---

## PHASE 5 — Git & 협업 워크플로우

> 혼자 쓸 때와 팀에서 쓸 때 완전히 다름. 내부 원리를 모르면 사고가 남

### 5-1. Git 내부 원리

- [ ] Git 오브젝트 모델 정리 (blob / tree / commit / tag — 모든 것이 SHA-1 해시)
- [ ] `.git` 디렉터리 구조 정리 (`HEAD` / `index` / `objects` / `refs`)
- [ ] **3가지 영역 완전 정리** (Working Directory → Staging Area → Repository)
- [ ] `git add` / `git commit` 내부에서 일어나는 일 정리
- [ ] Branch의 정체 정리 (특정 커밋을 가리키는 포인터에 불과함)
- [ ] `HEAD` 의 정체 정리 (현재 체크아웃된 커밋/브랜치를 가리키는 포인터)
- [ ] Fast-Forward merge vs 3-way merge 차이 정리
- [ ] `rebase` 동작 원리 정리 (커밋을 복사해서 재적용, merge와 결과는 같지만 히스토리가 다름)

### 5-2. Git 핵심 명령어 & 실전 시나리오

- [x] **되돌리기 명령어 차이 완전 정리** → [정리](./notes/phase-5-git/git-basics.md)
  - `git restore` — 워킹 디렉터리 변경사항 폐기
  - `git reset --soft/--mixed/--hard` — 커밋 취소
  - `git revert` — 되돌리는 커밋을 새로 만듦 (push된 커밋 취소할 때)
  - `reset` vs `revert` 언제 무엇을 써야 하는지 기준 정리
- [x] **`git stash`** — 작업 중간에 브랜치 전환해야 할 때 임시 저장 → [정리](./notes/phase-5-git/git-basics.md)
- [ ] **`git cherry-pick`** — 특정 커밋만 골라서 현재 브랜치에 적용
- [ ] **`git reflog`** — 실수로 reset/rebase 날렸을 때 복구하는 법
- [ ] **`git bisect`** — 버그가 처음 생긴 커밋을 이진 탐색으로 찾는 법
- [ ] **`git blame`** — 특정 라인을 누가 언제 수정했는지 추적
- [x] **`git log` 활용** — `--oneline` / `--graph` / `--author` / `--since` / 특정 파일 히스토리 → [정리](./notes/phase-5-git/git-basics.md)
- [ ] 실습: `reset --hard`로 날린 커밋을 `reflog`로 복구
- [ ] 실습: `bisect`로 버그 유발 커밋 찾기

### 5-3. 브랜치 전략 & 협업 워크플로우

- [ ] **Git Flow 정리** (main / develop / feature / release / hotfix 브랜치 역할)
- [ ] **GitHub Flow 정리** (main + feature 브랜치, PR 기반 단순 플로우)
- [ ] **Trunk-Based Development 정리** (단일 main, 짧은 수명의 feature 브랜치, Feature Flag 활용)
- [ ] 세 전략 비교 — 팀 규모 / 배포 주기에 따른 선택 기준 정리
- [ ] **커밋 메시지 컨벤션 정리** (Conventional Commits: `feat:` / `fix:` / `chore:` / `refactor:` / `docs:`)
- [ ] **PR(Pull Request) 작성 원칙 정리** (작은 단위 / 리뷰어 배려 / 변경 이유 명시)
- [ ] 실습: Git Flow로 feature 개발 → develop 머지 → release → main 전체 흐름 실습
- [ ] 실습: Conventional Commits 기반 커밋 메시지 규칙 적용

### 5-4. merge / rebase / 충돌 해결

- [ ] **`merge` vs `rebase` 언제 무엇을 써야 하는가** 정리
  - merge: 히스토리 보존, 협업 브랜치에서 안전
  - rebase: 히스토리 선형화, 로컬 작업 정리용 (push된 브랜치에 rebase는 위험)
- [ ] **`git rebase -i` (interactive rebase)** — 커밋 squash / 순서 변경 / 메시지 수정
- [ ] 충돌(conflict) 발생 원리 정리 (3-way merge에서 공통 조상 기준 비교)
- [ ] 실습: `rebase -i`로 WIP 커밋들을 의미있는 단위로 squash
- [ ] 실습: 의도적으로 충돌 만들고 해결하기
- [ ] 실습: `merge --no-ff`로 머지 커밋 명시적 생성

### 5-5. 원격 저장소 & 보안

- [x] `fetch` vs `pull` 차이 정리 (`pull` = `fetch` + `merge`) → [정리](./notes/phase-5-git/git-basics.md)
- [ ] `push --force` vs `push --force-with-lease` 차이 정리
- [x] **`.gitignore` 패턴 규칙 정리** + 이미 tracked된 파일 제거하는 법 (`git rm --cached`) → [정리](./notes/phase-5-git/git-basics.md)
- [ ] **민감 정보 커밋 사고 대응** — 이미 push된 비밀번호/키를 git history에서 제거하는 법
- [ ] Git Hooks 정리 (`pre-commit` / `commit-msg` / `pre-push`)
- [ ] 실습: `pre-commit` hook으로 커밋 전 코드 포맷 자동 적용
- [ ] 실습: `commit-msg` hook으로 Conventional Commits 형식 강제

---

## PHASE 6 — Java & JVM

> Spring 이전에 반드시 잡아야 할 Java/JVM 수준 지식

### 6-1. JVM 구조 & 메모리

- [ ] JVM 구조 정리 (ClassLoader / Runtime Data Area / Execution Engine)
- [ ] 메모리 영역 정리 (Heap / Stack / Method Area / PC Register / Native Stack)
- [ ] Heap 세부 구조 정리 (Young Generation: Eden/Survivor, Old Generation, Metaspace)
- [ ] GC 종류 및 동작 방식 정리 (Serial / Parallel / CMS / G1 / ZGC / Shenandoah)
- [ ] Stop-The-World 문제 및 GC 튜닝 포인트 정리
- [ ] 실습: `-Xms` / `-Xmx` / `-XX:+UseG1GC` JVM 옵션 설정 및 GC 로그 분석
- [ ] 실습: VisualVM / IntelliJ Profiler로 Heap 덤프 분석

### 6-2. Java 동시성 & 스레드

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

### 6-3. Java 핵심 문법 & 모던 Java

- [ ] Generic 타입 소거(Type Erasure) 및 와일드카드 정리
- [ ] Java 8+ Stream API 내부 동작 (지연 평가, 병렬 스트림 주의점) 정리
- [ ] Optional 올바른 사용법 및 안티패턴 정리
- [ ] Java 9~17 주요 변경사항 정리 (Records, Sealed Class, Text Blocks, Pattern Matching)
- [ ] Reflection 동작 원리 정리 (Spring이 내부적으로 사용하는 방식)
- [ ] 실습: Record + Sealed Class를 도메인 모델에 적용

### 6-4. Java 버전 선택 & LTS 전략

- [ ] **Java 버전 체계 정리** — LTS vs 비LTS 릴리즈 주기 차이
- [ ] **버전별 주요 변경사항 정리**
  - Java 8: Lambda / Stream / Optional / 기본 메서드 / `LocalDate`
  - Java 11 (LTS): `var` 확장 / HTTP Client API / `String` 신규 메서드
  - Java 17 (LTS): Sealed Class / Pattern Matching / Records GA / 난수 API
  - Java 21 (LTS): Virtual Thread GA / Sequenced Collections / Pattern Matching switch GA
- [ ] **프로젝트에서 버전 선택 기준 정리** (LTS 여부 / Spring Boot 지원 범위 / 라이브러리 호환성)
- [ ] Spring Boot 4.x가 요구하는 최소 Java 버전 및 권장 버전 정리
- [ ] 실습: Java 17 → Java 21 마이그레이션 체크리스트 작성

### 6-5. Java 자료구조 & 컬렉션 프레임워크

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
  - `hashCode()` + `equals()` 계약
  - 초기 capacity / load factor / resize(rehashing) 동작
  - Java 8+ 해시 충돌 시 LinkedList → Red-Black Tree 전환 (treeify threshold=8)
- [ ] **스택 / 큐 / 힙 자료구조를 Java로 구현하는 관용 패턴 정리**
- [ ] **불변 컬렉션 정리** (`List.of()` / `Map.of()` / `Collections.unmodifiableList()` 차이)
- [ ] 실습: 100만 건 데이터에서 `ArrayList` vs `LinkedList` 삽입/조회/삭제 성능 비교
- [ ] 실습: `HashMap` vs `TreeMap` 정렬 필요 여부에 따른 선택 실습
- [ ] 실습: `HashMap` 키로 커스텀 객체 쓸 때 `hashCode` / `equals` 미구현 버그 재현

---

## PHASE 7 — Spring 핵심 이론 & 구조

### 7-1. Spring 핵심 개념

- [ ] POJO(Plain Old Java Object) 개념 및 Spring이 POJO를 다루는 방식 정리
- [x] IoC(Inversion of Control) 컨테이너 동작 원리 정리 → [정리](./notes/phase-7-spring-core/ioc-di.md)
- [x] DI(Dependency Injection) — 생성자 주입 vs 필드 주입 vs 세터 주입 비교 정리 → [정리](./notes/phase-7-spring-core/ioc-di.md)
- [x] Spring Bean 생명주기 (생성 → 의존관계 주입 → 초기화 → 소멸) 정리 → [정리](./notes/phase-7-spring-core/spring-boot-application.md)
- [x] Bean 스코프 정리 (singleton / prototype / request / session) + 싱글톤 동시성 주의점 → [정리](./notes/phase-7-spring-core/ioc-di.md)
- [ ] ApplicationContext vs BeanFactory 차이 정리
- [x] Spring Boot 자동 구성(Auto Configuration) 동작 원리 정리 → [정리](./notes/phase-7-spring-core/spring-boot-application.md)
- [ ] Tomcat 임베디드 서버 구조 및 요청 처리 흐름 정리
- [ ] 실습: BeanDefinition, @Configuration, @Bean, @Component 직접 등록하며 차이 확인

### 7-2. Spring 핵심 어노테이션 총정리

- [x] **Bean 등록/설정**: `@Component` / `@Service` / `@Repository` / `@Controller` / `@Configuration` / `@Bean` 차이 → [정리](./notes/phase-7-spring-core/spring-layers.md)
- [x] **DI 관련**: `@Value` / `@ConfigurationProperties` → [정리](./notes/phase-7-spring-core/configuration-properties.md)
- [x] **요청 처리**: `@RequestMapping` / `@GetMapping` 등 / `@PathVariable` / `@RequestParam` / `@RequestBody` / `@ResponseBody` → [정리](./notes/phase-7-spring-core/request-mapping.md)
- [ ] **트랜잭션**: `@Transactional` 속성 전체 (propagation / isolation / readOnly / rollbackFor / timeout)
- [ ] **스케줄링**: `@Scheduled` / `@EnableScheduling` / cron 표현식
- [ ] **비동기**: `@Async` / `@EnableAsync` 동작 원리
- [ ] **이벤트**: `@EventListener` / `@TransactionalEventListener` 차이
- [ ] **캐시**: `@Cacheable` / `@CachePut` / `@CacheEvict` 동작
- [ ] **테스트**: `@SpringBootTest` / `@WebMvcTest` / `@DataJpaTest` / `@MockBean` / `@SpyBean`
- [ ] **조건부 Bean**: `@ConditionalOnProperty` / `@ConditionalOnMissingBean` / `@Profile`
- [x] **JPA**: `@Entity` / `@Table` / `@Id` / `@GeneratedValue` / `@Column` / `@Enumerated` → [정리](./notes/phase-11-database-jpa/jpa-entity-annotations.md) (`@OneToMany` / `@ManyToOne` / `@JoinColumn` 은 연관관계 매핑 시 추가)
- [ ] 실습: 헷갈리는 어노테이션 직접 사용하며 동작 확인

### 7-3. DTO 설계 & 레이어 간 변환

> 레이어마다 DTO를 분리하면 외부 계약과 내부 구현이 분리되어 변경에 강해진다

- [ ] **DTO vs Entity 분리 원칙 정리** — Entity를 Controller까지 노출하면 안 되는 이유
- [ ] **레이어별 DTO 설계 전략 정리**
  - `RequestDto` / `ResponseDto` — Controller 레이어, 외부 입출력 계약
  - `ServiceDto` (Command / Query) — Service 레이어 간 데이터 전달
  - Entity — Repository ↔ DB 매핑 전용
- [ ] **CQRS 관점의 DTO 분리 정리** — Command(쓰기)와 Query(읽기) DTO를 분리하는 이유
- [ ] **MapStruct 정리** — 컴파일 타임 코드 생성 기반 매핑, Reflection 기반 ModelMapper 대비 성능 비교
- [ ] 실습: 레이어별 DTO 분리 구조 적용 (RequestDto → ServiceDto → Entity 변환 체인)
- [ ] 실습: MapStruct로 DTO ↔ Entity 자동 매핑 구현
- [ ] 실습: `@JsonIgnore` / `@JsonProperty` / `@JsonNaming` Jackson 직렬화 제어

### 7-4. MVC 패턴 & DispatcherServlet

- [ ] DispatcherServlet 동작 흐름 정리 (HandlerMapping → HandlerAdapter → ViewResolver)
- [x] `@Controller` vs `@RestController` 차이 정리 → [정리](./notes/phase-7-spring-core/rest-controller.md)
- [ ] 요청/응답 처리 전체 흐름 정리 (Filter → Interceptor → AOP → Controller → ResponseBody)
- [ ] 실습: 순수 Servlet → Spring MVC 마이그레이션 직접 구현
- [ ] 실습: HandlerInterceptor 커스텀 구현
- [ ] 실습: HandlerMethodArgumentResolver 커스텀 구현

### 7-5. Filter / Interceptor / 쿠키 / 세션

- [ ] **Filter vs Interceptor 완전 정리** — 실행 시점 / 접근 객체 / 예외 처리 / 사용 기준 비교
- [ ] Filter 종류 정리 (`OncePerRequestFilter` vs `GenericFilterBean`)
- [ ] Spring Security Filter Chain과 일반 Filter가 분리되는 구조 정리
- [ ] 쿠키(Cookie) 동작 원리 정리 (`Set-Cookie`, `HttpOnly` / `Secure` / `SameSite` / `Path` / `Domain` / `Expires`)
- [ ] 세션(Session) 동작 원리 정리 (`JSESSIONID`, 서버 메모리 기반 세션 한계)
- [ ] 쿠키 vs 세션 vs JWT 비교 (저장 위치 / 보안 / 확장성)
- [ ] 세션 클러스터링 문제 정리 (Sticky Session vs 세션 복제 vs Redis 중앙화)
- [ ] 실습: `OncePerRequestFilter` 커스텀 필터 구현 (요청 로깅, 인증 토큰 추출)
- [ ] 실습: `FilterRegistrationBean`으로 필터 순서 / URL 패턴 지정
- [ ] 실습: `HandlerInterceptor`로 API 실행 시간 측정 구현
- [ ] 실습: 쿠키 기반 장바구니 구현 (비로그인 상태 유지)
- [ ] 실습: 서버 세션 기반 로그인 구현 → 수평 확장 시 세션 공유 문제 재현
- [ ] 실습: Spring Session + Redis로 세션 클러스터링 해결

---

## PHASE 8 — RESTful API 설계 & 구현 & 문서화

### 8-1. REST 이론

- [ ] REST 6가지 제약 조건 정리 (Stateless, Uniform Interface, Layered System 등)
- [ ] Richardson Maturity Model (Level 0~3) 정리
- [ ] HTTP 메서드별 멱등성 / 안전성 정리
- [ ] HTTP 상태코드 체계적 정리 (2xx / 3xx / 4xx / 5xx 시나리오별)
- [ ] API Versioning 전략 비교 정리 (URL / Header / Media Type)

### 8-2. RESTful API 구현

- [ ] 실습: RESTful 리소스 설계 원칙에 따른 API 구현 (CRUD)
- [x] 실습: 표준 응답 포맷 설계 (`ApiResponse` wrapper, 에러 포맷 통일) → [정리](./notes/phase-8-rest-api/api-result.md)
- [ ] 실습: `@ControllerAdvice` + `@ExceptionHandler` 글로벌 예외 처리 구현
- [ ] 실습: `@Valid` + `BindingResult` 입력 검증 구현
- [ ] 실습: HATEOAS 적용 (Level 3 REST)
- [ ] 실습: API Versioning 구현 및 비교

### 8-3. 외부 API 연동

- [x] `RestTemplate` vs `RestClient` vs `WebClient` 비교 정리 → [정리](./notes/phase-8-rest-api/rest-client.md)
- [ ] `RestTemplate` vs `WebClient` vs `OpenFeign` 비교 정리 (동기/비동기, 사용 시점)
- [ ] 실습: `RestTemplate`으로 외부 API 호출 구현
- [ ] 실습: `WebClient`로 논블로킹 외부 API 호출 구현
- [ ] 실습: `OpenFeign`으로 선언적 HTTP 클라이언트 구현
- [ ] 실습: 외부 API 실패 시 Retry / Fallback / Timeout 처리 구현

### 8-4. API 문서화

- [x] Swagger 2 vs SpringDoc OpenAPI 3 비교 정리 → [정리](./notes/phase-8-rest-api/swagger-springdoc.md)
- [x] 실습: SpringDoc OpenAPI 3 적용 및 커스터마이징 (@Operation, @ApiResponse, @Schema)
- [x] 실습: API 문서에 JWT Bearer 인증 헤더 적용
- [ ] 실습: API Versioning별 문서 분리

### 8-5. GraphQL

> REST의 Over-fetching / Under-fetching 문제를 해결하는 쿼리 언어. 단일 엔드포인트(POST /graphql)로 클라이언트가 원하는 데이터만 요청

- [ ] **REST vs GraphQL 비교 정리**
  - REST: 엔드포인트별 고정 응답 구조
  - GraphQL: 클라이언트가 필요한 필드만 지정해서 요청
  - Over-fetching (필요 이상 데이터) / Under-fetching (여러 번 요청 필요) 문제
- [ ] **GraphQL 핵심 개념 정리** (Schema / Query / Mutation / Subscription / Resolver)
- [ ] **GraphQL 단점 정리** (캐싱 어려움, N+1 문제, 복잡한 권한 처리)
- [ ] **언제 REST, 언제 GraphQL을 써야 하는가** 선택 기준 정리
- [ ] 실습: Spring for GraphQL 세팅 및 기본 Query / Mutation 구현
- [ ] 실습: DataLoader로 N+1 문제 해결
- [ ] 실습: GraphQL Subscription으로 실시간 데이터 구독 구현

---

## PHASE 9 — 예외 처리 & 로깅 전략

### 9-1. 예외 처리 이론

- [x] Java 예외 계층 구조 정리 (Checked vs Unchecked, Error vs Exception) → [정리](./notes/phase-9-exception/global-exception-handler.md)
- [ ] Spring 예외 처리 흐름 정리 (`HandlerExceptionResolver` 체인)
- [x] 비즈니스 예외 vs 시스템 예외 분리 설계 원칙 정리 → [정리](./notes/phase-9-exception/global-exception-handler.md)
- [x] HTTP 상태코드와 예외 매핑 전략 정리 → [정리](./notes/phase-9-exception/global-exception-handler.md)

### 9-2. 예외 처리 구현

- [x] 실습: 커스텀 비즈니스 예외 계층 설계 (`BaseException` → 도메인별 예외) → [정리](./notes/phase-9-exception/global-exception-handler.md)
- [x] 실습: `ErrorCode` Enum 설계 (code / message / httpStatus 통합 관리) → [정리](./notes/phase-9-exception/global-exception-handler.md)
- [x] 실습: `@ControllerAdvice` 글로벌 예외 핸들러 구현 → [정리](./notes/phase-9-exception/global-exception-handler.md)
- [ ] 실습: `MethodArgumentNotValidException` 처리 표준화
- [ ] 실습: Spring Security 인증/인가 예외 커스터마이징
- [ ] 실습: 외부 API 호출 예외 처리 패턴 (Retry, Circuit Breaker Fallback)

### 9-3. 로깅 전략

- [x] SLF4J / Logback / Log4j2 비교 정리 → [정리](./notes/phase-9-logging/logging-strategy.md)
- [x] 로그 레벨 전략 정리 (TRACE / DEBUG / INFO / WARN / ERROR 사용 기준) → [정리](./notes/phase-9-logging/logging-strategy.md)
- [ ] MDC(Mapped Diagnostic Context) 요청 추적 ID 전파 정리
- [ ] 실습: AOP 기반 요청 / 응답 / 예외 자동 로깅 구현
- [ ] 실습: MDC로 요청별 `traceId` 부여 및 로그 자동 포함
- [ ] 실습: 환경별 로그 레벨 분리 (dev=DEBUG, prod=WARN)
- [ ] 실습: Logback JSON 포맷 설정 (ELK 연동 대비)

---

## PHASE 10 — TDD

### 10-1. 테스트 이론

- [ ] TDD 사이클 정리 (Red → Green → Refactor)
- [ ] 단위 테스트 vs 통합 테스트 vs E2E 테스트 차이 정리
- [ ] 테스트 더블 종류 정리 (Mock / Stub / Spy / Fake / Dummy)
- [ ] `@SpringBootTest` vs `@WebMvcTest` vs `@DataJpaTest` 차이 정리
- [ ] 테스트 픽스처 전략 정리 (Object Mother, Test Data Builder)

### 10-2. TDD 실습

- [ ] 실습: JUnit5 + AssertJ 기본 세팅 및 활용
- [ ] 실습: Mockito를 활용한 단위 테스트 작성
- [ ] 실습: `@WebMvcTest`로 Controller 슬라이스 테스트
- [ ] 실습: `@DataJpaTest`로 Repository 슬라이스 테스트
- [ ] 실습: TestContainers로 실제 DB 통합 테스트 구현
- [ ] 실습: RestAssured로 E2E API 테스트 구현
- [ ] 실습: TDD로 서비스 레이어 처음부터 구현 (테스트 먼저 작성)
- [ ] 실습: 아키텍처 테스트 (ArchUnit으로 레이어 의존성 규칙 검증)

---

## PHASE 11 — 데이터베이스 & JPA

### 11-0. DB 설계 원칙 & ERD

> 코드보다 설계가 먼저다. 잘못된 스키마는 나중에 고치기 매우 어렵다

- [ ] **ERD 표기법 정리** (IE 표기법 — 실선/점선, 필수/선택, 1:1 / 1:N / M:N 관계)
- [ ] **도메인 모델 설계 원칙 정리**
  - 식별자(PK) 전략 (Auto Increment vs UUID vs ULID — 각각의 장단점)
  - Soft Delete vs Hard Delete 선택 기준 (`deleted_at` 컬럼 방식)
  - 생성일/수정일 Auditing 필드 표준화 (`created_at` / `updated_at`)
  - Null 허용 기준 — 언제 nullable로 설계하는가
- [ ] **M:N 관계 처리 전략 정리** (중간 테이블 직접 설계 vs JPA `@ManyToMany`)
- [ ] **계층형 데이터 설계 패턴 정리** (댓글/카테고리 — Adjacency List / Closure Table / Nested Set)
- [ ] **이력 테이블 설계 패턴 정리** — 변경 이력을 어떻게 저장하는가
- [ ] 실습: pong-to-rich 전체 도메인 ERD 설계 (User / Stock / Order / Strategy / Trade)

### 11-1. RDBMS 핵심 원리

- [ ] RDBMS 개념 정리 (관계형 모델, 테이블 / 행 / 열 / 기본키 / 외래키)
- [ ] InnoDB 스토리지 엔진 구조 정리 (Buffer Pool / Redo Log / Undo Log / Change Buffer)
- [ ] **MVCC 동작 원리 정리** (Undo Log 기반 버전 관리, 읽기 일관성)
- [ ] **InnoDB Lock 종류 완전 정리**
  - Row Lock: Shared Lock(S) vs Exclusive Lock(X)
  - Gap Lock / Next-Key Lock / Record Lock (팬텀 리드 방지 메커니즘)
  - Table Lock / Intention Lock / Auto-Increment Lock
- [ ] **실행 계획(EXPLAIN) 읽는 법 완전 정리**
  - `type` 컬럼: ALL / index / range / ref / eq_ref / const / system 의미
  - `key` / `rows` / `filtered` / `Extra` 의미
- [ ] Join 알고리즘 정리 (Nested Loop / Hash Join / Sort Merge Join)
- [ ] 실습: 동일 쿼리를 EXPLAIN으로 분석하며 실행 계획 개선 전후 비교

### 11-2. 데이터베이스 설계

- [ ] 정규화 (1NF ~ 3NF, BCNF) 이론 정리
- [ ] 역정규화 적용 기준 및 전략 정리
- [ ] 파티셔닝(Partitioning) vs 샤딩(Sharding) 개념 정리 (수직 vs 수평 분할)
- [ ] Read Replica 개념 및 Spring에서 라우팅 정리
- [ ] 실습: 동일 데이터셋으로 정규화 / 역정규화 스키마 각각 설계
- [ ] 실습: MySQL Replication 구성 및 `@Transactional(readOnly=true)` 라우팅 구현

### 11-3. 인덱스 심화

- [ ] **B-Tree 인덱스 내부 구조 정리** (페이지 / 리프 노드 / 루트 노드, 탐색 방식)
- [ ] **InnoDB 클러스터드 인덱스 정리** (PK가 곧 데이터 정렬 순서)
- [ ] **인덱스 설계 기준 정리**
  - 카디널리티(Cardinality): 왜 선택도가 높은 컬럼에 인덱스를 걸어야 하는가
  - 복합 인덱스 컬럼 순서 결정 기준
  - 인덱스가 오히려 느려지는 경우
- [ ] **Covering Index 정리** (인덱스만으로 쿼리 완성)
- [ ] **복합 인덱스 정리** (Leftmost Prefix Rule)
- [ ] **인덱스 종류 비교** (B-Tree / Hash / Full-Text / Spatial)
- [ ] 실습: 인덱스 없음 vs 단일 인덱스 vs 복합 인덱스 실행시간 비교
- [ ] 실습: Covering Index 적용 전후 비교
- [ ] 실습: 낮은 카디널리티 컬럼 인덱스 성능이 오히려 나쁜 케이스 재현

### 11-4. Pagination & 대용량 조회

- [ ] **offset 페이징 vs cursor(keyset) 페이징 비교 정리**
- [ ] `Page<T>` vs `Slice<T>` vs `List` 차이 정리 (count 쿼리 발생 여부)
- [ ] 실습: offset 페이징 성능 저하 재현 (100만 건, OFFSET 증가에 따른 속도 저하)
- [ ] 실습: cursor 기반 무한 스크롤 API 구현 및 offset 대비 성능 비교
- [ ] 실습: count 쿼리 분리 최적화

### 11-5. JPA 개념 & 영속성 컨텍스트

- [ ] **ORM 개념 정리** (SQL 직접 작성 vs ORM 방식 비교)
- [ ] **JPA vs Hibernate 관계 정리** (JPA는 표준 인터페이스, Hibernate는 구현체)
- [ ] **Entity 상태 4가지 정리** (비영속 / 영속 / 준영속 / 삭제)
- [ ] **영속성 컨텍스트 동작 원리 정리**
  - 1차 캐시 / 변경 감지 (Dirty Checking) / 쓰기 지연 / 지연 로딩
- [ ] **플러시(Flush) 시점 정리**
- [ ] 즉시 로딩(EAGER) vs 지연 로딩(LAZY) 및 N+1 문제 정리
- [ ] 실습: N+1 문제 재현 → fetch join / `@EntityGraph`로 해결
- [ ] **연관관계 매핑 정리** (`@OneToMany` / `@ManyToOne` / `@ManyToMany` / `@OneToOne`)
- [ ] **연관관계 주인(Owner) 정리**
- [ ] JPA 상속 매핑 전략 정리 (SINGLE_TABLE / JOINED / TABLE_PER_CLASS)
- [ ] 실습: 상속 매핑 전략별 쿼리 / 성능 비교
- [ ] Auditing 정리 (`@CreatedDate` / `@LastModifiedDate`)
- [ ] 실습: `BaseEntity`에 Auditing 적용
- [ ] **Spring Data JPA 정리** (`JpaRepository` 메서드 규칙, `@Query`, `Pageable`)
- [ ] JPQL vs QueryDSL vs Native Query 비교 정리
- [ ] 실습: QueryDSL 세팅 및 동적 쿼리 구현
- [ ] Batch Insert 전략 정리 (`saveAll` 성능 문제, JDBC Batch 설정)
- [ ] 실습: 대량 데이터 Insert 방식별 성능 비교 (JPA saveAll vs JDBC Batch)

### 11-6. 동시성 제어

- [ ] 동시성 문제 유형 정리 (Race Condition, Lost Update, Phantom Read)
- [ ] 낙관적 락(Optimistic Lock) vs 비관적 락(Pessimistic Lock) 이론 정리
- [ ] 실습: 재고 감소 동시 100명 요청 문제 재현
- [ ] 실습: `@Version` 기반 낙관적 락 구현 및 충돌 재시도 처리
- [ ] 실습: `@Lock(PESSIMISTIC_WRITE)` 비관적 락 구현
- [ ] 실습: Redisson 분산 락으로 멀티 인스턴스 환경 동시성 제어
- [ ] 실습: 방법별 성능 비교 (낙관적 vs 비관적 vs 분산 락)

### 11-7. 트랜잭션 관리

- [ ] 트랜잭션 ACID 속성 정리
- [ ] Spring `@Transactional` 동작 원리 정리 (프록시 기반)
- [ ] 트랜잭션 격리 수준 4단계 정리 (READ UNCOMMITTED ~ SERIALIZABLE)
- [ ] 실습: 격리 수준별 Dirty Read / Non-Repeatable Read / Phantom Read 재현
- [ ] 트랜잭션 전파(Propagation) 7가지 옵션 이론 정리
- [ ] 실습: REQUIRED / REQUIRES_NEW / NESTED 전파 옵션 동작 직접 확인
- [ ] 실습: 자기 호출(Self-Invocation) 문제 재현 및 해결

### 11-8. CQRS 패턴

> 읽기(Query)와 쓰기(Command)를 분리해서 각각 최적화하는 패턴. 단순 코드 분리부터 DB 분리까지 단계가 있다

- [ ] **CQRS 개념 정리**
  - Command — 상태를 변경하는 작업 (Create / Update / Delete), 반환값 없거나 최소화
  - Query — 상태를 조회하는 작업 (Read), 사이드 이펙트 없음
  - 왜 분리하는가 — 읽기/쓰기 트래픽 비율이 다름, 각각 다른 최적화 전략 필요
- [ ] **CQRS 적용 단계 정리**
  - Level 1: 같은 DB, 같은 모델 — Command/Query 메서드만 분리
  - Level 2: 같은 DB, 모델 분리 — Command용 Entity, Query용 DTO 따로
  - Level 3: DB 분리 — Write DB(MySQL) + Read DB(Read Replica / Elasticsearch)
- [ ] **Event Sourcing과의 관계 정리** — CQRS와 Event Sourcing은 별개 패턴, 함께 쓰는 경우가 많을 뿐
- [ ] **CQRS 단점 정리** — 복잡도 증가, 최종 일관성(Eventual Consistency) 허용 필요
- [ ] 실습: Level 1 CQRS — Service 메서드를 Command / Query로 분리
- [ ] 실습: Level 2 CQRS — 쓰기용 Entity + 읽기용 QueryDto 분리 구현
- [ ] 실습: Level 3 CQRS — Read Replica 라우팅 (`@Transactional(readOnly=true)`)
- [ ] 실습: CQRS 적용 전후 코드 복잡도 및 성능 비교

### 11-9. DB 마이그레이션 (Flyway)

- [ ] Flyway vs Liquibase 비교 정리
- [ ] Flyway 동작 원리 정리 (버전 관리, checksum, `flyway_schema_history`)
- [ ] 실습: Flyway 세팅 및 마이그레이션 스크립트 작성 (V1, V2...)
- [ ] 실습: 운영 중 컬럼 추가 / 이름 변경 안전하게 마이그레이션하는 전략 구현

### 11-9. 커넥션 풀 (HikariCP)

- [ ] 커넥션 풀 개념 및 필요성 정리
- [ ] HikariCP 내부 동작 원리 정리 (풀 사이즈 결정 공식 포함)
- [ ] HikariCP 주요 설정 항목 정리 (`maximumPoolSize` / `connectionTimeout` / `idleTimeout` / `maxLifetime`)
- [ ] 실습: HikariCP 세팅 및 커넥션 풀 모니터링 구현
- [ ] 실습: 풀 사이즈별 (5 / 10 / 20 / 50) 동시 요청 성능 비교 (k6)
- [ ] 실습: 커넥션 풀 고갈 상황 재현 및 타임아웃 처리 확인

### 11-10. 파일 업로드 & 스토리지

- [ ] Multipart 파일 업로드 처리 방식 정리
- [ ] 실습: Spring Multipart 파일 업로드 구현 (로컬 저장)
- [ ] 실습: AWS S3 파일 업로드 / 다운로드 구현
- [ ] 실습: S3 Presigned URL 발급으로 클라이언트 직접 업로드 구현
- [ ] 실습: 이미지 리사이징 처리 (Thumbnailator)

---

## PHASE 12 — AOP

### 12-1. AOP 이론

- [ ] AOP 핵심 개념 정리 (Aspect / Advice / Pointcut / JoinPoint / Weaving)
- [ ] Spring AOP vs AspectJ 차이 정리 (프록시 기반 vs 바이트코드 조작)
- [ ] JDK Dynamic Proxy vs CGLIB 프록시 차이 정리

### 12-2. AOP 구현

- [ ] 실습: `@Around` / `@Before` / `@After` 어드바이스 구현
- [ ] 실습: 메서드 실행 시간 측정 AOP 구현
- [ ] 실습: 커스텀 어노테이션 기반 로깅 AOP 구현 (`@Loggable`)
- [ ] 실습: 커스텀 어노테이션 기반 권한 체크 AOP 구현 (`@RequireRole`)
- [ ] 실습: 트랜잭션 AOP 흐름 직접 추적 (디버깅으로 프록시 확인)

---

## PHASE 13 — 커스텀 어노테이션 & 멀티모듈

### 13-1. 어노테이션 동작 원리

- [ ] Java 어노테이션 메커니즘 정리 (`@Retention` / `@Target` / `@Documented` / `@Inherited`)
- [ ] 런타임 리플렉션으로 어노테이션 읽는 방식 정리
- [ ] Spring이 어노테이션을 처리하는 방식 정리 (BeanPostProcessor, AOP 프록시)
- [ ] 메타 어노테이션 / 합성 어노테이션(Composed Annotation) 개념 정리

### 13-2. 커스텀 어노테이션 구현

- [ ] 실습: 커스텀 유효성 검증 어노테이션 구현 (`@PhoneNumber`, `@BusinessNumber`, `@Enum`)
- [ ] 실습: AOP 연동 커스텀 어노테이션 구현 (`@Loggable`, `@RequireRole`, `@RateLimit`)
- [ ] 실습: `@ConfigurationProperties` 커스텀 설정 바인딩 구현

### 13-3. MSA 환경 커스텀 어노테이션 — @CurrentUser

- [ ] MSA 사용자 식별 흐름 정리 (Gateway JWT 검증 → `X-User-Id` Header 전파)
- [ ] `HandlerMethodArgumentResolver` 동작 원리 정리
- [ ] 실습: Gateway에서 JWT 검증 후 `X-User-Id` / `X-User-Role` 헤더 주입 구현
- [ ] 실습: 각 서비스에서 `@CurrentUser` + `ArgumentResolver` 구현
- [ ] 실습: `@CurrentUser`를 공통 모듈(common)에 두고 재사용하는 구조 구현
- [ ] 실습: 모놀리식(SecurityContext) vs MSA(Header) 환경 전략 분기 구현

### 13-4. 멀티 모듈 프로젝트 구조

- [ ] 멀티 모듈 구조 필요성 및 설계 원칙 정리 (계층 간 의존성 방향)
- [ ] 실습: Gradle 멀티 모듈 프로젝트 세팅 (`api` / `domain` / `infra` / `common` 분리)
- [ ] 실습: 모듈 간 의존성 설정 및 순환 참조 방지
- [ ] 실습: 공통 모듈(common)에 예외 / 응답 포맷 / 유틸 / `@CurrentUser` 분리

---

## PHASE 14 — Spring Security & 인증/인가

### 14-1. Security 동작 원리

- [x] Security Filter Chain 전체 흐름 정리 (주요 필터 역할 각각) → [정리](./notes/phase-14-security-jwt/spring-security.md)
- [x] Authentication / Authorization 차이 및 처리 흐름 정리 → [정리](./notes/phase-14-security-jwt/spring-security.md)
- [x] `SecurityContext` / `SecurityContextHolder` 동작 정리 → [정리](./notes/phase-14-security-jwt/spring-security.md)
- [x] `PasswordEncoder` 종류 및 BCrypt 동작 정리 → [정리](./notes/phase-14-security-jwt/password-encoder.md)

### 14-2. JWT 기반 인증 구현

- [x] JWT 구조 정리 (Header.Payload.Signature) 및 장단점 → [정리](./notes/phase-14-security-jwt/jwt.md)
- [x] Access Token / Refresh Token 전략 정리 → [정리](./notes/phase-14-security-jwt/jwt.md)
- [x] 실습: JWT 발급 / 검증 / 재발급 필터 구현 → JwtProvider, JwtAuthenticationFilter
- [x] 실습: DB 기반 Refresh Token 저장 / 재발급 / 로그아웃 구현
- [ ] 실습: Redis 기반 Refresh Token 저장 및 블랙리스트 구현 (DB → Redis 전환 + 성능 비교)
- [ ] 실습: Redis 기반 JWT 로그아웃 처리 구현

### 14-3. 보안 취약점 방어

- [ ] OWASP Top 10 각 항목 정리 및 Spring에서 방어 방법 정리
- [x] CORS 동작 원리 정리 (Preflight, Simple Request) → [정리](./notes/phase-14-security-jwt/spring-security.md)
- [x] CSRF 공격 원리 및 방어 정리 → [정리](./notes/phase-14-security-jwt/spring-security.md)
- [ ] XSS 공격 원리 및 방어 정리
- [ ] SQL Injection 방어 정리 (PreparedStatement, JPA 파라미터 바인딩)
- [ ] 보안 HTTP 헤더 정리 (`X-Frame-Options` / `Strict-Transport-Security` / `Content-Security-Policy`)
- [ ] **입력 검증 전략 정리** (`@Valid` / `@NotBlank` / `@Pattern` / `ConstraintViolation`)
- [ ] **전역 예외 처리 정리** (`@RestControllerAdvice` / `ErrorCode` Enum / 에러 응답 포맷 통일)
- [ ] 실습: `@Valid` + `@RestControllerAdvice` 입력 검증 및 예외 처리 구현
- [ ] 실습: Spring Security 설정으로 보안 헤더 일괄 적용
- [ ] 실습: CORS 설정 (허용 Origin / Method / Header 세분화) — Swagger Mixed Content 해결 포함
- [ ] 실습: SQL Injection / XSS 공격 재현 및 방어 구현

### 14-4. Rate Limiting & API Throttling

> 서버 보호뿐 아니라 외부 API 쿼터 보호에도 필수. 한투 Open API / AI API는 분당 요청 수 제한이 있어 초과 시 차단됨.

- [ ] Rate Limiting 알고리즘 정리 (Token Bucket / Leaky Bucket / Fixed Window / Sliding Window)
- [ ] **외부 API 쿼터 관리 전략 정리** — 한투 Open API / AI API 요청 제한 초과 방지
  - 클라이언트 → 서버 요청 수 제한 (사용자별 / IP별)
  - 서버 → 외부 API 요청 수 제한 (전역 쿼터 카운터 Redis 관리)
  - 쿼터 초과 시 응답 전략 (큐잉 / 캐싱 / 429 응답)
- [ ] 실습: Bucket4j + Redis 기반 API Rate Limiting 구현
- [ ] 실습: `@RateLimit` 커스텀 어노테이션으로 엔드포인트별 제한
- [ ] 실습: 한투 API 요청 쿼터 카운터 구현 (Redis incr + TTL 방식)
- [ ] 실습: Spring Cloud Gateway Rate Filter 구현 (MSA 환경)

### 14-5. 소셜 로그인 (OAuth2)

- [ ] OAuth2 Authorization Code Flow 이론 정리
- [ ] 실습: Google OAuth2 소셜 로그인 구현
- [ ] 실습: Kakao / Naver OAuth2 커스텀 Provider 구현
- [ ] 실습: 소셜 로그인 + JWT 연동

---

## PHASE 15 — 알림 & 인증 서비스

### 15-1. 이론

- [ ] 알림 전송 방식 비교 정리 (이메일 / 푸시 / 실시간 / SMS)
- [ ] Redis Pub/Sub vs Kafka 이벤트 방식 비교 정리
- [ ] WebSocket vs SSE(Server-Sent Events) 비교 정리
- [ ] **이메일 인증 vs SMS 본인인증 비교 정리**
  - 이메일 인증: 회원가입 인증 / 비밀번호 재설정용. 구현 단순, 비용 없음
  - SMS OTP: 실명 본인확인 / 2FA용. 통신사 연동 필요, 건당 비용 발생
  - 실서비스에서 언제 어떤 걸 써야 하는지 판단 기준

### 15-2. 이메일 인증

- [ ] **이메일 인증 플로우 정리**
  - 회원가입 → 인증 메일 발송 → 링크 클릭 → 인증 완료 플로우
  - 인증 토큰 설계 (UUID / JWT / Redis TTL)
  - 미인증 계정 처리 정책 (N일 후 자동 삭제)
- [ ] 실습: 회원가입 이메일 인증 구현 (토큰 발급 → 메일 발송 → 검증)
- [ ] 실습: JavaMailSender + SMTP 이메일 발송 구현
- [ ] 실습: Thymeleaf HTML 이메일 템플릿 구현 (인증 링크 / 알림 메일)
- [ ] 실습: 비동기 이메일 발송 처리 (`@Async`)

### 15-3. SMS 본인인증

- [ ] **SMS OTP 인증 플로우 정리**
  - 전화번호 입력 → OTP 발송 → 입력 → 검증 플로우
  - OTP 만료 시간 / 재발송 제한 / 일일 발송 횟수 제한 설계
  - 국내 SMS 발송 서비스 비교 (NCP / 알리고 / 솔라피)
- [ ] **실명 본인확인(PASS) 정리**
  - 금융 서비스에서 실명 확인이 필요한 이유
  - KCB / NICE / PASS 인증 연동 방식 개요
- [ ] 실습: NCP SMS API 연동으로 OTP 발송 구현
- [ ] 실습: Redis 기반 OTP 저장 (TTL 3분) + 검증 + 재발송 제한 구현

### 15-4. 실시간 알림 (WebSocket / SSE)

- [ ] 실습: SSE로 서버 → 클라이언트 단방향 실시간 알림 구현
- [ ] 실습: WebSocket + STOMP로 양방향 채팅 구현
- [ ] 실습: Redis Pub/Sub으로 멀티 인스턴스 환경 실시간 알림 브로드캐스트

### 15-5. FCM 푸시 알림

- [ ] 실습: Firebase Admin SDK 연동
- [ ] 실습: FCM 토큰 저장 및 개별 / 토픽 푸시 발송 구현
- [ ] 실습: Kafka 이벤트 기반 비동기 푸시 파이프라인 구현

---

## PHASE 16 — Redis 심화

### 16-1. 이론

- [ ] Redis 자료구조 전체 정리 (String / Hash / List / Set / ZSet / Stream)
- [ ] Redis 영속성 정리 (RDB vs AOF)
- [ ] Redis Cluster vs Sentinel 차이 정리
- [ ] 캐시 전략 정리 (Cache-Aside / Write-Through / Write-Behind)
- [ ] 캐시 문제 유형 정리 (Cache Stampede / Cache Penetration / Cache Avalanche)

### 16-2. 실습

- [ ] 실습: Redis 자료구조별 활용 (랭킹 ZSet, 최근 본 상품 List, 태그 Set)
- [ ] 실습: 캐시 Stampede 방지 구현 (Mutex Lock, PER 알고리즘)
- [ ] 실습: Redis Streams 기반 간단한 이벤트 큐 구현
- [ ] 실습: 세션 클러스터링 구현 (Spring Session + Redis)
- [ ] 실습: Redisson 분산 락 구현

---

## PHASE 17 — 디자인 패턴

### 17-1. 이론

- [ ] GoF 23가지 패턴 분류 정리 (생성 / 구조 / 행위)
- [ ] Spring 내부에서 사용되는 패턴 목록 정리 (Template Method, Proxy, Strategy 등)

### 17-2. 실습

- [ ] 실습: Strategy 패턴 — 결제 수단 전략 구현
- [ ] 실습: Template Method 패턴 — 알림 발송 추상화
- [ ] 실습: Factory / Abstract Factory 패턴 구현
- [ ] 실습: Decorator 패턴 — 응답 가공 체인 구현
- [ ] 실습: Observer 패턴 — Spring Event 기반 구현 (`ApplicationEventPublisher`)
- [ ] 실습: Builder 패턴 — Lombok `@Builder` 없이 직접 구현 후 비교
- [ ] 실습: Proxy 패턴 — Spring AOP 없이 직접 프록시 구현
- [ ] 실습: Chain of Responsibility — 필터 / 검증 체인 구현

---

## PHASE 18 — 클린 코드 & 성능 최적화

### 18-1. 클린 코드 원칙

- [ ] SOLID 원칙 각각 정리 + Spring에서 위반 사례 / 적용 사례 분석
- [ ] Clean Code 핵심 원칙 정리 (명명 / 함수 / 주석 / 오류 처리)
- [ ] 코드 스멜(Code Smell) 종류 정리 및 리팩터링 기법 정리
- [ ] 실습: 기존 코드 SOLID 위반 지점 찾아 리팩터링

### 18-2. Java / JVM 성능 최적화

- [ ] String / StringBuilder / StringBuffer 성능 차이 정리
- [ ] Stream 병렬 처리 주의점 정리 (ForkJoinPool, 스레드 안전성)
- [ ] 불필요한 객체 생성 패턴 정리 (Autoboxing, 임시 객체)
- [ ] 실습: JVM 힙 사이즈 조정 및 GC 로그 분석으로 메모리 튜닝
- [ ] 실습: async-profiler로 CPU / 메모리 핫스팟 분석
- [ ] 실습: 비동기 처리 구현 (`@Async`, `CompletableFuture`) 전후 응답시간 비교

### 18-3. 애플리케이션 캐싱

- [ ] 캐시 적용 대상 선정 기준 정리 (읽기 빈도 / 데이터 변경 빈도 / 연산 비용)
- [ ] 실습: `@Cacheable` + 로컬 캐시(Caffeine) 적용
- [ ] 실습: Redis 분산 캐시 적용 전후 응답시간 비교
- [ ] 실습: 캐시 무효화 전략 구현 (`@CacheEvict`, TTL 설정)

### 18-4. 스케줄러 & 배치

- [ ] `@Scheduled` 주의점 정리 (단일 스레드 기본, 클러스터 환경 중복 실행 문제)
- [ ] Spring Batch 핵심 개념 정리 (Job / Step / Chunk / ItemReader / ItemWriter)
- [ ] 실습: `@Scheduled` + ShedLock으로 분산 환경 중복 실행 방지 구현
- [ ] 실습: Spring Batch로 대용량 데이터 마이그레이션 구현 (Chunk 처리)
- [ ] 실습: Batch 실패 시 재시작 / 재처리 전략 구현

---

## PHASE 19 — Docker & 컨테이너 환경

### 19-1. Docker 이론

- [ ] 컨테이너 vs VM 차이 정리
- [ ] Docker 이미지 레이어 구조 및 Dockerfile 최적화 원칙 정리
- [ ] **Docker 네트워크 심화 정리**
  - 모드별 동작 (bridge / host / overlay / none)
  - bridge 네트워크 내부 동작 (veth pair / Linux bridge / iptables NAT 규칙)
  - 컨테이너 간 DNS 기반 이름 해석 동작 원리
  - overlay 네트워크 동작 원리 (VXLAN 캡슐화, Swarm/k8s 환경)
- [ ] Docker Compose 개념 및 서비스 오케스트레이션 정리

### 19-2. 실습

- [x] 실습: Spring Boot 앱 Dockerfile 작성 (멀티 스테이지 빌드) → [정리](./notes/phase-19-docker/dockerfile-multistage.md)
- [ ] 실습: Docker Compose로 Spring + MySQL + Redis 환경 구성
- [ ] 실습: 환경변수로 `application.yml` 설정값 외부화
- [ ] 실습: `.dockerignore` 최적화 및 이미지 경량화
- [ ] 실습: Docker 네트워크 직접 생성 및 컨테이너 간 통신 확인
- [ ] 실습: Docker 볼륨으로 MySQL 데이터 영속화

---

## PHASE 20 — 컨테이너 오케스트레이션

### 20-1. 이론

- [ ] 컨테이너 오케스트레이션 필요성 정리 (단순 Docker Compose 한계)
- [ ] Docker Swarm vs k3s vs k8s 비교 정리 (복잡도 / 기능 / 적합 규모)
- [ ] k8s 핵심 오브젝트 정리 (Pod / Deployment / Service / Ingress / ConfigMap / Secret / PVC)
- [ ] k8s 네트워킹 모델 정리 (ClusterIP / NodePort / LoadBalancer / Ingress)
- [ ] Liveness Probe vs Readiness Probe vs Startup Probe 차이 정리
- [ ] k8s Rolling Update / Rollback 동작 원리 정리

### 20-2. Docker Swarm 실습

- [ ] 실습: Docker Swarm 클러스터 구성 (Manager 1 + Worker 2)
- [ ] 실습: Stack 배포 (`docker stack deploy`) — Spring + MySQL + Redis 서비스 구성
- [ ] 실습: 서비스 스케일 아웃 / 인 (`docker service scale`)
- [ ] 실습: Rolling Update 및 Rollback 실습
- [ ] 실습: Swarm Overlay 네트워크로 서비스 간 통신 확인

### 20-3. k3s 실습 (경량 k8s)

- [ ] 실습: k3s 단일 노드 설치 및 kubectl 연결
- [ ] 실습: Spring Boot 앱 Deployment + Service 배포
- [ ] 실습: ConfigMap / Secret으로 환경변수 외부화
- [ ] 실습: Ingress(Traefik) 설정으로 도메인 라우팅
- [ ] 실습: Liveness / Readiness Probe 설정 및 실패 시 재시작 확인
- [ ] 실습: HPA(Horizontal Pod Autoscaler)로 부하 기반 자동 스케일 아웃
- [ ] 실습: Rolling Update 전략 설정 및 무중단 배포 확인
- [ ] 실습: PersistentVolume으로 MySQL 데이터 영속화

### 20-4. k8s 심화 (선택 — AWS EKS 연계)

- [ ] 실습: Helm Chart로 애플리케이션 패키징 및 배포
- [ ] 실습: AWS EKS 클러스터 생성 및 kubectl 연결
- [ ] 실습: EKS + ALB Ingress Controller 설정
- [ ] 실습: ArgoCD 연동으로 GitOps 자동 배포 파이프라인 구성
- [ ] 실습: Istio Service Mesh 설치 및 트래픽 관리 (Canary 배포)

---

## PHASE 21 — AI 연동

### 21-1. 이론

- [ ] LLM API 호출 방식 정리 (Prompt Engineering 기초 — Zero-shot / Few-shot / Chain-of-Thought)
- [ ] LangChain4j vs Spring AI 비교 정리
- [ ] RAG(Retrieval Augmented Generation) 개념 정리
- [ ] 벡터 DB 개념 정리 (임베딩, 유사도 검색, HNSW 인덱스)
- [x] Harness Testing 정리 (AI 모델 평가 프레임워크, 할루시네이션/안전성/Prompt Injection 검증) → [정리](./notes/phase-21-ai/harness-testing.md)
- [ ] **AI Agent 개념 정리**
  - Agent란 무엇인가 (LLM + Tool + Memory + Planning 조합)
  - ReAct(Reasoning + Acting) 패턴 정리
  - Tool Calling / Function Calling 동작 원리
  - Multi-Agent 아키텍처 (Supervisor / Subagent 패턴)
- [ ] **MCP(Model Context Protocol) 정리**
  - MCP 개념 및 등장 배경 (AI 모델과 외부 도구 연결 표준 프로토콜)
  - MCP Server / Client / Host 구조 정리
  - MCP Tools / Resources / Prompts 개념
  - Claude Desktop / Cursor 등에서 MCP 동작 방식
  - Spring Boot MCP Server 구현 방식

### 21-2. 기본 실습

- [ ] 실습: Claude API / OpenAI API Spring Boot 연동
- [ ] 실습: Spring AI로 Chat API 엔드포인트 구현
- [ ] 실습: 문서 기반 RAG 파이프라인 구현 (벡터 DB 연동)
- [ ] 실습: 대화 이력 관리 (Redis 기반 세션)
- [ ] 실습: 스트리밍 응답 구현 (SSE 기반)

### 21-3. AI Agent 실습

- [ ] 실습: Tool Calling 기반 Agent 구현 (DB 조회 / 외부 API 호출 도구 연동)
- [ ] 실습: ReAct 패턴 Agent 구현 (추론 → 행동 → 관찰 루프)
- [ ] 실습: Multi-Agent 파이프라인 구현 (분석 Agent → 전략 Agent → 실행 Agent)
- [ ] 실습: Spring Boot MCP Server 구현 (Claude Desktop에서 직접 DB / API 호출)
- [ ] 실습: Agent 메모리 관리 (단기: 대화 이력 / 장기: 벡터 DB 저장)

### 21-4. 뉴스 기반 AI 예측 실습 (pong-to-rich 연동)

- [ ] 실습: 뉴스 RSS 피드 / 크롤링으로 실시간 뉴스 수집 파이프라인 구현
- [ ] 실습: LLM 기반 뉴스 감성 분석 (Positive / Negative / Neutral 분류)
- [ ] 실습: 종목별 뉴스 감성 점수 집계 → 매매 신호 생성 연동
- [ ] 실습: RAG 기반 종목 분석 리포트 자동 생성 (재무 데이터 + 뉴스 컨텍스트)
- [ ] 실습: KIS API 실시간 시세 + 뉴스 감성 점수 결합 전략 구현
- [ ] 실습: Kafka 기반 뉴스 이벤트 스트림 → 실시간 감성 분석 파이프라인

### 21-5. AI 보안 (Phase 31과 연계)

> 기본 개념은 여기서 다루고, 실습 구현은 Phase 31-2에서 진행한다.

- [ ] Prompt Injection 공격 원리 정리 → 실습은 Phase 31-2
- [ ] 벡터 DB 접근 제어 정리 → 실습은 Phase 31-2
- [ ] AI API 비용 폭탄 방지 전략 정리 → 실습은 Phase 31-2
- [ ] LLM 응답 검증 전략 정리 → 실습은 Phase 31-2

### 21-6. 시계열 분석 & ML 파이프라인 (심화 / 선택)

> 백엔드 개발자 필수 영역은 아님. pong-to-rich 예측 고도화 시점에 진행

- [ ] 시계열 데이터 특성 정리 (추세 / 계절성 / 잔차 분해)
- [ ] 통계 기반 예측 모델 정리 (ARIMA / SARIMA)
- [ ] 딥러닝 기반 시계열 모델 정리 (LSTM / GRU)
- [ ] Prophet 정리 (Facebook 개발, 트렌드 + 계절성 자동 분해)
- [ ] 금융 기술적 지표 정리 (이동평균 / RSI / MACD / 볼린저밴드)
- [ ] 백테스팅 개념 정리 (과거 데이터로 전략 검증, 룩어헤드 바이어스 주의점)
- [ ] 성과 지표 정리 (샤프 지수 / MDD / 승률 / 손익비)
- [ ] 실습: KIS API 시세 데이터 수집 → 기술적 지표 계산 파이프라인 구현
- [ ] 실습: LSTM 기반 주가 예측 모델 학습 및 Spring Boot API로 서빙
- [ ] 실습: 뉴스 감성 점수 + 시세 데이터 결합 피처 엔지니어링
- [ ] 실습: 백테스팅 엔진 구현 (전략별 수익률 / MDD 비교)
- [ ] 실습: MLflow로 모델 버전 관리 및 실험 추적
- [ ] 실습: 실시간 예측 파이프라인 (Kafka → 모델 추론 → 매매 신호 → KIS API 주문)

---

## PHASE 22 — NGINX & SSL

### 22-1. 이론

- [ ] NGINX 동작 원리 정리 (이벤트 기반 비동기 아키텍처)
- [ ] Reverse Proxy 개념 및 로드밸런싱 알고리즘 정리 (RR / Least Conn / IP Hash)
- [ ] TLS/SSL 핸드셰이크 과정 정리 (대칭키 / 비대칭키)
- [ ] HTTP/1.1 vs HTTP/2 vs HTTP/3 차이 정리 (멀티플렉싱, QUIC)

### 22-2. 실습

- [ ] 실습: NGINX Reverse Proxy 설정 (Spring Boot 앞단 배치)
- [ ] 실습: NGINX upstream 헬스체크 / keepalive / 버퍼 튜닝
- [ ] 실습: Certbot으로 Let's Encrypt SSL 인증서 발급 및 자동 갱신
- [ ] 실습: HTTPS 리다이렉트 및 HSTS 설정
- [ ] 실습: HTTP/2 활성화 및 성능 비교
- [x] 실습: Cloudflare Tunnel로 로컬 PC와 외부 연결 → [정리](./notes/phase-22-nginx/cloudflare-tunnel.md)

---

## PHASE 23 — MSA & Spring Cloud

### 23-1. MSA 이론

- [ ] 모놀리식 vs MSA 장단점 정리
- [ ] MSA 분리 기준 정리 (도메인 중심 설계, Bounded Context)
- [ ] 서비스 간 통신 방식 정리 (동기: REST / gRPC, 비동기: 이벤트)
- [ ] 분산 트랜잭션 문제 정리 (왜 `@Transactional`이 MSA에서 안 통하는가)
- [ ] 2PC(Two-Phase Commit) 원리 및 한계 정리
- [ ] Saga 패턴 두 방식 비교 정리 (Choreography vs Orchestration)
- [ ] 보상 트랜잭션(Compensating Transaction) 설계 원칙 정리

### 23-2. Saga & 보상 트랜잭션 실습

> 시나리오: 주문 서비스 → 결제 서비스 → 재고 서비스 → 배송 서비스

- [ ] 실습: **Choreography Saga** — Kafka 이벤트 기반 보상 트랜잭션 구현
- [ ] 실습: **Orchestration Saga** — Orchestrator 서비스가 흐름 제어하는 방식 구현
- [ ] 실습: 보상 트랜잭션 멱등성 보장 구현 (중복 이벤트 처리 방지)
- [ ] 실습: Saga 실패 시 부분 완료 상태 추적 및 모니터링 구현
- [ ] 실습: Choreography vs Orchestration 복잡도 / 디버깅 난이도 비교 정리

### 23-3. Spring Cloud 구성

- [ ] 실습: Eureka Server 구성 및 서비스 등록 / 발견
- [ ] 실습: Spring Cloud Gateway로 API Gateway 구현
- [ ] 실습: Config Server로 설정 중앙화
- [ ] 실습: Feign Client로 서비스 간 통신 구현
- [ ] 실습: Circuit Breaker (Resilience4j) 구현
- [ ] 실습: 모놀리식 프로젝트를 MSA로 분할 (도메인별 서비스 분리)

### 23-4. 수평 확장 & 성능 비교

- [ ] 실습: Docker Compose로 동일 서비스 인스턴스 3개 실행
- [ ] 실습: NGINX 로드밸런싱으로 트래픽 분산
- [ ] 실습: k6로 단일 인스턴스 vs 3중화 성능 비교
- [ ] 실습: 병목 지점 분석 및 튜닝

---

## PHASE 24 — Kafka & Elasticsearch

### 24-1. Apache Kafka

- [ ] Kafka 아키텍처 정리 (Broker / Topic / Partition / Consumer Group / Offset)
- [ ] Kafka vs RabbitMQ 비교 정리
- [ ] Kafka 메시지 전달 보장 수준 정리 (At Most Once / At Least Once / Exactly Once)
- [ ] 실습: Spring Kafka 기반 Producer / Consumer 구현
- [ ] 실습: MSA 서비스 간 비동기 이벤트 통신 구현
- [ ] 실습: Dead Letter Queue(DLQ) 패턴 구현
- [ ] 실습: Exactly Once Semantics(EOS) 트랜잭션 프로듀서 구현
- [ ] 실습: Kafka + Spark 스트리밍 파이프라인 구현 (로그 집계)

### 24-2. Elasticsearch

- [ ] ES 핵심 개념 정리 (Index / Document / Shard / Replica / Inverted Index)
- [ ] RDBMS 전문 검색 vs ES 검색 성능 비교 계획 수립
- [ ] 실습: Spring Data Elasticsearch 연동
- [ ] 실습: 형태소 분석기 적용 (nori analyzer)
- [ ] 실습: RDBMS LIKE 검색 vs ES 검색 성능 비교 (대용량 데이터)

---

## PHASE 25 — 모니터링 & Observability

### 25-1. 이론

- [ ] Observability 3대 요소 정리 (Metrics / Logs / Traces)
- [ ] SLI / SLO / SLA / Error Budget 개념 정리
- [ ] 알림(Alert) 설계 원칙 정리 (증상 기반 vs 원인 기반)

### 25-2. 실습

- [ ] 실습: Spring Actuator + Micrometer + Prometheus 세팅
- [ ] 실습: Grafana 대시보드 구성 (JVM / DB 커넥션 풀 / API 응답시간 / 에러율)
- [ ] 실습: Liveness / Readiness Endpoint 설정 및 k8s Probe 연동
- [ ] 실습: ELK Stack 로그 파이프라인 구성 (Logstash → Elasticsearch → Kibana)
- [ ] 실습: Zipkin / Jaeger로 분산 추적(Distributed Tracing) 구현
- [ ] 실습: Grafana Alert → Slack 연동
- [ ] 실습: 커스텀 메트릭 등록 (Micrometer `Counter` / `Gauge` / `Timer`)

---

## PHASE 26 — CI/CD 파이프라인

### 26-1. 이론

- [x] CI/CD 개념 정리 (Continuous Integration / Delivery / Deployment 차이) → [정리](./notes/phase-26-cicd/cicd-concepts.md)
- [ ] GitHub Actions vs Jenkins vs GitLab CI 비교 정리
- [ ] GitOps 개념 정리 (ArgoCD 기반)
- [ ] 브랜치 전략 정리 (Git Flow vs GitHub Flow vs Trunk-Based)
- [ ] 배포 전략 정리 (Blue-Green / Canary / Rolling / Recreate)

### 26-2. GitHub Actions 실습 (Push 방식)

- [x] 실습: PR 시 자동 테스트 실행 워크플로우 구성 → [정리](./notes/phase-26-cicd/github-actions.md)
- [x] 실습: main 브랜치 머지 시 빌드 → Self-hosted Runner 배포 파이프라인 → [정리](./notes/phase-26-cicd/github-actions.md)
- [ ] 실습: main 브랜치 머지 시 Docker 이미지 빌드 → GHCR / ECR push 파이프라인
- [ ] 실습: ECS / EC2 자동 배포 파이프라인 구성
- [ ] 실습: 환경별 배포 분리 (dev / staging / prod)
- [ ] 실습: Slack 배포 알림 연동

### 26-3. GitOps & ArgoCD (k8s 환경 Pull 방식)

> k8s 환경에서는 CI(빌드/테스트)와 CD(배포)를 분리하는 것이 표준. CI는 GitHub Actions, CD는 ArgoCD가 담당

- [ ] **Push 방식 vs Pull 방식 (GitOps) 차이 정리**
  - Push: CI가 직접 서버에 배포 명령 → 서버 접근 권한을 CI가 가짐
  - Pull(GitOps): ArgoCD가 Git 상태를 주기적으로 감지 → Git이 단일 진실 소스(Source of Truth)
- [ ] **GitOps 원칙 정리** (선언적 인프라 / Git이 유일한 진실 / 자동 동기화 / 변경 감지)
- [ ] **ArgoCD 동작 원리 정리** (App of Apps 패턴, Sync / OutOfSync / Degraded 상태)
- [ ] **Helm Chart 기반 배포 정리** — 환경별 values.yaml로 설정 분리
- [ ] 실습: GitHub Actions로 이미지 빌드 → GHCR push (CI 단계)
- [ ] 실습: Helm Chart로 배포 매니페스트 관리 (k8s Deployment / Service / Ingress)
- [ ] 실습: ArgoCD 설치 및 Git 저장소 연동
- [ ] 실습: 이미지 태그 변경 → Git 커밋 → ArgoCD 자동 동기화 전체 흐름 구성
- [ ] 실습: ArgoCD Rollback 실습 (이전 Git 커밋으로 되돌리기)

### 26-3. Jenkins 실습

- [ ] 실습: Jenkins 서버 Docker로 구성
- [ ] 실습: Jenkinsfile 기반 파이프라인 구성 (Build → Test → Deploy)
- [ ] 실습: GitHub Webhook 연동으로 자동 트리거

### 26-4. 코드 품질 자동화

- [ ] 실습: SonarQube 연동 (정적 분석 / 코드 커버리지)
- [ ] 실습: Jacoco 코드 커버리지 리포트 생성 및 기준 설정
- [ ] 실습: PR 시 커버리지 미달이면 머지 차단하는 워크플로우 구성

---

## PHASE 27 — AWS 배포 & 인프라

### 27-1. AWS 기초 이론

- [ ] AWS 핵심 서비스 정리 (EC2 / RDS / ElastiCache / S3 / ECR / ECS / EKS / SQS / CloudFront / WAF)
- [ ] IAM 이론 정리 (User / Role / Policy / 최소 권한 원칙 / AssumeRole)
- [ ] VPC 구조 정리 (Public/Private Subnet / NAT Gateway / Security Group / NACL)
- [ ] **AWS SAA 핵심 개념 정리**
  - EC2 구매 옵션 비교 (On-Demand / Reserved / Spot / Dedicated)
  - S3 스토리지 클래스 비교 (Standard / IA / Glacier / Intelligent-Tiering)
  - RDS Multi-AZ vs Read Replica 차이 (고가용성 vs 읽기 성능)
  - ELB 종류 비교 (ALB / NLB / CLB)
  - Auto Scaling 정책 종류 (Target Tracking / Step / Scheduled)
  - Route53 라우팅 정책 (Simple / Weighted / Latency / Failover / Geolocation)
  - CloudFront 캐시 동작 원리 (Origin / Edge Location / TTL / Invalidation)
  - SQS vs SNS vs EventBridge 사용 시나리오 구분
  - Kinesis vs SQS 비교 (스트리밍 vs 큐잉)
- [ ] AWS Well-Architected Framework 6대 원칙 정리

### 27-2. 핵심 인프라 실습

- [ ] 실습: IAM 사용자 및 역할 설정 (EC2 → RDS 접근 Role, 최소 권한)
- [ ] 실습: VPC / Subnet / Security Group 직접 설계
- [ ] 실습: EC2에 Docker 기반 Spring Boot 배포
- [ ] 실습: RDS (MySQL) + ElastiCache (Redis) 연결
- [ ] 실습: Parameter Store / Secrets Manager로 민감 정보 관리
- [ ] 실습: S3 파일 업로드 / 다운로드 구현
- [ ] 실습: CloudFront + S3로 정적 파일 CDN 배포

### 27-3. 컨테이너 배포 실습

- [ ] 실습: ECR + ECS(Fargate)로 컨테이너 배포
- [ ] 실습: GitHub Actions CI/CD 파이프라인 구성 (빌드 → ECR push → ECS 배포)
- [ ] 실습: Route53 + ACM으로 커스텀 도메인 + HTTPS 적용
- [ ] 실습: Auto Scaling Group으로 수평 확장 설정

### 27-4. 보안 & 고가용성

- [ ] 실습: AWS WAF로 IP 차단 / SQL Injection / Rate Limiting 규칙 설정
- [ ] 실습: ALB Access Log → S3 → Athena로 접근 로그 분석
- [ ] 실습: Multi-AZ RDS 설정 및 Failover 테스트

---

## PHASE 28 — 성능 테스트 & 분석

### 28-1. 이론

- [ ] **성능 테스트 종류 완전 정리**
  - **Smoke Test** — 최소 부하(VU 1~5)로 기본 동작 확인. 배포 직후 제일 먼저 실행
  - **Load Test** — 예상 정상 트래픽으로 시스템이 목표 성능을 유지하는지 확인
  - **Stress Test** — 한계까지 부하를 높여서 시스템이 어디서 무너지는지 확인
  - **Spike Test** — 갑작스러운 트래픽 급증에 어떻게 반응하는지 확인 (이벤트, 프로모션)
  - **Soak Test (Endurance)** — 장시간 지속 부하로 메모리 누수 / 리소스 고갈 확인
  - **Breakpoint Test** — 시스템이 실제로 다운되는 한계점 찾기
  - **Scalability Test** — 인스턴스 증설 시 성능이 선형으로 늘어나는지 확인
- [ ] **주요 성능 지표 정리** (TPS / RPS / Latency P50/P95/P99 / Throughput / Error Rate / Apdex)
- [ ] **병목 지점 유형 정리** (CPU bound / 메모리 / DB 커넥션 / Slow Query / 네트워크 / 스레드 풀)

### 28-2. 부하 테스트 도구 비교

- [ ] **부하 테스트 도구 비교 정리**
  - **k6** — JavaScript, 코드 기반, CI/CD 연동 최적, 현재 업계 표준 트렌드
  - **JMeter** — Java, GUI 있음, 기업 환경에서 오래 사용됨, 무겁고 XML 설정
  - **Gatling** — Scala DSL, 리포트가 깔끔해서 경영진 보고용, 코드 기반
  - **Locust** — Python, 코드 기반, 분산 테스트 쉬움
  - **nGrinder** — 네이버 오픈소스, JMeter 기반, 국내 기업 다수 사용
  - **wrk / hey** — CLI 기반 초간단 부하 테스트, 빠른 스모크용
- [ ] **도구 선택 기준 정리** — 팀 언어 / CI 연동 / 리포트 요구사항 / 분산 테스트 필요 여부

### 28-3. k6 실습

- [ ] 실습: k6 스크립트 기본 구조 작성 (VU / duration / thresholds)
- [ ] 실습: 시나리오별 스크립트 작성 (Smoke / Load / Stress / Spike / Soak)
- [ ] 실습: k6 HTML 리포트 생성 및 분석
- [ ] 실습: GitHub Actions CI 파이프라인에 k6 Smoke Test 추가 (배포 후 자동 실행)
- [ ] 실습: Grafana + InfluxDB로 k6 실시간 대시보드 구성

### 28-4. k8s 환경 분산 부하 테스트

- [ ] **k6 Operator 정리** — k8s Pod로 k6 분산 실행, 대규모 부하 생성
- [ ] 실습: k6 Operator 설치 및 TestRun CRD로 분산 부하 테스트 실행
- [ ] 실습: 단일 실행 vs 분산 실행 부하 비교

### 28-5. 카오스 엔지니어링

> 부하 테스트가 "얼마나 버티나"라면, 카오스 엔지니어링은 "장애가 생겼을 때 어떻게 회복하나"

- [ ] **카오스 엔지니어링 개념 정리** (Netflix Chaos Monkey에서 시작, 의도적 장애 주입으로 복원력 검증)
- [ ] **카오스 도구 비교 정리**
  - **Chaos Mesh** — CNCF, k8s 네이티브, 네트워크 지연/패킷 손실/Pod 킬 등
  - **Litmus Chaos** — CNCF, k8s 카오스 실험 라이브러리, 워크플로우 기반
- [ ] 실습: Chaos Mesh 설치 및 네트워크 지연 주입 실험
- [ ] 실습: Pod 랜덤 킬 실험 → Circuit Breaker 동작 확인
- [ ] 실습: 카오스 실험 결과 리포트 작성 (장애 주입 → 감지 시간 → 복구 시간)

### 28-6. 성능 분석 실습

- [ ] 실습: async-profiler / IntelliJ Profiler로 CPU / 메모리 핫스팟 분석
- [ ] 실습: DB Slow Query 로그 분석 및 인덱스 최적화
- [ ] 실습: 캐싱 적용 전후 성능 비교
- [ ] 실습: **단계별 성능 비교 리포트 작성**
  - 단일 인스턴스 → 수평 확장 → 캐시 도입 → ES 도입 → 비동기 처리 도입

---

## PHASE 29 — 장애 대응 & 트러블슈팅 & 유지보수

### 29-1. 유지보수 관점 이론

- [ ] **운영 대시보드 필수 지표 정리**
  - JVM: Heap 사용률 / GC 빈도 및 Stop-The-World 시간 / 스레드 수
  - API: 응답시간 P50 / P95 / P99 / 에러율 / TPS
  - DB: 커넥션 풀 사용률 / Slow Query 빈도 / Lock Wait 시간
  - 인프라: CPU / 메모리 / 디스크 I/O / 네트워크 대역폭
- [ ] **알림(Alert) 임계값 설계 원칙 정리**
- [ ] **로그 레벨과 운영 로그 읽는 법 정리**
- [ ] **분산 추적(Trace ID)으로 요청 흐름 따라가는 법 정리**
- [ ] 장애 등급 분류 기준 정리 (P0 ~ P3, 서비스 영향도 기반)
- [ ] 장애 대응 절차 정리 (감지 → 확인 → 격리 → 임시 조치 → 근본 원인 분석 → 항구 조치)
- [ ] 사후 분석(Post-Mortem) 작성 방법 정리
- [ ] 런북(Runbook) 작성 방법 정리

### 29-2. 장애 유형별 트러블슈팅 실습

- [ ] 실습: **OutOfMemoryError 재현 및 대응** (Heap 덤프 분석, 메모리 누수 원인 찾기)
- [ ] 실습: **CPU 100% 장애 재현 및 대응** (jstack 스레드 덤프 분석, async-profiler)
- [ ] 실습: **스레드 데드락 재현 및 탐지** (jstack에서 BLOCKED + deadlock 패턴)
- [ ] 실습: **응답 시간 급증 재현 및 분석** (Grafana P99 → Trace ID → Slow Query)
- [ ] 실습: **커넥션 풀 고갈 장애 재현 및 대응**
- [ ] 실습: **DB Dead Lock 재현 및 대응** (`SHOW ENGINE INNODB STATUS`)
- [ ] 실습: **Slow Query 탐지 및 최적화**
- [ ] 실습: **OOM Killed 컨테이너 대응** (k8s OOMKilled 상태 확인)
- [ ] 실습: **Pod CrashLoopBackOff 대응**
- [ ] 실습: **외부 API 장애 전파 차단** (Circuit Breaker 동작 확인)

### 29-3. 무중단 운영 & 유지보수 실습

- [ ] 실습: Blue-Green 배포 구현 및 롤백 (NGINX upstream 전환)
- [ ] 실습: k8s Rolling Update 중 트래픽 유실 없는 배포 확인
- [ ] 실습: Canary 배포 구현 (10% → 50% → 100% 트래픽 점진적 전환)
- [ ] 실습: Flyway + Expand-Contract 패턴으로 컬럼 이름 변경 무중단 마이그레이션
- [ ] 실습: 인덱스 무중단 추가 (`ALGORITHM=INPLACE`)
- [ ] 실습: 대용량 테이블 무중단 데이터 마이그레이션 (배치 + Dual Write 패턴)
- [ ] 실습: Grafana Alert → Slack 연동으로 장애 자동 탐지
- [ ] 실습: 런북 기반 장애 대응 절차 문서화

### 29-4. 장애 예방 설계 실습

- [ ] 실습: Chaos Engineering 기초 — 의도적 장애 주입 후 복구 확인
- [ ] 실습: 헬스체크 엔드포인트 설계 (DB / Redis / 외부 API 연결 상태 포함)
- [ ] 실습: Graceful Shutdown 구현 (진행 중인 요청 완료 후 종료)
- [ ] 실습: 서킷브레이커 임계값 튜닝

---

## PHASE 30 — 결제 시스템 연동

### 30-1. 결제 이론

- [ ] PG(Payment Gateway) 동작 원리 정리 (클라이언트 → PG사 → 카드사 → 정산 흐름)
- [ ] 결제 상태 관리 정리 (PENDING → SUCCESS / FAIL / CANCEL / REFUND)
- [ ] 결제 멱등성 정리 (네트워크 오류로 중복 결제 요청 방지)
- [ ] 취소 / 부분 취소 / 환불 처리 흐름 정리
- [ ] 가상계좌 / 카드 / 간편결제 방식 비교 정리

### 30-2. PG 연동 실습 (토스페이먼츠 기준)

- [ ] 실습: 토스페이먼츠 테스트 환경 세팅 및 결제 위젯 연동
- [ ] 실습: 결제 승인 API 구현 (클라이언트 결제 완료 → 서버 최종 승인)
- [ ] 실습: 결제 금액 위변조 방지 구현 (서버에서 주문 금액 검증)
- [ ] 실습: 결제 취소 / 환불 API 구현
- [ ] 실습: 결제 Webhook 수신 처리 구현
- [ ] 실습: 결제 실패 시 주문 보상 트랜잭션 구현 (Saga 패턴 연계)

### 30-3. 동시 구매 처리 실습

> 시나리오: 재고 1개 남은 상품에 100명이 동시에 구매 시도

- [ ] 실습: 동시 구매 문제 재현 (재고 마이너스 되는 상황)
- [ ] 실습: 비관적 락으로 해결 및 성능 측정
- [ ] 실습: 분산 락(Redisson)으로 해결 및 성능 측정
- [ ] 실습: Kafka 기반 주문 큐잉으로 순차 처리 구현
- [ ] 실습: 방법별 TPS / 응답시간 비교 리포트 작성

---

## PHASE 31 — 보안 심화

> Phase 14에서 다룬 인증/인가를 넘어서, 실제 서비스 운영 / 금융 서비스 / AI 서비스에서 요구되는 보안을 다룬다.
> pong-to-rich는 주식 자동매매 + AI 분석 플랫폼이므로 이 Phase는 실무 필수 수준으로 다룬다.

### 31-1. 계정 보안 강화

- [ ] **로그인 실패 잠금 정리**
  - N회 실패 시 계정 잠금 원리 (Redis 기반 실패 카운터 + TTL)
  - 잠금 해제 전략: 시간 기반 자동 해제 vs 이메일 인증 해제
  - IP 기반 잠금 vs 계정 기반 잠금 차이
- [ ] **비밀번호 재설정 플로우 정리**
  - 이메일 토큰 방식 동작 원리 (1회용 토큰 발급 → 링크 클릭 → 검증 → 재설정)
  - 토큰 만료 시간 설정 기준 (보통 15~30분)
  - 재설정 토큰 DB 저장 vs Redis 저장 비교
- [ ] **2FA (Two-Factor Authentication) 정리**
  - TOTP (Time-based One-Time Password) 동작 원리 (Google Authenticator 방식)
  - SMS OTP vs TOTP 비교 (보안성 / 구현 복잡도)
  - 금융 서비스에서 2FA가 사실상 필수인 이유
- [ ] **동시 로그인 제어 정리**
  - 단일 기기 로그인 강제 vs 멀티 기기 허용 정책 결정 기준
  - 새 로그인 시 기존 세션 강제 만료 구현 방법
  - 새 기기 로그인 알림 (이메일 / 푸시)
- [ ] 실습: 로그인 실패 5회 → 30분 잠금 구현 (Redis 카운터 + TTL)
- [ ] 실습: 이메일 기반 비밀번호 재설정 플로우 구현 (토큰 발급 → 검증 → 재설정)
- [ ] 실습: TOTP 기반 2FA 구현 (Google Authenticator 연동)
- [ ] 실습: 동시 로그인 감지 + 기존 Refresh Token 강제 만료 구현

### 31-2. 금융 서비스 보안

- [ ] **멱등성(Idempotency) 완전 정리**
  - 네트워크 오류 / 클라이언트 재시도로 중복 요청이 들어올 때 한 번만 처리되는 원리
  - `Idempotency-Key` 헤더 방식 / Redis 기반 중복 감지 구현 방법
  - 주식 주문 / 결제에서 멱등성이 없으면 생기는 문제 시나리오
- [ ] **감사 로그(Audit Log) 설계 정리**
  - 금융 규정상 누가 언제 무엇을 했는지 변경 불가능한 로그로 남겨야 하는 이유
  - Append-only 테이블 설계 / 이벤트 소싱 기반 감사 로그 차이
  - `@CreatedBy` / `@LastModifiedBy` Spring Data Auditing 활용
- [ ] **금융 데이터 암호화 정리**
  - 저장 시 암호화 (Encryption at Rest) vs 전송 시 암호화 (Encryption in Transit)
  - 민감 컬럼 암호화 전략 (계좌번호 등) — AES-256 적용 방법
  - 키 관리 전략 (AWS KMS / HashiCorp Vault)
- [ ] **이상 거래 탐지 (FDS) 정리**
  - 비정상적인 매매 패턴 감지 원리 (단시간 대량 주문, 비정상 금액, 새벽 거래 등)
  - Rule-based FDS vs ML-based FDS 비교
  - 탐지 후 처리 전략: 거래 차단 / 알림 / 수동 검토 큐
- [ ] **전자금융거래법 핵심 요구사항 정리** (한국 금융 서비스 법적 의무사항)
  - 거래 기록 5년 보존 의무
  - 이용자 본인 확인 의무
  - 부정 거래 방지 시스템 구축 의무
- [ ] **PCI-DSS 개념 정리** — 카드 정보 직접 저장 금지, PG사 위임 이유, 규정 요약
- [ ] 실습: `Idempotency-Key` 기반 중복 주문 방지 구현 (Redis + 주식 주문 API 연동)
- [ ] 실습: 매매 이력 감사 로그 테이블 설계 및 AOP 기반 자동 기록 구현
- [ ] 실습: 민감 컬럼 AES-256 암호화 / 복호화 구현 (JPA AttributeConverter 활용)
- [ ] 실습: Rule-based 이상 거래 탐지 구현 (단시간 N회 주문 감지 → 자동 차단)

### 31-3. 데이터 보호 & 개인정보

- [ ] **로그 마스킹 정리**
  - 로그에 이메일 / 계좌번호 / 전화번호가 그대로 찍히면 안 되는 이유
  - Logback PatternLayout 커스터마이징으로 민감 정보 자동 마스킹 방법
  - 마스킹 패턴 예시: `test@test.com` → `te**@****.com`
- [ ] **개인정보보호법 / GDPR 핵심 요구사항 정리**
  - 수집 최소화 원칙 (필요한 데이터만 수집)
  - 보존 기간 정책 (언제까지 갖고 있을 수 있는가)
  - 탈퇴 사용자 데이터 처리 의무 (삭제 vs 익명화)
  - 개인정보 처리 방침 필수 고지 항목
- [ ] **데이터 보존 정책 설계 정리**
  - 탈퇴 후 즉시 삭제 vs 일정 기간 보존 후 삭제 vs 익명화 전략
  - Soft Delete vs Hard Delete 선택 기준
  - 자동 파기 스케줄러 설계 방법
- [ ] 실습: Logback 커스텀 마스킹 필터 구현 (이메일 / 계좌번호 자동 마스킹)
- [ ] 실습: 탈퇴 API 구현 (개인정보 즉시 익명화 + 거래 기록 보존)
- [ ] 실습: 데이터 자동 파기 스케줄러 구현 (`@Scheduled` + 보존 기간 초과 데이터 삭제)

### 31-4. 공격 탐지 & 보안 모니터링

- [ ] **보안 이벤트 로그 중앙화 (SIEM 개념) 정리**
  - SIEM이란 무엇인가 (Security Information and Event Management)
  - 어떤 이벤트를 보안 로그로 남겨야 하는가 (로그인 성공/실패, 권한 변경, 대량 조회 등)
  - ELK Stack 기반 보안 이벤트 수집 / 시각화 방법
- [ ] **IP 차단 / 허용 목록 관리 정리**
  - 동적 IP 차단 전략 (공격 감지 → 자동 차단 → 일정 시간 후 해제)
  - 관리자 IP 화이트리스트 관리 방법
  - AWS WAF / Cloudflare 기반 IP 차단 vs 애플리케이션 레벨 차단 비교
- [ ] **비정상 접근 패턴 탐지 정리**
  - 단시간 대량 요청 (DDoS 징후) 감지 방법
  - 크리덴셜 스터핑 공격 감지 (여러 계정 순차 로그인 시도)
  - 비정상 시간대 접근 / 비정상 지역 접근 감지
- [ ] 실습: 보안 이벤트 로그 구조 설계 및 ELK 연동 (로그인 실패 / 권한 오류 수집)
- [ ] 실습: AWS WAF 규칙 설정 (IP 차단 / Rate Limiting / SQL Injection 방어)
- [ ] 실습: 관리자 대시보드에서 실시간 보안 이벤트 모니터링 구현

### 31-5. AI 서비스 보안

- [ ] **Prompt Injection 공격 원리 및 방어 정리**
  - 사용자가 악의적인 프롬프트로 AI 동작을 조작하는 공격 패턴
  - RAG 시스템에서 Prompt Injection이 특히 위험한 이유 (외부 문서 → 프롬프트 주입)
  - 방어 전략: 입력 검증 / 시스템 프롬프트 분리 / 출력 필터링
- [ ] **벡터 DB 접근 제어 정리**
  - 임베딩된 데이터에 권한 없는 사용자가 접근하는 문제
  - 메타데이터 필터링 기반 접근 제어 (사용자별 데이터 격리)
  - 유사도 검색 결과에 권한 필터 적용 방법
- [ ] **AI API 비용 폭탄 방지 전략 정리**
  - 토큰 사용량 추적 방법 (입력 토큰 / 출력 토큰 분리 집계)
  - 사용자별 일일 토큰 한도 설정 + Redis 기반 카운터
  - 프롬프트 길이 제한 / 응답 max_tokens 제한으로 비용 상한선 설정
- [ ] **LLM 응답 검증 정리**
  - AI가 생성한 내용을 그대로 DB 저장 / 코드 실행하면 안 되는 이유
  - 응답 파싱 실패 / 형식 불일치 / 할루시네이션 대응 전략
- [ ] 실습: Prompt Injection 공격 재현 및 입력 검증 / 시스템 프롬프트 분리 방어 구현
- [ ] 실습: 사용자별 토큰 사용량 추적 + 일일 한도 초과 시 429 응답 구현
- [ ] 실습: 벡터 DB 메타데이터 필터링으로 사용자별 데이터 격리 구현

### 31-6. 외부 API 쿼터 관리

> 한투 Open API / AI API는 분당 요청 수 제한이 있음. 초과 시 계정 차단 또는 비용 폭탄.

- [ ] **외부 API 쿼터 관리 전략 정리**
  - 클라이언트 → 서버 요청 수 제한 (사용자별 / IP별 Rate Limiting)
  - 서버 → 외부 API 요청 수 제한 (전역 쿼터 카운터)
  - 쿼터 초과 시 응답 전략 비교: 즉시 거절(429) / 큐잉 / 캐시 응답
  - 한투 API 제한 기준 설계 방법
- [ ] 실습: Redis `INCR` + `EXPIRE` 기반 분당 쿼터 카운터 구현 (한투 API 연동)
- [ ] 실습: AI API 요청 전 쿼터 잔량 확인 → 초과 시 큐잉 처리 구현
- [ ] 실습: 쿼터 소진 알림 (임계치 80% 도달 시 Slack / 이메일 발송)

### 31-7. 의존성 & 공급망 보안

- [ ] **의존성 취약점 관리 정리**
  - Log4Shell 같은 라이브러리 취약점(CVE)이 어떻게 서비스를 뚫는지 원리 이해
  - OWASP Dependency-Check / Dependabot 동작 방식
  - 취약한 버전 사용 중인지 확인하는 방법 (`./gradlew dependencyCheckAnalyze`)
- [ ] **공급망 공격(Supply Chain Attack) 정리**
  - 오픈소스 라이브러리에 악성 코드가 심어지는 공격 패턴
  - 방어 전략: 의존성 버전 고정 / 내부 Nexus 미러 운영 / 서명 검증
- [ ] 실습: Dependabot 설정으로 취약한 의존성 자동 감지 구현
- [ ] 실습: OWASP Dependency-Check Gradle 플러그인 적용 및 CVE 리포트 생성

### 31-8. 인프라 보안

- [ ] **시크릿 중앙 관리 정리** (HashiCorp Vault / AWS Secrets Manager)
  - 현재 `.env` 방식의 한계 (파일 유출 시 전체 노출)
  - Vault로 동적 시크릿 발급 및 자동 갱신하는 원리
- [ ] **컨테이너 보안 정리**
  - Docker 이미지 취약점 스캔 (Trivy / Snyk)
  - non-root 사용자로 컨테이너 실행하는 이유 및 방법
  - 최소 권한 원칙 (컨테이너별 필요한 권한만 부여)
- [ ] **네트워크 보안 정리**
  - AWS Security Group / NACL 설계 원칙
  - 내부 서비스 간 mTLS 통신 (Istio 연계)
  - 퍼블릭에 노출하면 안 되는 포트 관리 (DB, Redis, Kafka)
- [ ] **제로 트러스트(Zero Trust) 아키텍처 정리**
  - "내부 네트워크라도 믿지 않는다" 원칙
  - 서비스 간 통신도 인증/인가를 거쳐야 하는 이유
  - BeyondCorp / Cloudflare Zero Trust 개념
- [ ] 실습: Docker 이미지 Trivy 취약점 스캔 및 non-root 사용자 설정
- [ ] 실습: HashiCorp Vault 연동으로 DB 비밀번호 / API 키 동적 주입
- [ ] 실습: Cloudflare Zero Trust로 관리자 페이지 접근 제어 구현

---

## PHASE 32 — 서비스 런칭 & 사업 운영

> 기술이 완성됐을 때 실제 서비스로 출시하고 사업을 운영하기 위해 챙겨야 할 것들.
> 개발자가 놓치기 쉬운 법적 / 정책적 / 운영적 항목들을 정리한다.

### 32-1. 법적 요건

- [ ] **사업자등록 정리**
  - 유료 서비스 운영 시 사업자등록 의무 (개인사업자 vs 법인 선택 기준)
  - 소프트웨어 업종 코드 및 부가세 처리
- [ ] **이용약관 작성 항목 정리**
  - 필수 포함 항목: 서비스 내용 / 요금 / 환불 정책 / 면책 조항
  - 투자 손실 면책 고지 문구 필수 포함 이유
  - 서비스 중단 / 변경 시 고지 의무
- [ ] **개인정보처리방침 작성 항목 정리**
  - 개인정보보호법 의무 고지 항목 (수집 항목 / 목적 / 보존 기간 / 제3자 제공 여부)
  - 개인정보 처리방침 UI 노출 위치 (푸터, 회원가입 동의)
- [ ] **외부 API 상업적 이용 약관 확인 정리**
  - OpenAI / Anthropic API 상업적 사용 허용 범위
  - 한투 Open API 상업적 서비스 사용 가능 여부 확인
  - API 위에 서비스를 얹어서 구독료 받는 구조의 합법성 (허용됨)
- [ ] **투자 관련 법규 정리**
  - 투자일임업 / 투자자문업 해당 여부 판단 기준
  - "도구 제공 + 투자 결정은 사용자" 구조가 합법인 이유
  - 면책 고지 문구 필수 위치 (서비스 내 AI 응답 하단, 약관)
  - 수익률 보장 표현 금지 이유 (유사수신행위)

### 32-2. 결제 & 구독 시스템

- [ ] **구독 모델 설계 정리**
  - 월정액 / 연간 / 사용량 기반(토큰 소모) 요금제 비교
  - 무료 플랜 + 유료 플랜 티어 설계 방법 (Freemium 전략)
  - 트라이얼 기간 처리 방법
- [ ] **구독 결제 플로우 정리**
  - 정기 결제(자동 갱신) 구현 원리 (PG사 빌링키 방식)
  - 결제 실패 시 재시도 전략 (Dunning Management)
  - 구독 취소 / 환불 정책 설계
- [ ] 실습: 토스페이먼츠 빌링키 기반 정기 결제 구현
- [ ] 실습: 구독 플랜 테이블 설계 및 플랜별 기능 제한 구현
- [ ] 실습: 결제 실패 → 재시도 스케줄러 구현

### 32-3. 관리자 & 운영 도구

- [ ] **관리자 대시보드 필수 기능 정리**
  - 가입자 현황 / 구독 현황 / 결제 내역 / 이탈률
  - 사용자별 토큰 사용량 / API 호출 현황
  - 보안 이벤트 로그 (로그인 실패 / 이상 접근)
- [ ] **고객 지원 시스템 정리**
  - 1:1 문의 처리 플로우
  - 환불 요청 처리 프로세스
  - 서비스 장애 시 사용자 공지 방법 (상태 페이지)
- [ ] 실습: 관리자 전용 API 구현 (Role 기반 접근 제어)
- [ ] 실습: 서비스 상태 페이지 구현 (정상 / 점검 / 장애 상태 표시)

### 32-4. 서비스 안정성 & SLA

- [ ] **가용성 등급(Nines) 정리**
  - 99% (Two Nines) — 연간 87.6시간 다운타임 허용. 단일 서버 수준
  - 99.9% (Three Nines) — 연간 8.76시간 / 월 43분. 헬스체크 + 자동 재시작으로 달성 가능
  - 99.99% (Four Nines) — 연간 52분 / 월 4.4분. 다중 AZ + 로드밸런서 + 무중단 배포 필요
  - 99.999% (Five Nines) — 연간 5.26분 / 월 26초. 글로벌 멀티 리전 + 자동 페일오버. 은행 코어 시스템 수준
  - **pong-to-rich 현실적 목표**: 초기 99.9% → 성장 후 99.99%
- [ ] **SLA(Service Level Agreement) 정리**
  - 가용성 목표 수치를 이용약관 / 서비스 상태 페이지에 명시하는 방법
  - SLA 위반 시 보상 정책 설계 (크레딧 환급 등)
  - 계획된 점검(Planned Maintenance)을 다운타임에서 제외하는 방법
- [ ] **점검 / 배포 정책 정리**
  - 무중단 배포 전략 (Blue/Green, Rolling, Canary)
  - 점검 시간대 선택 기준 및 사전 공지 방법
- [ ] **비용 최적화 정리**
  - AWS 비용 구조 파악 (EC2 / RDS / 데이터 전송 비용)
  - AI API 비용이 구독료 마진을 잠식하지 않도록 설계하는 방법
  - 사용자당 AI API 비용 계산 → 요금제 가격 책정 방법

---

## 추가 추천 주제 (심화 / 선택)

- [ ] **Flyway 심화**: 대규모 무중단 마이그레이션 전략 (Expand-Contract 패턴)
- [ ] **gRPC 심화**: Protobuf 직렬화 / Unary / Streaming 4가지 통신 방식 / Spring Boot 연동
- [ ] **Reactive Programming**: WebFlux + R2DBC (Blocking vs Non-Blocking 비교)
- [ ] **CQRS + Event Sourcing**: 상세 내용은 Phase 11-8 참고. Event Sourcing 심화는 여기서 다룸
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
| 1 | 네트워크 & 웹 기초 (OSI / TCP / HTTP / IO 모델) | 🔲 진행 전 |
| 2 | OS 기초 (프로세스 vs 스레드 / 가상 메모리 / CPU 스케줄링) | 🔲 진행 전 |
| 3 | Linux & Shell | 🟡 진행 중 |
| 4 | 환경 설정 & Gradle | 🟡 진행 중 |
| 5 | Git & 협업 워크플로우 | 🟡 진행 중 |
| 6 | Java & JVM | 🟡 진행 중 |
| 7 | Spring 핵심 이론 & 구조 | 🟡 진행 중 |
| 8 | RESTful API & 문서화 | 🟡 진행 중 |
| 9 | 예외 처리 & 로깅 전략 | 🔲 진행 전 |
| 10 | TDD | 🔲 진행 전 |
| 11 | 데이터베이스 & JPA | 🔲 진행 전 |
| 12 | AOP | 🔲 진행 전 |
| 13 | 커스텀 어노테이션 & 멀티모듈 | 🔲 진행 전 |
| 14 | Spring Security & 인증/인가 | 🔲 진행 전 |
| 15 | 알림 서비스 | 🔲 진행 전 |
| 16 | Redis 심화 | 🔲 진행 전 |
| 17 | 디자인 패턴 | 🟡 진행 중 |
| 18 | 클린 코드 & 성능 최적화 | 🔲 진행 전 |
| 19 | Docker | 🟡 진행 중 |
| 20 | 컨테이너 오케스트레이션 (Swarm / k3s / k8s / EKS) | 🔲 진행 전 |
| 21 | AI 연동 (Spring AI / RAG / Agent / MCP / 시계열 ML) | 🔲 진행 전 |
| 22 | NGINX & SSL | 🟡 진행 중 |
| 23 | MSA & Spring Cloud | 🔲 진행 전 |
| 24 | Kafka & Elasticsearch | 🔲 진행 전 |
| 25 | 모니터링 & Observability | 🔲 진행 전 |
| 26 | CI/CD 파이프라인 | 🔲 진행 전 |
| 27 | AWS 배포 & 인프라 (SAA 핵심 개념 포함) | 🔲 진행 전 |
| 28 | 성능 테스트 & 분석 & 카오스 엔지니어링 | 🔲 진행 전 |
| 29 | 장애 대응 & 트러블슈팅 & 유지보수 | 🔲 진행 전 |
| 30 | 결제 시스템 (PG 연동 / 환불 / 동시 구매 처리) | 🔲 진행 전 |
| 31 | 보안 심화 (금융 보안 / AI 보안 / 외부 API 쿼터 / 인프라 보안) | 🔲 진행 전 |
| 32 | 서비스 런칭 & 사업 운영 (법적 요건 / 구독 결제 / 관리자 / SLA) | 🔲 진행 전 |
