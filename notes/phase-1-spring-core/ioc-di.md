# IoC / DI 정리

---

## IoC (Inversion of Control) — 제어의 역전

원래 객체는 자신이 사용할 의존 객체를 직접 생성한다:

```java
public class OrderService {
    private PaymentService paymentService = new PaymentService(); // 직접 생성
}
```

이 방식의 문제:
- `OrderService`가 `PaymentService`에 강하게 결합됨
- `PaymentService`를 바꾸려면 `OrderService` 코드도 수정해야 함
- 테스트할 때 Mock으로 교체 불가

IoC는 이 **제어권을 외부(Spring 컨테이너)에 넘기는 것**이다.
Spring이 객체를 대신 생성하고, 필요한 곳에 주입해준다.

---

## DI (Dependency Injection) — 의존성 주입

IoC를 구현하는 방법이 DI다. Spring 컨테이너가 의존 객체를 만들어서 주입해준다.

주입 방법은 3가지다.

---

### 1. 생성자 주입 (권장)

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

**왜 권장하나:**
- `final` 키워드로 불변성 보장 — 주입 후 변경 불가
- 객체 생성 시점에 의존관계가 모두 확정됨 → 누락되면 컴파일 에러
- 테스트 시 생성자로 Mock 객체 직접 주입 가능
- 순환 참조 문제를 애플리케이션 시작 시점에 잡아냄

> `@Autowired` 는 생성자가 하나뿐이면 생략 가능 (Spring 4.3+)

---

### 2. 필드 주입 (비권장)

```java
@Service
public class OrderService {
    @Autowired
    private PaymentService paymentService;
}
```

**왜 비권장인가:**
- `final` 사용 불가 → 불변성 보장 안 됨
- Spring 컨테이너 없이는 주입 불가 → 순수 Java 테스트 불가
- 의존관계가 외부에서 보이지 않음

---

### 3. 세터 주입 (선택적 의존관계에만 사용)

```java
@Service
public class OrderService {
    private PaymentService paymentService;

    @Autowired(required = false)
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

**언제 쓰나:**
- 의존관계가 선택적일 때 (없어도 동작해야 할 때)
- 런타임에 의존관계를 바꿔야 할 때 (거의 없음)

---

## Spring Bean

Spring 컨테이너가 관리하는 객체를 Bean이라고 한다.
`@Component`, `@Service`, `@Repository`, `@Controller` 등이 붙은 클래스는 Spring이 자동으로 Bean으로 등록한다.

```
@SpringBootApplication
    └── @ComponentScan — 해당 패키지 하위를 스캔해서 Bean 등록
```

---

## 정리

| 개념 | 설명 |
|------|------|
| IoC | 객체 생성/관리 제어권을 Spring 컨테이너에 넘김 |
| DI | Spring이 의존 객체를 대신 만들어서 주입해줌 |
| Bean | Spring 컨테이너가 관리하는 객체 |
| 생성자 주입 | 실무에서 표준. `final` + 불변성 + 테스트 용이 |
| 필드 주입 | 간편하지만 테스트/불변성 문제로 지양 |
