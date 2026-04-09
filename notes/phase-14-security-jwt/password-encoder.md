# PasswordEncoder & BCrypt

## 왜 비밀번호를 암호화해야 하는가

DB가 털렸을 때 평문 비밀번호가 그대로 노출되면 안 된다. 암호화된 값만 저장해서 원본 비밀번호를 알 수 없게 해야 한다.

---

## 해시 vs 암호화

| 구분 | 방향 | 예시 |
|------|------|------|
| 암호화 | 양방향 (복호화 가능) | AES, RSA |
| 해시 | 단방향 (복호화 불가) | BCrypt, SHA-256 |

비밀번호는 **단방향 해시**를 써야 한다. 서버도 원본 비밀번호를 알 필요가 없다. 로그인 시 입력한 비밀번호를 해시해서 저장된 해시와 비교하면 된다.

---

## BCrypt

BCrypt는 비밀번호 해시에 특화된 알고리즘이다.

### 특징

**Salt 자동 포함** — 같은 비밀번호도 해시할 때마다 결과가 다르다.

```
"1234" → $2a$10$abc...xyz  (해시 1)
"1234" → $2a$10$def...uvw  (해시 2)  ← 다른 결과
```

Salt가 없으면 Rainbow Table 공격(미리 계산된 해시 테이블)으로 뚫린다. BCrypt는 Salt를 해시값 안에 포함시켜서 자동으로 방어한다.

**Cost Factor** — 해시 계산 비용을 조절할 수 있다. `$2a$10$` 에서 `10`이 cost factor. 숫자가 클수록 해시가 느려진다. 느릴수록 brute force 공격이 어려워진다.

### 검증 방식

```java
// 저장할 때
String encoded = passwordEncoder.encode("1234");
// DB에 "$2a$10$abc...xyz" 저장

// 로그인할 때
boolean matches = passwordEncoder.matches("1234", encoded);
// 입력값을 동일한 Salt로 해시해서 비교 → true
```

---

## pong-to-rich에서 사용된 곳

### SecurityConfig.java

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // cost factor 기본값 10
}
```

### AuthService.java

```java
// 회원가입 — 비밀번호 암호화 후 저장
User user = User.builder()
        .password(passwordEncoder.encode(request.getPassword()))
        .build();

// 로그인 — 입력 비밀번호와 저장된 해시 비교
if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
}
```
