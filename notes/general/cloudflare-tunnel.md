# Cloudflare 터널 & DNS

## DNS 기초

### DNS란
- 도메인 이름을 IP 주소로 변환해주는 시스템
- 브라우저에 `pongtrader.pro` 입력 시:
  1. DNS 서버에 "이 도메인의 IP가 뭐야?" 질문
  2. DNS 서버가 IP 반환
  3. 그 IP로 접속

### 네임서버(NS)란
- "이 도메인의 DNS 정보는 어느 서버한테 물어봐" 를 알려주는 서버
- 도메인마다 네임서버가 지정돼 있고, 그 네임서버가 DNS 레코드를 관리

```
pongtrader.pro 의 NS = ns.gabia.co.kr
→ pongtrader.pro DNS 정보는 가비아한테 물어봐
→ 가비아가 DNS 관리자
```

### DNS 레코드 종류

| 타입 | 역할 | 예시 |
|------|------|------|
| A | 도메인 → IPv4 주소 | `pongtrader.pro → 1.2.3.4` |
| AAAA | 도메인 → IPv6 주소 | `pongtrader.pro → ::1` |
| CNAME | 도메인 → 다른 도메인 | `pongtrader.pro → xxx.cfargotunnel.com` |
| NS | 네임서버 지정 | `pongtrader.pro → cesar.ns.cloudflare.com` |
| MX | 메일 서버 지정 | 이메일 수신용 |

---

## 도메인 등록 기관 vs DNS 관리

- **도메인 등록 기관** (가비아, GoDaddy 등) — 도메인을 내 소유로 등록해주는 곳. 도메인 구매는 여기서
- **DNS 관리자** — 실제 DNS 레코드(A, CNAME 등)를 관리하는 곳. 네임서버로 지정

둘은 분리 가능하다. 가비아에서 도메인을 사고, DNS 관리는 Cloudflare에 맡길 수 있다.

```
변경 전: 가비아 = 등록 기관 + DNS 관리자
변경 후: 가비아 = 등록 기관만 / Cloudflare = DNS 관리자
```

---

## Cloudflare

- CDN, DDoS 방어, DNS 관리, 터널 등을 제공하는 인프라 회사
- 무료 플랜으로 DNS 관리 + Cloudflare 터널 사용 가능

### Cloudflare를 DNS 관리자로 만드는 방법
1. Cloudflare에 도메인 추가
2. Cloudflare가 네임서버 2개 발급
3. 가비아에서 네임서버를 Cloudflare 꺼로 교체
4. 전파 완료 후 Cloudflare가 DNS 관리 담당

전파 확인:
```bash
nslookup -type=NS pongtrader.pro 8.8.8.8
```

---

## Cloudflare 터널 (Zero Trust Tunnel)

### 왜 필요한가
일반적으로 외부에서 내 서버에 접속하려면:
- 공인 IP 필요
- 공유기 포트 포워딩 필요
- ISP가 포트 차단하는 경우 불가

로컬 PC나 VM은 이 조건을 갖추기 어렵다.

### 동작 원리
```
일반 방식: 외부 → (공인IP:포트) → 서버  (인바운드)

터널 방식: 서버 → Cloudflare (아웃바운드 연결 먼저 맺음)
           외부 → Cloudflare → 터널 → 서버
```

- VM이 Cloudflare에 먼저 연결을 맺음 (아웃바운드)
- 외부 트래픽은 Cloudflare를 통해 터널로 들어옴
- 서버는 포트를 외부에 열 필요 없음

### 추가 장점
- HTTPS 자동 적용 (Cloudflare가 인증서 관리)
- DDoS 방어
- 실제 서버 IP 숨김

### Zero Trust와의 관계
Cloudflare 터널은 Zero Trust Network Access(ZTNA) 제품.
"절대 믿지 말고, 항상 검증" — 내부망이든 외부망이든 모든 접근을 인증/인가.
터널을 통해 접근하는 트래픽도 Cloudflare가 검증 후 통과시킴.

---

## cloudflared 설치 및 설정

### 설치
```bash
curl -L https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb -o cloudflared.deb
sudo dpkg -i cloudflared.deb
```

### 터널 생성
```bash
cloudflared tunnel login              # Cloudflare 계정 인증
cloudflared tunnel create <터널명>    # 터널 생성
```

### config.yml 작성
```yaml
tunnel: <터널명>
credentials-file: /home/ubuntu/.cloudflared/<tunnel-id>.json

ingress:
  - hostname: pongtrader.pro
    service: http://localhost:8080
  - service: http_status:404   # 매칭 안 되는 요청은 404
```

### DNS CNAME 레코드 등록
```bash
cloudflared tunnel route dns <터널명> <도메인>
# Cloudflare DNS에 CNAME 레코드 자동 등록
```

### systemd 서비스 등록 (자동 실행)
```bash
sudo cloudflared --config /home/ubuntu/.cloudflared/config.yml service install
sudo systemctl enable cloudflared
sudo systemctl start cloudflared

# 상태 확인
sudo systemctl status cloudflared
```

---

## pong-to-rich 인프라 구성

```
로컬 Windows PC
└── Multipass VM (pong-server, Ubuntu 24.04)
    ├── Docker (Spring Boot + MySQL + Redis)
    └── cloudflared (systemd 서비스로 상시 실행)
            ↕ 아웃바운드 터널
        Cloudflare Edge (인천 icn05/icn06)
            ↕
        외부 사용자
        pongtrader.pro
```
