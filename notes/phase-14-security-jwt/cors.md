# CORS (Cross-Origin Resource Sharing)

## SOP — 왜 브라우저가 기본으로 막는가

브라우저에는 **SOP (Same-Origin Policy)** 가 기본 적용된다.

**Origin = scheme + host + port** 3개가 모두 같아야 "같은 Origin"

```
https://pongtrader.pro:443/api/login
└── scheme  └── host         └── port
```

| 비교 대상 | Same Origin? | 이유 |
|---|---|---|
| `https://pongtrader.pro` vs `https://api.pongtrader.pro` | ❌ | host 다름 |
| `https://pongtrader.pro` vs `http://pongtrader.pro` | ❌ | scheme 다름 |
| `https://pongtrader.pro:443` vs `https://pongtrader.pro:8080` | ❌ | port 다름 |
| `https://pongtrader.pro/login` vs `https://pongtrader.pro/api` | ✅ | path는 Origin 구성 요소가 아님 |

**SOP가 없으면 생기는 문제:**
- 악성 사이트(`evil.com`)에서 JS로 `bank.com/transfer` API를 자유롭게 호출 가능
- 사용자의 쿠키/세션을 가로채 인증된 요청 위조 가능 (CSRF의 기반)

---

## CORS란

서버가 **"이 Origin에서 오는 요청은 허용한다"** 고 브라우저에게 알려주는 메커니즘.

> 서버는 응답을 보냈는데 **브라우저가 JS에게 결과를 숨기는 것**이다.
> `curl`로 직접 치면 잘 되는 이유가 이것 — CORS는 브라우저 보안 정책이지, 서버가 막는 게 아니다.

---

## Preflight (사전 요청)

GET 이외의 변경성 요청(POST/PUT/DELETE/PATCH) 전에 브라우저가 먼저 `OPTIONS`로 허가를 묻는다.

```
1. 브라우저 → OPTIONS /api/auth/login
              Origin: https://pongtrader.pro
              Access-Control-Request-Method: POST

2. 서버    → 200 OK
              Access-Control-Allow-Origin: https://pongtrader.pro
              Access-Control-Allow-Methods: GET, POST, PUT, DELETE
              Access-Control-Allow-Headers: *
              Access-Control-Max-Age: 3600   ← 이 시간 동안 Preflight 캐싱

3. 브라우저 → 허용 확인 → 실제 POST 요청 전송
```

`Access-Control-Max-Age`: Preflight 결과를 캐싱하는 시간(초). 매 요청마다 OPTIONS를 보내지 않게 한다.

---

## 핵심 응답 헤더

| 헤더 | 역할 |
|------|------|
| `Access-Control-Allow-Origin` | 허용할 Origin. `*` 또는 명시적 URL |
| `Access-Control-Allow-Methods` | 허용할 HTTP 메서드 |
| `Access-Control-Allow-Headers` | 허용할 요청 헤더 (`Authorization` 포함 시 필요) |
| `Access-Control-Allow-Credentials` | 쿠키/Authorization 헤더 포함 요청 허용 여부 |
| `Access-Control-Max-Age` | Preflight 캐싱 시간(초) |

**`allowCredentials(true)` 주의:**
- `true`로 설정하면 `allowedOrigins("*")` 와일드카드 **사용 불가**
- Spring Security가 startup 시점에 예외를 던진다
- 반드시 명시적 Origin 목록으로 설정해야 함

---

## Spring Security에서 CORS 설정

Spring Security 필터 체인 안에서 CORS를 처리해야 한다.
`WebMvcConfigurer`로만 설정하면 Security 필터가 먼저 요청을 막아버린다.

```java
// SecurityConfig.java
http.cors(cors -> cors.configurationSource(corsConfigurationSource()))

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "https://pongtrader.pro",
        "https://api.pongtrader.pro",
        "http://localhost:5173"
    ));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);  // JWT Authorization 헤더 허용

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

---

## 로컬 개발에서 CORS 우회 — Vite 프록시

로컬에서 프론트(`localhost:5173`) → 백엔드(`localhost:8080`)는 **port가 다르므로 다른 Origin**.

Vite 개발 서버의 프록시를 쓰면 브라우저 입장에서는 "같은 Origin"으로 보인다.

```js
// vite.config.js
server: {
  proxy: {
    '/api': { target: 'http://localhost:8080', changeOrigin: true },
  }
}
```

브라우저 → `localhost:5173/api/...` → Vite 서버가 `localhost:8080/api/...`로 중계
→ 브라우저는 Origin이 바뀐 걸 모름 → CORS 발생 안 함

---

## pong-to-rich에서 사용된 곳

**파일:** `pong-to-rich/backend/src/main/java/.../security/SecurityConfig.java`

**목적:** `https://pongtrader.pro` 프론트엔드와 `https://api.pongtrader.pro` Swagger UI에서
백엔드 API(`api.pongtrader.pro`)로 보내는 요청 허용

**발생한 문제:**
- Swagger UI를 `https://api.pongtrader.pro/swagger-ui`로 접속
- Swagger JS가 `api.pongtrader.pro`로 API 호출 → 서버에 CORS 헤더 없음 → 브라우저 차단
- `curl`로 직접 치면 되는데 Swagger에서만 안 되는 이유가 CORS

**해결:** `SecurityConfig`에 `CorsConfigurationSource` 빈 추가 + `.cors()` 적용

**파일:** `pong-to-rich/frontend/vite.config.js`

**목적:** 로컬 개발 시 `localhost:5173` → `localhost:8080` CORS 우회
