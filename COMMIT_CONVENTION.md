# Commit Convention

[Conventional Commits](https://www.conventionalcommits.org/) 기반 커밋 메시지 규칙

---

## 형식

```
<type>: <subject>

[body]
```

- **subject**: 50자 이내, 마침표 없이, 명령형으로
- **body**: 선택사항. 변경 이유 / 이전과 달라진 점 설명 (72자 줄바꿈)

---

## Type 종류

| Type | 설명 | 예시 |
|------|------|------|
| `init` | 프로젝트 초기 세팅 | `init: 프로젝트 초기 세팅` |
| `feat` | 새로운 기능 추가 | `feat: JWT 발급 기능 추가` |
| `fix` | 버그 수정 | `fix: 토큰 만료 시 NPE 수정` |
| `refactor` | 기능 변경 없는 코드 개선 | `refactor: UserService 메서드 분리` |
| `test` | 테스트 코드 추가/수정 | `test: UserService 단위 테스트 추가` |
| `docs` | 문서 추가/수정 | `docs: API 명세 업데이트` |
| `chore` | 빌드, 설정, 패키지 변경 | `chore: Gradle 의존성 업데이트` |
| `style` | 포맷, 세미콜론 등 코드 스타일 | `style: 코드 포맷 정리` |
| `perf` | 성능 개선 | `perf: N+1 쿼리 fetch join으로 개선` |
| `ci` | CI/CD 설정 변경 | `ci: GitHub Actions 배포 파이프라인 추가` |
| `study` | 학습 정리, 실습 코드 | `study: 커넥션 풀 성능 비교 실습` |

---

## 예시

```
feat: 회원가입 API 구현

- 이메일 중복 검증 추가
- 비밀번호 BCrypt 암호화 적용
```

```
fix: 로그인 시 Redis 연결 실패 오류 수정
```

```
study: HikariCP 풀 사이즈별 성능 비교 실습 완료
```

---

## 브랜치 전략

| 브랜치 | 용도 | 예시 |
|--------|------|------|
| `main` | 배포 가능한 안정 버전 | — |
| `develop` | 개발 통합 브랜치 | — |
| `feat/기능명` | 기능 개발 | `feat/user-login` |
| `fix/버그명` | 버그 수정 | `fix/token-expired-npe` |
| `refactor/내용` | 리팩토링 | `refactor/user-service` |
| `chore/내용` | 설정, 의존성 변경 | `chore/gradle-update` |

- `feat`, `fix`, `refactor` 브랜치는 `develop` 에서 분기 후 `develop` 으로 머지
- `develop` → `main` 머지는 배포 가능한 상태일 때만
- 이론 정리(`study`) 커밋은 브랜치 없이 `main` 에 직접 커밋

## PR & 머지 플로우

```
1. develop 에서 기능 브랜치 생성
   git switch -c feat/기능명

2. 작업 후 커밋
   git add 파일명
   git commit -m "feat: 기능 설명"

3. 원격에 push
   git push origin feat/기능명

4. GitHub에서 PR 생성 (base: develop ← compare: feat/기능명)

5. 코드 리뷰 후 머지 (Squash and merge 또는 Merge commit)

6. 머지 후 기능 브랜치 삭제
   git branch -d feat/기능명
```

### PR 작성 원칙
- 제목: 커밋 컨벤션과 동일 (`feat: 기능 설명`)
- 본문: 변경 이유 / 주요 변경사항 / 테스트 방법
- 가능한 작은 단위로 PR 작성 (리뷰하기 쉽게)
- 관련 이슈 자동 close: 본문 마지막에 `Closes #번호` 명시 (복수: `Closes #20, Closes #37`)
  - `Closes`, `Fixes`, `Resolves` 키워드 모두 동작
  - PR이 머지될 때 자동으로 이슈 닫힘

---

## 규칙

- 커밋은 **하나의 논리적 단위**로 쪼개기
- 하나의 커밋에 여러 목적 섞지 않기
- 이론 정리와 구현 코드는 **별도 커밋**으로 분리

---

## GitHub 이슈 네이밍 규칙

### 형식

```
[type] 한 줄 설명 (무엇을 하는지 명확하게)
```

### type 종류

| type | 설명 | 예시 |
|------|------|------|
| `feat` | 새로운 기능 추가 | `[feat] JWT 로그인 API 구현` |
| `fix` | 버그 수정 | `[fix] 탈퇴 유저 로그인 가능 버그 수정` |
| `refactor` | 기능 변경 없는 코드 개선 | `[refactor] AuthService 메서드 분리` |
| `perf` | 성능 개선 | `[perf] stock_prices 인덱스 추가` |
| `security` | 보안 관련 작업 | `[security] appkey/appsecret AES 암호화` |
| `chore` | 빌드, 설정, 의존성 변경 | `[chore] Gradle 의존성 정리` |
| `docs` | 문서, README 등 | `[docs] API 명세 업데이트` |
| `test` | 테스트 코드 추가/수정 | `[test] KisAuthService 단위 테스트 추가` |
| `infra` | 서버, Docker, CI/CD 관련 | `[infra] GitHub Actions 배포 파이프라인 추가` |

### 이슈 본문 구성

```
## 배경
왜 이 작업이 필요한가. 현재 상태와 문제점.

## 구현 내용
- 구현할 항목을 체크리스트로 작성
- [ ] 항목 1
- [ ] 항목 2

## 참고
관련 이슈, 파일, 외부 문서 링크 등 (선택)
```

### 규칙

- 제목은 **동사 원형**으로 시작 (구현, 추가, 수정, 연동 등)
- 제목만 봐도 무슨 작업인지 알 수 있어야 함
- 관련 이슈는 본문에 `#번호`로 명시
- 라벨은 type과 동일하게 설정
