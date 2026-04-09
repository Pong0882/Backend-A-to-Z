# JWT (JSON Web Token)

## 구조

JWT는 `.`으로 구분된 3개의 Base64 인코딩 문자열로 구성된다.

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSJ9.abc123
        │                        │                          │
    Header (Base64)         Payload (Base64)          Signature
```

### Header

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

어떤 알고리즘으로 서명했는지 명시. `HS256`은 HMAC + SHA-256 대칭키 방식.

### Payload

```json
{
  "sub": "user@gmail.com",
  "role": "ROLE_USER",
  "iat": 1712000000,
  "exp": 1712086400
}
```

| 클레임 | 의미 |
|--------|------|
| `sub` | Subject — 토큰 주체 (보통 사용자 식별자) |
| `iat` | Issued At — 발급 시각 (Unix timestamp) |
| `exp` | Expiration — 만료 시각 (Unix timestamp) |
| `role` | 커스텀 클레임 — 사용자 역할 |

**Payload는 Base64로 인코딩된 것이지 암호화가 아니다.** 누구나 디코딩해서 볼 수 있으므로 비밀번호 같은 민감 정보는 절대 넣으면 안 된다.

### Signature

```
HMACSHA256(
  Base64(header) + "." + Base64(payload),
  서버_시크릿키
)
```

서버만 시크릿키를 알고 있다. Signature가 맞으면 **이 토큰은 우리 서버가 발급한 것**임이 보장된다. Payload를 누군가 변조하면 Signature가 달라져서 즉시 탐지된다.

---

## Access Token / Refresh Token 전략

토큰 하나만 쓰면 두 가지 문제가 생긴다.
- 유효기간이 길면 → 탈취당했을 때 오래 쓰임
- 유효기간이 짧으면 → 자주 로그인해야 해서 불편

두 토큰으로 역할을 분리해서 해결한다.

```
로그인 성공
    ↓
Access Token (30분) + Refresh Token (7일) 발급
    ↓
클라이언트: API 호출 시 Access Token을 헤더에 포함
    Authorization: Bearer {accessToken}
    ↓
Access Token 만료
    ↓
Refresh Token으로 새 Access Token 재발급 요청
    ↓
Refresh Token도 만료 → 재로그인
```

| 토큰 | 유효기간 | 용도 |
|------|---------|------|
| Access Token | 30분 | API 인증. 짧게 유지해서 탈취 피해 최소화 |
| Refresh Token | 7일 | Access Token 재발급 전용. 길게 유지해서 편의성 확보 |

---

## JWT 장단점

### 장점
- **Stateless** — 서버가 토큰 정보를 저장하지 않아도 됨. 서버를 여러 대로 늘려도 세션 공유 문제 없음
- **확장성** — MSA 환경에서 서비스 간 인증 전파가 쉬움 (토큰을 그대로 전달)

### 단점
- **토큰 무효화 불가** — 서버에 상태가 없으니 발급된 토큰을 강제로 만료시킬 수 없음. 로그아웃해도 토큰이 유효기간까지 살아있음
  - 해결: Redis에 블랙리스트 저장 (로그아웃된 토큰 목록)
- **Payload 노출** — 암호화가 아니라 인코딩이라 누구나 볼 수 있음. 민감 정보 넣으면 안 됨
- **토큰 크기** — 세션 ID(작음)보다 토큰이 훨씬 큼. 매 요청마다 헤더에 포함되어 전송

---

## HS256 vs RS256

| 방식 | 키 구조 | 사용 시점 |
|------|---------|----------|
| HS256 | 대칭키 (하나의 시크릿키) | 단일 서버, 토큰 발급/검증을 같은 서버가 함 |
| RS256 | 비대칭키 (개인키/공개키) | MSA 환경, 발급 서버(개인키)와 검증 서버(공개키)가 다를 때 |

pong-to-rich는 현재 단일 서버라 HS256 사용.

---

## pong-to-rich에서 사용된 곳

### JwtProvider.java

```java
// 시크릿키로 SecretKey 생성
this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

// Access Token 발급 (email + role + 만료시간)
public String generateAccessToken(String email, String role) {
    return Jwts.builder()
            .subject(email)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
}

// 토큰 검증
public boolean validateToken(String token) {
    try {
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        return true;
    } catch (Exception e) {
        return false;  // 만료, 변조, 형식 오류 모두 false
    }
}
```

### AuthService.java

로그인 성공 시 Access Token(30분) + Refresh Token(7일) 동시 발급.

```java
String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());
String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());
```
