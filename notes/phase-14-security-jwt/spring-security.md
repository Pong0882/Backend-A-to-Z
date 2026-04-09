# Spring Security 동작 원리

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
