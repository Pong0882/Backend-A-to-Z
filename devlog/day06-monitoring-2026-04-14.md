# Day 06 (모니터링) — 2026-04-14

## 오늘 한 것

- Prometheus + Grafana + Loki 모니터링 스택 세팅
- monitoring-server VM 분리 결정 및 생성
- 서브도메인 방식 결정 (grafana.pongtrader.pro)
- prometheus.yml 환경변수 치환 방식 도입 (envsubst)
- Cloudflare 터널 서브도메인 설정 방법 정리

---

## 모니터링을 왜 붙이는가

Redis 전환 전후 성능 비교를 하려고 k6로 부하 테스트를 계획했다.
숫자만 찍는 것보다 Grafana로 실시간으로 보면 훨씬 직관적이고 devlog에 스크린샷으로 남길 수 있다.

그리고 모니터링이 붙어있으면 k6 부하 → API 응답시간 그래프, HikariCP 커넥션 사용률, JVM 힙 메모리 변화를 한눈에 볼 수 있다.

**모니터링이 보는 것 3가지:**
- 애플리케이션 레이어 — 어떤 API가 몇 번 호출됐는지, 응답시간, 예외 발생 횟수
- 인프라 레이어 — CPU, 메모리, JVM 힙, GC, HikariCP 커넥션풀
- 로그 레이어 — 에러 로그, 요청/응답 로그 (Loki)

---

## 모니터링 스택 선택 — PLG vs ELK

처음에 로그 수집 도구로 ELK(Elasticsearch + Logstash + Kibana)와 PLG(Promtail + Loki + Grafana)를 비교했다.

**ELK를 쓰는 경우:**
- 로그를 전문 검색(Full-text search)해야 할 때
- "이 유저가 지난 한 달간 어떤 API를 호출했는지" 같은 복잡한 검색이 필요할 때
- MSA에서 여러 서비스 로그를 한곳에서 추적해야 할 때
- 대기업, 금융, 커머스처럼 로그 분석이 중요한 곳

**PLG를 선택한 이유:**
- Grafana 하나로 메트릭(Prometheus)이랑 로그(Loki)를 같은 화면에서 볼 수 있다
- Elasticsearch는 메모리를 많이 먹는데 VM 메모리가 제한적이다
- pong-to-rich 규모에서는 단순 모니터링으로 충분하다
- ELK는 MSA 구성할 때 Phase 후반에 경험하는 게 자연스럽다

---

## 모니터링 서버를 왜 분리했나

처음엔 pong-server docker-compose에 Prometheus + Grafana를 같이 올리려 했다.

근데 생각해보니 문제가 있다:

```
앱 서버가 죽었다
    → Prometheus도 같이 죽음
    → Grafana도 같이 죽음
    → 서버가 죽었는데 알림도 안 옴
```

모니터링의 목적이 "서버 장애 감지"인데, 장애가 나면 모니터링도 같이 죽어버리는 모순이다.
실무에서는 모니터링 서버를 별도 서버로 분리하는 게 표준이다.

```
앱 서버 (pong-server)
    → Spring Boot + MySQL 실행
    → /actuator/prometheus 메트릭 노출

모니터링 서버 (monitoring-server)
    → Prometheus — pong-server 메트릭 수집 (pull 방식)
    → Grafana — 시각화
    → Loki — 로그 저장
```

Prometheus가 주기적으로 앱 서버의 `/actuator/prometheus`를 **외부에서 HTTP로 호출(pull)** 해서 데이터를 가져오는 구조라 서버가 분리되어도 동작한다.

반면 Promtail은 앱 서버에서 로그를 읽어서 모니터링 서버로 **밀어내는(push)** 방식이라 앱 서버에 에이전트를 설치해야 한다.

---

## monitoring-server VM 스펙 결정

새 VM을 만들 때 메모리를 얼마나 줄지 고민했다.

각 컨테이너 메모리 사용량:
```
Prometheus   ~300MB
Grafana      ~200MB
Loki         ~300MB
OS           ~200MB
─────────────────────
합계         ~1GB
```

**1GB로 하면 안 되는 이유:**
딱 1GB면 여유가 없어서 OOM(메모리 부족)으로 컨테이너가 죽을 수 있다.

**2GB로 하면 생기는 문제:**
Multipass VM은 메모리를 **고정으로 예약**한다. CPU는 필요할 때만 점유하는 공유 방식이지만, 메모리는 VM이 실행 중인 동안 예약해둔다. VM 안에서 500MB만 써도 호스트 입장에선 2GB가 잡혀있다.

앞으로 VM이 더 늘어날 수 있어서 메모리를 아껴야 한다.

**1.5GB로 타협한 이유:**
Prometheus + Grafana + Loki 돌리기에 충분하고, pong-server 4GB에서 여유 메모리도 확보할 수 있다. 안 쓸 때는 `multipass stop monitoring-server`로 메모리를 반환할 수 있다.

```bash
multipass launch --name monitoring-server --cpus 1 --memory 1500M --disk 10G
```

생성 결과:
```
monitoring-server  Running  172.24.122.19  Ubuntu 24.04 LTS
```

같은 `172.24.x.x` 대역이라 pong-server(`172.24.126.133`)와 통신 가능하다.

---

## CPU 할당 vs 메모리 할당 차이

VM 스펙 고민하면서 알게 된 것:

```
메모리 (--memory)  → 고정 예약. VM이 실행 중이면 항상 점유
CPU    (--cpus)   → 공유 방식. 필요할 때만 점유, 놀고 있으면 호스트가 다 씀
```

CPU는 크게 신경 안 써도 되지만 메모리는 아껴야 한다.

---

## 서브도메인 방식 선택

Grafana를 외부에서 접근할 때 두 가지 방법을 비교했다.

**포트 방식:**
```
pongtrader.pro:3000  → Grafana
```

**서브도메인 방식:**
```
grafana.pongtrader.pro  → Grafana
```

**서브도메인을 선택한 이유:**

1. **포트를 외부에 열 필요 없다** — Cloudflare 터널은 VM이 아웃바운드로 먼저 연결하는 구조라 방화벽에서 포트를 열지 않아도 된다. 포트 방식은 3000, 9090을 열어야 하고 열린 포트는 공격 대상이 된다.

2. **HTTPS 자동 적용** — Cloudflare가 `*.pongtrader.pro` 와일드카드 인증서를 자동 처리한다. 포트 방식은 포트마다 인증서를 따로 발급해야 한다.

3. **서비스별 접근 제어** — Cloudflare Access로 `grafana.pongtrader.pro`에만 인증을 걸 수 있다. 일반 사용자는 앱만 접근하고, Grafana는 인증된 사람만 볼 수 있다.

4. **URL이 깔끔하다** — `grafana.pongtrader.pro` vs `pongtrader.pro:3000`

---

## Cloudflare 터널 동작 방식 (왜 config.yml에 http://localhost인가)

config.yml에 서비스를 `http://localhost:3000`으로 적는 이유가 궁금했다.

```
외부 브라우저
    → grafana.pongtrader.pro (HTTPS)
    → Cloudflare Edge 서버       ← 여기서 SSL 처리 (SSL Termination)
    → Cloudflare 터널 (암호화)
    → VM 안 cloudflared 프로세스
    → http://localhost:3000 (HTTP)  ← VM 내부 통신
    → Grafana 컨테이너
```

`cloudflared`와 Grafana가 **같은 VM 안**에 있으니까 localhost로 접근한다.
VM 내부 통신이라 HTTP로 충분하다 — 외부에 노출되지 않으니까.

HTTPS는 Cloudflare가 브라우저와의 구간을 담당하고, VM 내부는 HTTP로 통신한다.
이걸 **SSL Termination** 이라고 한다.

---

## config.yml 방식 vs Zero Trust 대시보드 방식

Cloudflare 터널 서브도메인을 추가하는 방법이 두 가지다.

**config.yml 방식 (현재 사용 중):**
- VM 안 파일을 직접 수정
- 적용하려면 cloudflared 재시작 필요
- DNS 레코드 명령어로 따로 등록
- 설정이 파일로 남아서 git으로 버전 관리 가능

**Zero Trust 대시보드 방식:**
- Cloudflare 웹사이트에서 클릭으로 설정
- 즉시 반영, DNS 자동 등록
- 팀 단위 운영에 적합

실무 트렌드는 IaC(Infrastructure as Code) 방향이라 config.yml처럼 파일로 관리하는 게 표준이 되고 있다. "클릭으로 만든 인프라는 어떻게 만들었는지 기록이 안 남는다"는 문제 때문이다.

이미 config.yml 방식으로 세팅되어 있어서 그대로 유지했다.

---

## prometheus.yml IP 노출 문제

prometheus.yml에 pong-server IP를 직접 쓰면 GitHub에 올라갔을 때 내부 네트워크 구조가 노출된다.

`172.24.126.133`은 Multipass 내부 사설 IP라 인터넷에서 접근이 불가능해서 실질적 위험은 없지만, 그래도 개선 방법을 고민했다.

**검토한 방법들:**

1. **.gitignore에 추가** — 가장 간단하지만 개발 상태를 git에 못 올리는 문제
2. **Prometheus 환경변수 치환** — Prometheus 자체는 환경변수 치환을 지원하지 않음
3. **envsubst** — template 파일에 변수로 써두고, 실행 전에 치환해서 실제 파일 생성

**envsubst 방식을 선택한 이유:**
- template 파일은 git에 올라가서 구조 파악 가능
- 실제 IP는 .env에만 있고 git 제외
- `./start.sh` 한 번에 치환 + 실행

```
prometheus.yml.template  ← git에 올라감 (${PONG_SERVER_IP})
.env                     ← git 제외 (PONG_SERVER_IP=172.24.126.133)
start.sh 실행 시         → envsubst로 prometheus.yml 생성 → docker compose up
```

---

## 변경된 파일

### pong-server (Spring Boot 앱)

**build.gradle** — micrometer-registry-prometheus 의존성 추가
```gradle
implementation 'io.micrometer:micrometer-registry-prometheus'
```

**application-local.yml / application-prod.yml** — actuator prometheus 엔드포인트 노출
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, prometheus, metrics
  endpoint:
    prometheus:
      enabled: true
  metrics:
    tags:
      application: pong-to-rich
```

**SecurityConfig.java** — `/actuator/prometheus` permitAll 추가
```java
"/actuator/health",
"/actuator/prometheus",   // Prometheus 메트릭 스크랩
```

**docker-compose.yml** — prometheus, grafana 제거 (monitoring-server로 분리)

### monitoring-server

**monitoring/docker-compose.yml** — Prometheus + Grafana + Loki

**monitoring/prometheus.yml.template** — 스크랩 설정 (IP 변수로)

**monitoring/start.sh** — envsubst 치환 + docker compose up

**.gitignore** — `monitoring/prometheus.yml` 추가

---

## Grafana 대시보드 연결

### Prometheus 데이터소스 연결

```
좌측 메뉴 → Connections → Data sources → Add data source → Prometheus
URL: http://pong-prometheus:9090
```

같은 docker-compose 네트워크(`monitoring_default`) 안에 있어서 컨테이너 이름으로 접근 가능하다.
IP가 바뀌어도 컨테이너 이름은 고정이라 신경 안 써도 된다.

→ **Successfully queried the Prometheus API** 확인

### 대시보드 임포트

처음에 ID `4701` (JVM 대시보드)로 임포트했는데 Instance 드롭다운 선택이 안 되고 데이터가 안 뜨는 문제가 있었다.

Spring Boot 3.x 전용 대시보드 ID `19004` 로 변경하니 정상 동작했다.

![Grafana Spring Boot 대시보드](https://github.com/user-attachments/assets/da8e3aad-b171-463f-bae3-b813d61ce733)

JVM 힙 메모리, GC, HTTP 요청수/응답시간, HikariCP 커넥션풀, CPU, 스레드 상태가 한눈에 보인다.

### /actuator/prometheus 화이트리스트 적용

Prometheus 타겟이 UP인 걸 확인하다가 `/actuator/prometheus` 가 외부에서도 접근 가능하다는 걸 발견했다.

`172.24.126.133:8080` 은 Multipass 사설 IP라 인터넷에서 직접 접근은 불가능하지만, Cloudflare 터널을 통해 `pongtrader.pro/actuator/prometheus` 로 접근하면 서버 내부 메트릭이 노출된다.

**화이트리스트 정책 적용:**
```java
// 내부 VM 네트워크(172.24.x.x)에서만 접근 가능
.requestMatchers("/actuator/prometheus")
    .access(new WebExpressionAuthorizationManager("hasIpAddress('172.24.0.0/16')"))
```

화이트리스트는 "허용할 것만 명시, 나머지 전부 차단" 하는 방식이다.
블랙리스트("막을 것만 명시")는 막아야 할 걸 다 알고 있어야 해서 현실적으로 불가능하다.
Zero Trust 철학과 같다 — 기본적으로 아무도 믿지 않고, 명시적으로 허용된 것만 통과.

---

## 트러블슈팅

### cloudflared config 파일 경로 문제

`grafana.pongtrader.pro` 추가 후 접속이 안 됐다.

**상황:** `~/.cloudflared/config.yml` 을 수정했는데 반영이 안 됨

**원인:**
```
~/.cloudflared/config.yml    → 수동 실행용
/etc/cloudflared/config.yml  → systemd 서비스가 읽는 실제 파일
```

`cloudflared service install` 로 systemd 등록할 때 `/etc/cloudflared/` 에 파일이 복사되어 거기서 관리된다. 홈 디렉토리 파일을 수정해도 서비스에 반영이 안 됐던 것.

**해결:** `/etc/cloudflared/config.yml` 에 grafana 줄 추가 후 `sudo systemctl restart cloudflared`

앞으로 설정 변경은 `/etc/cloudflared/config.yml` 만 수정한다.

---

## monitoring-server systemd 자동 실행 등록

컴퓨터를 껐다 켜도 monitoring-server VM이 자동으로 올라오게 설정했다.

### 서비스 파일 생성

```bash
sudo tee /etc/systemd/system/monitoring-compose.service <<EOF
[Unit]
Description=Monitoring Stack (Prometheus + Grafana + Loki)
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/ubuntu/Backend-A-to-Z/pong-to-rich/monitoring
ExecStart=/usr/bin/docker compose up -d
ExecStop=/usr/bin/docker compose down
User=ubuntu

[Install]
WantedBy=multi-user.target
EOF
```

**각 항목 설명:**

`tee` 는 파일을 생성하고 내용을 쓰는 명령어다. `<<EOF ~ EOF` 는 여러 줄을 한번에 입력하는 heredoc 문법이다. `/etc/systemd/system/` 은 시스템 디렉토리라 `sudo` 가 필요하다.

```
[Unit]
Requires=docker.service  → docker가 먼저 떠야 실행 가능
After=docker.service     → docker 뜬 후에 실행

[Service]
Type=oneshot             → 명령어 한 번 실행하고 끝나는 타입
RemainAfterExit=yes      → 명령어 끝나도 서비스가 active 상태 유지
                           (docker compose up -d는 백그라운드 실행 후 바로 종료됨)
WorkingDirectory         → docker-compose.yml, .env 파일이 있는 경로
ExecStart                → 시작 시 실행 (docker compose up -d)
ExecStop                 → 중지 시 실행 (docker compose down)
User=ubuntu              → ubuntu 유저 권한으로 실행 (docker 그룹 멤버)

[Install]
WantedBy=multi-user.target → 일반 부팅 시 자동 실행
```

### 서비스 등록 및 실행

```bash
sudo systemctl daemon-reload      # 새 서비스 파일 읽도록 systemd에 알림
sudo systemctl enable monitoring-compose  # 부팅 시 자동 실행 등록
sudo systemctl start monitoring-compose   # 지금 당장 실행
sudo systemctl status monitoring-compose  # 상태 확인
```

**결과:**
```
Active: active (exited)
```

`active (exited)` 는 정상이다. `Type=oneshot` 이라 `docker compose up -d` 명령어가 성공적으로 실행되고 종료된 상태다. 컨테이너는 백그라운드에서 계속 실행 중이다.

pong-server의 `pong-compose.service` 와 완전히 동일한 구조다.

---

## 최종 인프라 구성

```
로컬 Windows PC
├── Multipass VM: pong-server (Ubuntu 24.04, CPU 2, 메모리 4GB)
│   ├── Docker: Spring Boot 앱 + MySQL
│   ├── cloudflared (systemd) → pongtrader.pro, grafana.pongtrader.pro 터널
│   └── /actuator/prometheus → 172.24.0.0/16 대역만 접근 허용 (화이트리스트)
│
└── Multipass VM: monitoring-server (Ubuntu 24.04, CPU 1, 메모리 1.5GB)
    └── Docker: Prometheus + Grafana + Loki (systemd 자동 실행)
        ├── Prometheus → pong-server:8080/actuator/prometheus 15초마다 스크랩 (pull)
        ├── Grafana → grafana.pongtrader.pro (Cloudflare 터널)
        └── Loki → 로그 수집 대기
```

외부에서 `grafana.pongtrader.pro` 접속 → Cloudflare 터널 → pong-server cloudflared → monitoring-server:3000 → Grafana

---

## 다음에 할 것

- SecurityConfig 화이트리스트 커밋 + 푸시 (CI/CD 배포)
- VM 재시작 시 IP 변경 문제 해결 (pong-server.mshome.net 테스트)
- k6 설치 + DB 상태 성능 측정
- Redis 전환 후 성능 비교
- Grafana Alert → 슬랙 연결
- Promtail 설치 (pong-server 로그 → Loki로 수집)
