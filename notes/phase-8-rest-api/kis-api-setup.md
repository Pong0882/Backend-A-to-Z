# KIS Developers API 세팅

한국투자증권 Open API. 모의투자 / 실전투자 모두 지원.

---

## 증권사 API 선택 과정

국내 주요 증권사 Open API 비교:

| 증권사 | API | REST 지원 | 모의투자 | 비고 |
|--------|-----|-----------|----------|------|
| 한국투자증권 | KIS Developers | O | O | Docker/Linux 환경 사용 가능 |
| 키움증권 | Open API+ | X | O | Windows 전용, ActiveX 설치 필요 |
| LS증권 | LS Open API | O | O | 문서화 부족 |
| 대신증권 | CYBOS Plus | X | X | Windows 전용 설치형 |

**KIS Developers 선택 이유:**
- REST API 지원 → Docker/Linux 서버 환경에서 사용 가능
- 모의투자 지원 → 실제 돈 없이 개발/테스트 가능
- 문서화가 가장 잘 되어 있음
- 키움/대신은 Windows 전용이라 서버 배포 환경과 맞지 않음

**주의:** 한국투자증권 계좌 개설 필수
- 비대면(앱) 개설은 온라인 전용 은행 계좌 필요
- 온라인 전용 계좌가 없는 경우 지점 방문 개설 필요
- 계좌 개설 전까지 KIS API 연동은 스킵하고 나머지 Phase 먼저 진행

---

## 1. 가입 및 앱 등록

1. `apiportal.koreainvestment.com` 접속
2. 회원가입 (**한국투자증권 계좌 필수** — 비대면으로 앱에서 바로 개설 가능)
3. 로그인 후 `마이페이지` → `앱 관리` → `앱 등록`
4. 앱 이름 입력 (예: pong-to-rich)
5. 서비스 유형: **모의투자** 먼저 선택 → 실제 돈 안 씀

---

## 2. 앱키 발급

등록 후 아래 두 값 발급됨:
- `AppKey`
- `AppSecret`

이 두 값으로 **Access Token** 을 발급받아서 모든 API 호출에 사용한다.

---

## 3. 환경변수 등록

`.env` 에 추가:
```
KIS_APP_KEY=발급받은키
KIS_APP_SECRET=발급받은시크릿
```

`.env.example` 에도 키 이름만 추가:
```
KIS_APP_KEY=
KIS_APP_SECRET=
```

---

## 4. Access Token 발급 흐름

```
AppKey + AppSecret
       ↓
POST /oauth2/tokenP
       ↓
Access Token (유효기간 1일)
       ↓
모든 API 요청 Header에 포함
Authorization: Bearer {access_token}
```

---

## 5. 모의투자 vs 실전투자

| 구분 | Base URL | 비고 |
|------|----------|------|
| 모의투자 | `https://openapivts.koreainvestment.com:29443` | 개발/테스트용 |
| 실전투자 | `https://openapi.koreainvestment.com:9443` | 실제 돈 사용 |

개발 단계에서는 반드시 모의투자 URL 사용.
