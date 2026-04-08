# SSH 키 인증

## 개념

비밀번호 대신 **공개키/개인키 쌍**으로 인증하는 방식.
비밀번호는 네트워크로 전송되지만, 키 인증은 개인키를 로컬에만 두고 수학적 증명으로 인증한다.

## 공개키 / 개인키

| 키 | 위치 | 역할 |
|----|------|------|
| 개인키 (`id_rsa`) | 내 PC에만 보관 | 신원 증명용. 절대 외부에 노출 금지 |
| 공개키 (`id_rsa.pub`) | 서버의 `~/.ssh/authorized_keys` | 접속 허용 목록 |

```
내 PC                          서버
id_rsa (개인키)       →        id_rsa.pub가 authorized_keys에 등록되어 있음
          "내가 이 키를 가지고 있다" 증명
          ↓
          서버가 authorized_keys와 대조해서 허용/거부
```

비밀번호는 서버로 전송되지만, 개인키는 로컬을 절대 떠나지 않는다.

## authorized_keys

서버의 `~/.ssh/authorized_keys` 파일에 허용할 공개키를 한 줄씩 등록한다.

```bash
cat ~/.ssh/authorized_keys
# ssh-rsa AAAA... user@hostname
# ssh-rsa BBBB... another@host
```

Multipass는 VM 생성 시 내장 공개키를 자동으로 `authorized_keys` 에 등록해둔다.
그래서 `C:\ProgramData\Multipass\data\ssh-keys\id_rsa` 로 VM에 접속이 가능하다.

## SSH 접속 명령어

```bash
ssh -i "개인키 경로" 사용자명@호스트
```

```bash
# Multipass VM 접속 예시
ssh -i "C:/ProgramData/Multipass/data/ssh-keys/id_rsa" ubuntu@{VM_IP}
```

## Windows에서 키 파일 권한 설정

SSH 클라이언트는 개인키 파일의 권한이 너무 열려 있으면 보안상 거부한다.
Linux는 `chmod 600`, Windows는 `icacls` 로 설정한다.

```powershell
# 상속 권한 제거 + 현재 사용자에게만 읽기 권한 부여
icacls "C:\ProgramData\Multipass\data\ssh-keys\id_rsa" /inheritance:r /grant:r "사용자계정:R"
```

- `/inheritance:r` — 상위 폴더에서 상속받은 권한 제거
- `/grant:r "계정:R"` — 지정한 계정에게만 읽기(R) 권한 부여

권한이 너무 열려 있으면 발생하는 에러:
```
Load key "...\id_rsa": Permission denied
```

## pong-to-rich에서 사용된 곳

Multipass pong-server VM SSH 접속 및 IntelliJ Remote SSH 연결 시 사용.
Multipass 내장 키(`C:\ProgramData\Multipass\data\ssh-keys\id_rsa`)를 활용했다.
