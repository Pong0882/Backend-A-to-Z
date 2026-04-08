# 캐싱 패턴 (Caching Pattern)

## 개념

한 번 가져온 데이터를 메모리에 저장해두고, 같은 요청이 오면 다시 가져오지 않고 저장된 값을 재사용하는 패턴.

```
첫 번째 요청:  캐시 없음 → 외부 API 호출 → 결과 저장 (캐시 미스)
이후 요청:     캐시 있음 → 저장된 값 반환              (캐시 히트)
```

## 캐시 히트 / 캐시 미스

| 용어 | 의미 |
|------|------|
| 캐시 히트 | 캐시에 유효한 값이 있어서 바로 반환 |
| 캐시 미스 | 캐시가 없거나 만료되어 새로 가져옴 |

## pong-to-rich에서 사용된 곳

`KisAuthService` — 한투 API 액세스 토큰 재사용

한투 API는 토큰 발급에 시간이 걸리고, 같은 토큰을 유효 기간 동안 재사용할 수 있다.
매 요청마다 새 토큰을 발급하면 불필요한 API 호출이 발생하므로, 유효한 토큰을 메모리에 캐싱한다.

```java
// 캐싱용 필드
private String cachedToken;
private LocalDateTime tokenExpiredAt;

public String getAccessToken() {
    if (isTokenValid()) {
        return cachedToken;  // 캐시 히트 — 저장된 토큰 반환
    }
    return issueToken();     // 캐시 미스 — 새 토큰 발급
}

private boolean isTokenValid() {
    return cachedToken != null
            && tokenExpiredAt != null
            && LocalDateTime.now().isBefore(tokenExpiredAt);
}
```

토큰 만료 1시간 전에 갱신하도록 여유를 두었다 (`minusHours(1)`).

## 현재 방식의 한계

현재는 Spring Bean의 필드(메모리)에 토큰을 저장한다.
- 단일 서버, 단일 계정에서만 유효
- 서버 재시작 시 캐시 초기화 → 재발급 필요
- 다중 서버 환경에서는 서버마다 토큰이 따로 관리됨

## 확장 방향

다중 사용자 또는 다중 서버 환경이 되면 Redis로 전환한다.

```
현재: JVM 메모리 (Bean 필드)
추후: Redis (userId → token 매핑, TTL 자동 관리)
```

→ Phase 16 (Redis) 에서 다룰 예정
