# Git 자주 쓰는 명령어 정리

---

## 상태 확인

```bash
git status                    # 현재 변경사항 전체 확인
git log --oneline             # 커밋 히스토리 한 줄 요약
git log --oneline --graph     # 브랜치 포함 시각화
```

---

## 변경 내용 확인 (diff)

```bash
git diff                      # unstaged 변경사항 확인 (워킹트리 vs 스테이징)
git diff --cached             # staged 변경사항 확인 (스테이징 vs 마지막 커밋)
git diff HEAD                 # staged + unstaged 전체 확인
git diff main feature/xxx     # 브랜치 간 차이 비교
```

---

## 스테이징 (add)

```bash
git add 파일명                 # 특정 파일만 스테이징
git add 폴더/                  # 폴더 전체 스테이징
git add -p                    # 변경사항을 chunk 단위로 골라서 스테이징 (추천)
```

> `git add .` 은 실수로 민감한 파일까지 올릴 수 있으니 가급적 파일/폴더를 명시하는 게 좋다

---

## 스테이징 취소 (unstage)

```bash
git restore --staged 파일명    # 특정 파일 unstage (add 취소)
git restore --staged .        # 전체 unstage
```

---

## 변경사항 되돌리기

```bash
git restore 파일명             # 워킹트리 변경사항 되돌리기 (저장 안 된 수정 취소) ⚠️ 복구 불가
git restore .                 # 전체 워킹트리 되돌리기 ⚠️ 복구 불가
```

---

## 커밋

```bash
git commit -m "type: 메시지"   # 커밋
git commit --amend            # 직전 커밋 메시지 수정 (push 전에만)
```

---

## 커밋 되돌리기

```bash
git revert HEAD               # 직전 커밋을 되돌리는 새 커밋 생성 (히스토리 유지, 안전)
git reset --soft HEAD~1       # 직전 커밋 취소, 변경사항은 staged 상태로 유지
git reset --mixed HEAD~1      # 직전 커밋 취소, 변경사항은 unstaged 상태로 유지 (기본값)
git reset --hard HEAD~1       # 직전 커밋 취소, 변경사항 완전 삭제 ⚠️ 복구 불가
```

> - push 전: `reset` 사용 가능
> - push 후: 반드시 `revert` 사용 (히스토리 덮어쓰기 금지)

---

## 브랜치

```bash
git branch                    # 로컬 브랜치 목록
git branch -a                 # 원격 포함 전체 브랜치 목록
git branch 브랜치명             # 브랜치 생성
git switch 브랜치명             # 브랜치 이동
git switch -c 브랜치명          # 브랜치 생성 + 이동
git branch -d 브랜치명          # 브랜치 삭제 (머지된 것만)
git branch -D 브랜치명          # 브랜치 강제 삭제
```

---

## 원격 저장소

```bash
git push origin 브랜치명        # 원격에 push
git push -u origin 브랜치명     # 최초 push + upstream 설정 (이후엔 git push만 해도 됨)
git pull                       # fetch + merge
git fetch                      # 원격 변경사항 가져오기만 (merge 안 함)
```

---

## 머지 & 리베이스

```bash
git merge 브랜치명              # 현재 브랜치에 대상 브랜치 머지
git rebase main               # 현재 브랜치를 main 기준으로 재정렬 (히스토리 깔끔)
```

> - `merge`: 히스토리 그대로 보존, 협업 브랜치에 적합
> - `rebase`: 히스토리 선형화, 로컬 작업 정리에 적합. push 후엔 사용 금지

---

## stash (임시 저장)

```bash
git stash                     # 현재 변경사항 임시 저장
git stash pop                 # 가장 최근 stash 복원 + 목록에서 제거
git stash list                # stash 목록 확인
git stash drop                # 가장 최근 stash 삭제
```

> 브랜치 전환 전에 작업 중인 내용을 잠깐 치워둘 때 유용

---

## 파일 추적 제거 (.gitignore 적용)

```bash
git rm --cached 파일명         # 원격에서 파일 제거, 로컬엔 유지 (.gitignore 뒤늦게 적용할 때)
git rm --cached -r 폴더/       # 폴더 전체
```

---

## 자주 쓰는 조합

```bash
# 브랜치 따서 작업 시작
git switch -c feat/기능명

# 변경사항 확인하고 커밋
git status
git diff
git add 파일명
git diff --cached
git commit -m "feat: 기능 추가"

# 작업 완료 후 main에 머지
git switch main
git merge feat/기능명
git branch -d feat/기능명
```
