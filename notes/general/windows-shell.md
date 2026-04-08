# Windows 쉘 종류 비교

## CMD (Command Prompt)

- Windows 최초 쉘 (DOS 시절부터)
- 기능이 매우 제한적 — 루프/조건문/파이프 처리 약함
- 배치 파일(`.bat`) 실행 환경
- 거의 레거시. 새 작업에는 쓰지 않는 게 좋음

## PowerShell

- Microsoft가 CMD를 대체하려고 만든 쉘
- `.NET` 기반 — 출력이 단순 텍스트가 아니라 **객체(Object)**
- `Get-VMHost`, `Get-Process` 같은 cmdlet으로 Windows 시스템 깊이 제어 가능
- Hyper-V, Active Directory, Azure 등 Windows 관리 작업은 PowerShell이 필수
- 스크립트 확장자: `.ps1`

## Git Bash (MINGW64)

- Git for Windows 설치 시 따라오는 쉘
- Linux/Unix 명령어(`ls`, `grep`, `curl`, `ssh` 등)를 Windows에서 그대로 사용 가능
- 내부적으로 MinGW(Minimalist GNU for Windows) 환경
- 터미널 프롬프트에 `MINGW64` 표시됨
- 개발자가 일상적으로 가장 많이 쓰는 쉘

## WSL (Windows Subsystem for Linux)

- Windows 안에서 진짜 Linux 커널을 실행하는 환경
- Git Bash보다 훨씬 완전한 Linux 환경 — `apt`, `systemctl`, `docker` 등 전부 사용 가능
- WSL2는 Hyper-V 기반 경량 VM으로 동작

## 언제 무엇을 쓰나

| 상황 | 쓸 쉘 |
|------|--------|
| 일반 개발 작업 (git, npm, gradle) | Git Bash |
| Windows 시스템 관리 (Hyper-V, 서비스, 레지스트리) | PowerShell |
| Linux 환경 필요 (apt, systemctl) | WSL |
| 레거시 배치 파일 실행 | CMD |

> multipass, Hyper-V VM 관련 명령어는 PowerShell에서 실행해야 제대로 동작하는 경우가 많다
