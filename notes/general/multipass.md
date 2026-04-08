# Multipass

## 개념

- Canonical(Ubuntu 만든 회사)이 만든 경량 VM 관리 도구
- Windows / macOS / Linux에서 Ubuntu VM을 빠르게 생성/관리할 수 있음
- Windows에서는 Hyper-V 드라이버를 사용해서 VM을 실행
- Docker와 비슷하게 CLI로 VM을 다루는 느낌

## Docker vs Multipass

| | Docker | Multipass |
|---|---|---|
| 단위 | 컨테이너 (프로세스 격리) | VM (OS 수준 격리) |
| OS | 호스트 커널 공유 | 독립된 Ubuntu 커널 |
| 용도 | 앱 패키징/배포 | 완전한 Linux 서버 환경 |
| 무게 | 가볍다 | 상대적으로 무겁다 |

## 설치 경로 관련 (Windows + Hyper-V)

Hyper-V 드라이버 사용 시 VM 데이터는 두 경로에 저장됨:

- **VirtualHardDiskPath** — VM 디스크 파일(`.vhdx`). 실제 OS/데이터가 쌓이는 곳. 용량 큼
- **VirtualMachinePath** — VM 설정 파일(`.vmcx`), 스냅샷 등. 용량 작음

경로 확인:
```powershell
Get-VMHost | Select-Object VirtualHardDiskPath, VirtualMachinePath
```

경로 변경:
```powershell
Set-VMHost -VirtualHardDiskPath "F:\Multipass\Virtual Hard Disks"
Set-VMHost -VirtualMachinePath "F:\Multipass\VMs"
```

> 변경 후 생성되는 VM부터 새 경로에 저장됨. 이미 만든 VM은 이동 안 됨

## 주요 명령어

### VM 생성
```bash
multipass launch --name <이름> --cpus <코어수> --memory <메모리> --disk <디스크>

# 예시
multipass launch --name pong-server --cpus 2 --memory 2G --disk 20G
```
- 이미지 미지정 시 최신 Ubuntu LTS 자동 선택
- 처음 실행 시 이미지 다운로드로 수 분 소요

### VM 목록 확인
```bash
multipass list
```
- Name / State / IPv4 / Image 확인 가능
- State: `Running` / `Stopped` / `Deleted`

### VM 접속
```bash
multipass shell <이름>
```
- VM 안 Ubuntu 터미널로 진입

### VM 시작 / 정지 / 삭제
```bash
multipass start <이름>
multipass stop <이름>
multipass delete <이름>
multipass purge          # delete한 VM 완전 제거
```

### VM 정보 확인
```bash
multipass info <이름>
```
- CPU, 메모리, 디스크 사용량 확인

### 호스트 ↔ VM 파일 전송
```bash
multipass transfer <로컬파일> <VM이름>:<VM경로>
multipass transfer <VM이름>:<VM파일> <로컬경로>
```

### 설정 확인
```bash
multipass get local.driver        # 드라이버 확인 (hyperv / qemu 등)
multipass version                 # 버전 확인
```

## pong-server VM 스펙

- 이름: `pong-server`
- OS: Ubuntu 24.04 LTS
- CPU: 2코어
- 메모리: 2GB
- 디스크: 20GB
- 드라이버: Hyper-V
- 데이터 저장 경로: `F:\Multipass\`
