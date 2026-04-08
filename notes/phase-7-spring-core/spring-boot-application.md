# @SpringBootApplication & Bean 생명주기

---

## @SpringBootApplication

`PongToRichApplication.java` 에 붙어있는 이 어노테이션 하나가 사실 세 개의 어노테이션을 합친 것이다.

```java
@SpringBootApplication
public class PongToRichApplication {
    public static void main(String[] args) {
        SpringApplication.run(PongToRichApplication.class, args);
    }
}
```

### 구성 어노테이션

```
@SpringBootApplication
├── @SpringBootConfiguration   ← @Configuration 과 동일. 이 클래스가 설정 클래스임을 의미
├── @EnableAutoConfiguration   ← 의존성 기반으로 Bean 자동 설정 (JPA, Web 등)
└── @ComponentScan             ← 이 패키지 하위를 스캔해서 @Component 붙은 클래스를 Bean으로 등록
```

**@EnableAutoConfiguration 동작 원리:**
- `spring-boot-autoconfigure` 라이브러리 안에 수백 개의 자동 설정 클래스가 있음
- `@ConditionalOnClass` 등 조건부 어노테이션으로 의존성이 있을 때만 활성화
- 예: `mysql-connector-j` 가 있으면 `DataSourceAutoConfiguration` 이 자동으로 DataSource Bean 등록

---

## Spring 컨테이너 (ApplicationContext)

`SpringApplication.run()` 을 호출하면:

```
1. SpringApplication 생성
2. ApplicationContext 생성
3. @ComponentScan → Bean 후보 클래스 탐색
4. Bean 생성 및 의존관계 주입 (DI)
5. 초기화 콜백 실행
6. 서버 시작 (Tomcat)
7. 요청 대기
```

---

## Bean 생명주기

Spring이 Bean을 관리하는 전체 흐름:

```
1. 빈 생성        — 생성자 호출, 객체 인스턴스화
2. 의존관계 주입   — @Autowired, 생성자 주입 등으로 의존 객체 주입
3. 초기화 콜백    — @PostConstruct 메서드 실행
4. 사용           — 실제 비즈니스 로직에서 사용
5. 소멸 전 콜백   — @PreDestroy 메서드 실행
6. 소멸           — GC에 의해 제거
```

### @PostConstruct / @PreDestroy

```java
@Component
public class ExampleService {

    @PostConstruct
    public void init() {
        // Bean 생성 + DI 완료 후 실행
        // DB 커넥션 초기화, 캐시 로딩 등에 사용
    }

    @PreDestroy
    public void destroy() {
        // Bean 소멸 직전 실행
        // 리소스 반납, 커넥션 종료 등에 사용
    }
}
```

---

## Bean 스코프

| 스코프 | 설명 | 기본값 |
|--------|------|--------|
| `singleton` | 컨테이너당 인스턴스 1개 | O (기본값) |
| `prototype` | 요청할 때마다 새 인스턴스 생성 | — |
| `request` | HTTP 요청당 1개 (Web) | — |
| `session` | HTTP 세션당 1개 (Web) | — |

대부분의 Bean은 `singleton` 으로 사용한다.
→ **싱글톤 Bean은 상태(필드 값)를 가지면 안 된다** — 여러 요청이 공유하기 때문에 동시성 문제 발생

---

## 정리

| 개념 | 설명 |
|------|------|
| @SpringBootApplication | @Configuration + @EnableAutoConfiguration + @ComponentScan 합친 것 |
| @EnableAutoConfiguration | 의존성 기반 자동 Bean 설정 |
| @ComponentScan | 패키지 스캔해서 Bean 등록 |
| ApplicationContext | Spring 컨테이너. Bean 생성/관리/소멸 담당 |
| @PostConstruct | Bean 초기화 직후 실행 |
| @PreDestroy | Bean 소멸 직전 실행 |
| singleton | Bean 기본 스코프. 인스턴스 1개 공유 → 상태 가지면 안 됨 |
