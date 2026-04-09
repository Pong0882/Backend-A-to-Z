# Spring Security 동작 원리

## @EnableWebSecurity

Spring Security를 활성화하는 어노테이션. `@Configuration`과 함께 사용한다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig { ... }
```

이 어노테이션이 하는 일:
- Spring Security의 기본 보안 설정 활성화
- `SecurityFilterChain` Bean을 등록할 수 있게 해줌
- 없으면 Spring Security가 동작하지 않음

> Spring Boot에서는 `spring-boot-starter-security` 의존성만 추가해도 기본 Security가 적용되지만, `@EnableWebSecurity`로 커스텀 설정을 명시적으로 선언하는 게 일반적이다.

---

## Security Filter Chain 전체 흐름

Spring Security는 Servlet Filter 기반이다. DispatcherServlet(Spring MVC)에 요청이 닿기 **전에** 필터들이 먼저 가로챈다.

```
HTTP 요청
    ↓
DelegatingFilterProxy          ← Servlet 컨테이너 영역. Spring Bean을 필터로 위임
    ↓
FilterChainProxy               ← Spring Security 진입점. 필터 체인 관리
    ↓
Security Filter Chain          ← 필터들이 순서대로 실행
    │
    ├─ SecurityContextPersistenceFilter    ← SecurityContext 로드/저장 (세션 기반)
    ├─ UsernamePasswordAuthenticationFilter ← 폼 로그인 처리 (우리는 사용 안 함)
    ├─ JwtAuthenticationFilter (커스텀)    ← JWT 토큰 검사 (우리가 만든 것)
    ├─ ExceptionTranslationFilter          ← 인증/인가 예외를 HTTP 응답으로 변환
    └─ AuthorizationFilter                 ← 인가(권한) 최종 판단
    ↓
DispatcherServlet
    ↓
Controller
```

### DelegatingFilterProxy가 필요한 이유

Servlet 컨테이너(Tomcat)는 Spring의 ApplicationContext를 모른다. DelegatingFilterProxy가 Servlet 필터처럼 등록되어 있다가 실제 처리는 Spring Bean인 FilterChainProxy에 위임한다.

---

## 인증(Authentication) vs 인가(Authorization)

| 구분 | 의미 | 실패 시 HTTP 상태 |
|------|------|-----------------|
| 인증 | 너 누구야? (신원 확인) | 401 Unauthorized |
| 인가 | 너 이거 할 수 있어? (권한 확인) | 403 Forbidden |

인증이 먼저, 인가는 그 다음이다. 인증되지 않은 사용자는 인가 단계까지 가지 않는다.

---

## SecurityContext

인증된 사용자 정보를 **요청 스코프 동안** 보관하는 저장소.

```
요청 들어옴
    ↓
JwtAuthenticationFilter
    → 토큰 검증
    → Authentication 객체 생성
    → SecurityContextHolder.getContext().setAuthentication(authentication)
    ↓
Controller에서 꺼내 씀
    @AuthenticationPrincipal UserDetails user
    ↓
요청 끝나면 자동으로 비워짐
```

### ThreadLocal 기반

`SecurityContextHolder`는 기본적으로 **ThreadLocal** 기반이다. 같은 스레드 안에서만 SecurityContext가 공유된다.

```
요청 1 (Thread A) → SecurityContext A (사용자 김철수)
요청 2 (Thread B) → SecurityContext B (사용자 이영희)
```

스레드가 다르면 SecurityContext도 다르기 때문에 다른 사용자의 정보가 섞이지 않는다.

### JWT 방식에서 세션을 쓰지 않는 이유

세션 기반 인증은 서버 메모리에 인증 정보를 저장한다. 서버가 여러 대가 되면 세션 공유 문제가 생긴다. JWT는 토큰 자체에 정보가 담겨있어서 서버가 상태를 저장하지 않아도 된다 (Stateless).

---

## UserDetailsService

Spring Security가 사용자 정보를 로드하는 표준 인터페이스. `loadUserByUsername(String username)`을 구현해야 한다.

```
로그인 요청
    ↓
AuthenticationManager
    ↓
UserDetailsService.loadUserByUsername(email)  ← DB에서 사용자 조회
    ↓
UserDetails 반환 (email, password, roles)
    ↓
PasswordEncoder.matches() 로 비밀번호 검증
    ↓
Authentication 객체 생성 → SecurityContext 저장
```

우리는 JWT 방식이라 `UserDetailsService`를 로그인 시 직접 호출하지 않고 `AuthService`에서 직접 처리했다. 하지만 Spring Security 내부 구조를 이해하기 위해 구현해두었다.

---

## Filter vs Interceptor

| 구분 | 위치 | 접근 객체 |
|------|------|---------|
| Filter | DispatcherServlet 앞 | `HttpServletRequest` / `HttpServletResponse` |
| Interceptor | DispatcherServlet 뒤 | `HandlerMethod` (어느 Controller인지 알 수 있음) |

Spring Security는 **Filter** 기반이라 Spring MVC보다 먼저 실행된다. 인증/인가를 Controller 진입 전에 처리하기 위해서다.

### OncePerRequestFilter

`JwtAuthenticationFilter`가 상속하는 클래스. 하나의 요청에서 필터가 여러 번 실행되는 것을 방지한다. (Forward, Include 시 재실행 방지)

---

## CORS (Cross-Origin Resource Sharing)

### 왜 존재하는가

브라우저에는 **Same-Origin Policy(동일 출처 정책)** 가 있다. 다른 출처(Origin)에서 온 요청을 기본적으로 차단한다.

```
프론트: http://localhost:3000
백엔드: http://localhost:8080

→ 포트가 다름 → 다른 출처 → 브라우저가 요청 차단
```

**Origin = 프로토콜 + 도메인 + 포트.** 셋 중 하나라도 다르면 다른 출처다.

### Preflight 요청

브라우저는 실제 요청 전에 `OPTIONS` 메서드로 먼저 허용 여부를 물어본다.

```
브라우저 → OPTIONS /api/auth/login (Preflight)
서버 → Access-Control-Allow-Origin: http://localhost:3000
브라우저 → "허용됐네" → 실제 POST 요청 전송
```

서버가 CORS 허용 응답을 안 해주면 브라우저가 실제 요청 자체를 막아버린다.

### 핵심 포인트

**CORS는 브라우저가 하는 것이다.** Postman이나 서버 간 통신은 CORS와 무관하다. 그래서 Swagger/Postman에서는 잘 되는데 프론트에서만 안 되는 상황이 생긴다.

### Spring에서 설정하는 방법

`SecurityConfig`에 허용할 출처/메서드/헤더를 명시한다.

```java
.cors(cors -> cors.configurationSource(request -> {
    var config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("*"));
    return config;
}))
```

pong-to-rich는 프론트를 붙일 때 추가할 예정.

---

## CSRF (Cross-Site Request Forgery)

### 공격 원리

```
1. 사용자가 bank.com에 로그인 → 세션 쿠키 발급
2. 공격자가 만든 악성 사이트 방문
3. 악성 사이트에 숨겨진 폼이 자동으로 bank.com에 요청 전송
4. 브라우저가 쿠키를 자동으로 포함해서 보냄
5. 은행 서버는 정상 요청으로 인식 → 송금 실행
```

핵심은 **쿠키가 브라우저에 의해 자동으로 포함**된다는 점이다. 사용자가 의도하지 않은 요청이 본인 권한으로 실행된다.

### JWT에서 CSRF가 필요 없는 이유

JWT는 쿠키가 아닌 **Authorization 헤더**에 담아서 보낸다.

```
Authorization: Bearer eyJhbGci...
```

브라우저는 헤더를 자동으로 포함하지 않는다. 악성 사이트에서 헤더를 임의로 설정할 수 없다. 따라서 쿠키 자동 포함을 악용하는 CSRF 공격이 성립하지 않는다.

세션/쿠키 방식이면 CSRF 보호를 반드시 켜야 한다.

### SecurityConfig에서 disable하는 이유

```java
.csrf(AbstractHttpConfigurer::disable)
```

JWT 방식 + Authorization 헤더 → CSRF 공격 불가 → 비활성화해도 안전.

---

## pong-to-rich에서 사용된 곳

### SecurityConfig.java

```java
// JWT 방식 → 세션 불필요
.sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

// 경로별 접근 권한
.authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/api/auth/**", "/swagger", ...).permitAll()
        .anyRequest().authenticated()
)

// JWT 필터를 기존 폼 로그인 필터 앞에 배치
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

### JwtAuthenticationFilter.java

매 요청마다 실행. `Authorization: Bearer {token}` 헤더에서 토큰 추출 → 검증 → SecurityContext에 인증 정보 저장.

```java
SecurityContextHolder.getContext().setAuthentication(authentication);
```
