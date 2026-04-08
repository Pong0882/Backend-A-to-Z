# Zero Trust (제로 트러스트)

## 개념

"절대 믿지 말고, 항상 검증하라 (Never Trust, Always Verify)"는 보안 모델.

기존 보안 모델은 내부 네트워크를 신뢰하고 외부만 차단하는 방식이었다.
Zero Trust는 내부/외부 구분 없이 모든 접근을 검증한다.

```
기존 모델:  외부 ❌  내부 ✅ (일단 들어오면 신뢰)
Zero Trust: 외부 ❌  내부 ❌ (항상 검증 후 허용)
```

## 핵심 원칙

1. **최소 권한** — 필요한 것만, 필요한 시간만 허용
2. **항상 검증** — 위치(내부/외부)와 상관없이 모든 요청 인증
3. **침해 가정** — 이미 뚫렸다는 가정 하에 설계 (피해 최소화)

## Cloudflare 터널과 Zero Trust

Cloudflare 터널이 Zero Trust 방식의 대표적인 사례다.

```
기존 방식:  외부 → 방화벽 → 서버 (포트 개방 필요)
터널 방식:  서버 → Cloudflare 아웃바운드 연결
            외부 → Cloudflare → 터널 → 서버
```

서버가 외부에 포트를 열지 않는다. 서버 자신이 Cloudflare에 먼저 연결을 맺고, 모든 트래픽은 그 채널을 통해서만 들어온다.
포트 스캐닝이나 직접 공격이 불가능하다.

## pong-to-rich에서 적용된 곳

`pong-server` (Multipass Ubuntu VM) — Cloudflare 터널로 `pongtrader.pro` 외부 노출

VM은 어떤 인바운드 포트도 열지 않았다. cloudflared가 Cloudflare에 아웃바운드 연결을 유지하고, 외부 요청은 그 터널을 통해 Spring Boot(8080)로 전달된다.
