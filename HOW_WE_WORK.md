# 진행 방식 가이드

이 레포를 처음 보는 AI 또는 사람이 읽고 동일한 방식으로 진행할 수 있도록 작성된 문서

---

## 디렉토리 구조

```
spring_study/
├── HOW_WE_WORK.md          ← 지금 이 파일. 진행 방식 설명
├── README.md               ← 레포 소개 및 로드맵 요약
├── ROADMAP.md              ← 전체 학습 로드맵 (Phase 0-A ~ Phase 22)
├── COMMIT_CONVENTION.md    ← 커밋 메시지 규칙
├── .gitignore
│
├── devlog/                 ← 날짜별 학습 일지
│   └── day01-2026-04-07.md
│
├── notes/                  ← Phase별 이론 정리
│   ├── README.md
│   ├── phase-0A-network/
│   ├── phase-0B-env-gradle/
│   ├── phase-0C-git/
│   ├── phase-0D-linux/
│   ├── phase-0-java-jvm/
│   ├── phase-1-spring-core/
│   └── ... (ROADMAP.md의 Phase 순서와 동일)
│
├── hello-spring/           ← 간단한 테스트 & 실습용 (버려도 되는 코드)
│
└── pong-to-rich/           ← 메인 Spring 프로젝트
    ├── PROJECT.md          ← 프로젝트 기획 전체 문서
    └── (Spring Boot 프로젝트)  ← 로드맵의 모든 Phase를 여기에 적용
```

---

## 프로젝트 방향

- **hello-spring**: 개념 확인용 간단한 테스트 코드. 가볍게 쓰고 버려도 됨
- **pong-to-rich**: 실제 서비스 수준으로 키워나가는 메인 프로젝트. ROADMAP.md의 모든 Phase를 이 프로젝트에 순차적으로 적용
  - 기획 전체 내용: [pong-to-rich/PROJECT.md](./pong-to-rich/PROJECT.md)
  - 아키텍처 진화: 모놀리식 → 멀티모듈 → MSA

---

## 학습 사이클

각 Phase는 아래 순서로 진행한다:

```
1. 이론 정리   → notes/phase-N-xxx/*.md 에 작성
2. 구현 실습   → pong-to-rich/ 에 실제 코드로 적용
3. 성능 비교   → 적용 전/후 차이를 측정하고 기록
4. 깨달은 점   → devlog/dayNN-날짜.md 에 정리
```

> **중요**: 위 단계 중 무엇을 하든 그날의 devlog에 반드시 기록한다.
> 이론만 정리해도, 코드 한 줄만 써도, 막혔어도 — 모두 devlog에 남긴다.
> devlog는 작업 시작할 때 열고, 작업하면서 실시간으로 채워나간다.
>
> **이론 정리 페이스 규칙:**
> - 하루에 이론 정리는 3~4개로 제한
> - 구현 중 새로 나온 개념은 notes에 바로 정리하지 않고 devlog에 `정리 필요: 개념명` 으로 메모만 해둠
> - 나중에 사용자가 요청하면 그때 하나씩 notes에 정리

---

## devlog 작성 규칙

- 파일명: `dayNN-YYYY-MM-DD.md` (예: `day01-2026-04-07.md`)
- 내용 구성:

```markdown
# Day N — YYYY-MM-DD

## 오늘 한 것
- ...

## 이론 정리 링크
- [주제명](../notes/phase-N-xxx/파일명.md)

## 구현 내용
- ...

## 성능 비교
- ...

## 깨달은 점 / 막혔던 것
- 에러 원문 (로그 한 줄 그대로):
  ```
  에러 메시지 원문
  ```
  → 어느 부분을 보고 어떻게 판단했는지 → 어떻게 해결했는지
```

> 작업 시작할 때 파일을 열고, 진행하면서 계속 추가한다. 하루에 여러 작업을 해도 같은 파일에 이어서 작성한다.
> 에러가 발생하면 에러 로그 원문을 그대로 붙여넣고, 어느 부분을 보고 어떤 판단을 했는지, 어떻게 해결했는지 반드시 기록한다.
> 스크린샷 첨부 시 이미지 파일을 git에 직접 올리지 않는다. GitHub Issues 본문에 이미지를 드래그 앤 드롭하면 자동 링크가 생성되고(`https://github.com/user-attachments/assets/...`) 그 링크를 md에 붙여넣는다. Issue를 저장하지 않아도 링크는 영구 유효하다.
> 사용자가 결과 스크린샷을 올리면 반드시 해당 날짜 devlog에 마크다운 이미지 문법으로 추가한다.
> ```markdown
> ![설명](https://github.com/user-attachments/assets/...)
> ```

---

## notes 작성 규칙

- 각 Phase 폴더 안에 주제별 `.md` 파일로 작성
- 파일명은 영어 소문자, 하이픈 구분 (예: `ioc-di.md`, `http-basics.md`)
- 이론 정리 후 해당 날짜의 devlog에서 링크로 연결

### 정리 필요 목록 (notes/pending.md)

- 구현하다가 나온 개념인데 아직 정리 못 한 것들은 `notes/pending.md` 에 추가
- 발생한 날짜(dayNN)를 함께 기록
- 사용자가 요청하면 해당 개념을 해당 Phase notes 파일에 정리하고 pending.md에서 ✅ 체크

### pending 개념 학습 플로우

```
구현 중 새 개념 등장
       ↓
pending.md 에 메모
       ↓
PR 리뷰에서 해당 코드 라인에 코멘트로 간단히 설명 (예습)
       ↓
나중에 사용자가 요청하면 notes에 정식 정리 (복습)
```

- PR 머지 전 pending 개념이 코드에 등장하면 AI가 리뷰 코멘트 내용을 작성해준다
- 사용자가 GitHub PR에 직접 붙여넣기 해서 리뷰 연습

---

## ROADMAP 체크리스트 업데이트 규칙

이론 정리 파일을 작성하면 반드시 아래 순서로 ROADMAP.md를 업데이트한다:

1. 해당 항목 `- [ ]` → `- [x]` 로 체크
2. 항목 끝에 `→ [정리](./notes/phase-N-xxx/파일명.md)` 링크 추가

**예시:**
```markdown
- [x] **되돌리기 명령어 차이 완전 정리** → [정리](./notes/phase-0C-git/git-basics.md)
```

- 정리 파일 하나가 여러 항목을 커버할 수 있음 → 각 항목마다 개별 링크
- 실습 항목(`실습:`)은 실제 코드를 작성한 후에 체크
- 체크 후 커밋은 이론 정리 커밋과 함께 묶어서 진행

---

## 커밋 규칙

커밋은 목적별로 분리한다. 자세한 내용은 [COMMIT_CONVENTION.md](./COMMIT_CONVENTION.md) 참고

| 상황 | 타입 | 예시 |
|------|------|------|
| 이론 정리 | `study` | `study: Spring IoC/DI 이론 정리` |
| 구현 코드 | `feat` | `feat: 빈 수동 등록 실습 코드 추가` |
| devlog 작성 | `docs` | `docs: day01 devlog 작성` |
| 프로젝트 기획/구조 | `init` | `init: pong-to-rich Spring 프로젝트 초기 세팅` |

---

## 브랜치별 커밋 규칙

- **구현 코드** (`feat/`, `fix/`, `refactor/` 브랜치) → 해당 브랜치에서 커밋 후 PR
- **이론 정리** (notes/), **devlog**, **HOW_WE_WORK.md**, **ROADMAP.md** → 브랜치 상관없이 `main` 에 직접 커밋

---

## AI가 이 레포에서 작업할 때 따라야 할 규칙

1. **작업 시작 전** — 해당 날짜의 devlog 파일이 없으면 먼저 생성한다
2. **이론 정리 작성 시** — `notes/phase-N-xxx/파일명.md` 에 작성하고, ROADMAP.md 해당 항목 체크 + 링크 추가
3. **코드 작성 시** — `pong-to-rich/` 안에서 작업. hello-spring은 간단한 테스트 용도만
4. **작업할 때마다** — devlog에 실시간으로 기록 (이론 정리, 구현, 막힌 것 모두)
5. **커밋 명령어는 직접 실행하지 않는다** — 커밋할 내용과 명령어를 알려주고 사용자가 직접 실행
6. **git 명령어도 직접 실행하지 않는다** — 명령어를 알려주고 사용자가 직접 실행

---

## 현재 진행 상황

전체 진행 현황은 [ROADMAP.md](./ROADMAP.md) 의 체크리스트로 관리
