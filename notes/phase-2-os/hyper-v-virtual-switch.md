# Hyper-V 가상 스위치와 VM 고정 IP 설정

## Hyper-V란

Windows에 내장된 하이퍼바이저(Type-1 가상화). VMware나 VirtualBox와 달리 OS 위에서 돌아가는 게 아니라 하드웨어 바로 위에서 실행된다.

Multipass는 Windows에서 Hyper-V를 백엔드로 사용해서 VM을 만든다.

---

## 가상 스위치 3가지 타입

| 타입 | 설명 | 용도 |
|------|------|------|
| **외부(External)** | 호스트의 물리 NIC에 연결. VM이 실제 네트워크(인터넷)에 접근 가능 | 인터넷 연결이 필요한 VM |
| **내부(Internal)** | 호스트 ↔ VM 간 통신 가능. 인터넷 연결 없음 | VM 간 내부 통신, 고정 IP |
| **개인(Private)** | VM ↔ VM 간 통신만 가능. 호스트도 접근 불가 | 완전 격리 환경 |

**내부 스위치를 선택한 이유:**
- 호스트(Windows)에서 VM에 직접 접근 가능
- 인터넷 연결 없이 VM 간 통신 전용 네트워크 구성 가능
- 이 네트워크의 IP는 Multipass가 관리하지 않아서 고정 IP 부여 가능

---

## Multipass VM IP가 재시작마다 바뀌는 이유

Multipass는 기본적으로 **Default Switch**를 사용한다. Default Switch는 NAT 방식으로 동작하고, DHCP로 IP를 자동 할당한다.

```
Windows 재시작
    → Default Switch DHCP가 새 IP 할당
    → VM IP 변경
```

**내부 스위치로 고정 IP를 만드는 이유:**
- 내부 스위치는 DHCP 서버가 없음 → 우리가 직접 정적 IP 설정
- Multipass가 관리하지 않는 네트워크라 재시작해도 안 바뀜

---

## pong-to-rich 고정 IP 설정 과정

### 1. Hyper-V 관리자에서 내부 스위치 생성

```
Hyper-V 관리자 → 가상 스위치 관리자
→ 새 가상 네트워크 스위치 → 내부 선택
→ 이름: multipass-internal → 확인
```

### 2. 호스트 측 IP 설정

생성하면 Windows 네트워크 어댑터에 `vEthernet (multipass-internal)` 이 생긴다.

```
네트워크 연결 → vEthernet (multipass-internal) → 속성 → IPv4
IP: 192.168.100.1
서브넷: 255.255.255.0
게이트웨이: 없음
```

호스트가 `192.168.100.1`이 되고, VM들에게 `192.168.100.x` 대역을 수동으로 부여한다.

### 3. Multipass VM에 스위치 연결

PowerShell (관리자):

```powershell
# multipass-internal을 브리지 네트워크로 지정
multipass set local.bridged-network=multipass-internal

# VM에 연결 (반드시 stop 후 설정)
multipass stop pong-server
multipass set local.pong-server.bridged=true
multipass start pong-server
```

VM 안에 `eth1` 어댑터가 새로 생긴다. (`eth0`는 기존 Default Switch, `eth1`이 새 내부 스위치)

### 4. VM 안에서 netplan으로 고정 IP 설정

```bash
sudo nano /etc/netplan/60-eth1-static.yaml
```

```yaml
network:
  version: 2
  ethernets:
    eth1:
      addresses:
        - 192.168.100.10/24  # pong-server
```

```bash
sudo chmod 600 /etc/netplan/60-eth1-static.yaml  # netplan은 600 이하 권한 요구
sudo netplan apply
```

**주의:** `netplan apply` 후 네트워크 재설정으로 잠깐 shell이 멈출 수 있음. 30초 기다렸다가 재접속하면 됨.

---

## eth0 vs eth1 역할

```
eth0 — 기존 Multipass Default Switch
    → 인터넷 연결용 (docker pull, apt install 등)
    → 재시작마다 IP 바뀜 (172.29.xxx.xxx 대역)
    → Multipass가 자동 관리

eth1 — multipass-internal 내부 스위치
    → VM 간 통신 전용
    → 고정 IP (192.168.100.x 대역)
    → 우리가 직접 관리
```

인터넷은 eth0로, VM 간 통신(Prometheus 스크랩, Loki 로그 push)은 eth1로 한다.

---

## netplan version: 2 가 뭔가

netplan 설정 파일 스키마의 버전. 네트워크 프로토콜 버전이 아니다.
Ubuntu 18.04부터 netplan version 2 포맷을 사용하고 있다. version 1은 구버전이라 현재 거의 안 쓴다.

---

## 고정 IP 전환 시 함께 업데이트해야 할 곳

IP가 하드코딩된 곳이 여러 군데 있었다. 고정 IP로 전환할 때 **전부 한 번에** 바꿔야 한다.

| 파일 | 항목 | 변경 내용 |
|------|------|----------|
| `/etc/cloudflared/config.yml` (pong-server) | `grafana.pongtrader.pro` service URL | `172.24.xxx.xxx:3000` → `192.168.100.20:3000` |
| `monitoring/.env` (monitoring-server) | `PONG_SERVER_IP` | `172.24.xxx.xxx` → `192.168.100.10` |
| `pong-to-rich/.env` (pong-server) | `LOKI_URL` | `172.24.xxx.xxx` → `192.168.100.20` |
| `SecurityConfig.java` | actuator/prometheus 화이트리스트 | `172.24.0.0/16` → `192.168.100.0/24` |

---

## pong-to-rich에서 사용된 곳

- `pong-server` eth1: `192.168.100.10` — Prometheus 스크랩 타겟, Promtail 설치
- `monitoring-server` eth1: `192.168.100.20` — Prometheus + Grafana + Loki 실행
- `SecurityConfig.java` — `/actuator/prometheus` 접근을 `192.168.100.0/24` 대역으로 제한
- `monitoring/.env` — `PONG_SERVER_IP=192.168.100.10`
- `pong-to-rich/.env` — `LOKI_URL=192.168.100.20`
