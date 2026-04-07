# Backend Dev Roadmap

체계적으로 다시 쌓는 백엔드 학습 레포

---

## 프로젝트 소개

이 레포는 단순 예제 코드가 아닌, **하나의 실제 서비스를 처음부터 끝까지 만들면서** 백엔드 + DevOps 전 영역을 체감하는 학습 레포다.

메인 프로젝트는 **pong-to-rich** — 국내 + 미국 주식 자동매매 플랫폼이다.

- 한국투자증권 KIS API 연동 (모의투자 → 실전매매)
- AI 기반 뉴스 감성 분석 + 매매 전략 추천
- 전략 공유 커뮤니티 + 실시간 시세 스트리밍
- 아키텍처 진화: 모놀리식 → 멀티모듈 → MSA → k8s

ROADMAP의 모든 Phase를 이 프로젝트에 순차적으로 적용한다.
처음부터 최적화하지 않고, 느려지는 지점을 직접 체감하면서 단계적으로 개선한다.

> 자세한 기획 내용: [pong-to-rich/PROJECT.md](./pong-to-rich/PROJECT.md)

---

## 목표

- 개발 경험은 있지만 원리를 모르고 썼던 것들을 제대로 정리
- Spring Boot 기반 서비스를 처음부터 끝까지 — 코드부터 인프라, 운영까지

---

## 진행 방식

```
이론 정리 → 구현 실습 → 성능 비교 / 검증
```

- 너무 깊어진다 싶으면 넘어가고 나중에 다시 돌아옴
- 각 주제는 브랜치 단위로 관리
- 커밋 컨벤션은 [COMMIT_CONVENTION.md](./COMMIT_CONVENTION.md) 참고

---

## 로드맵

전체 학습 계획은 **[ROADMAP.md](./ROADMAP.md)** 에서 확인

| Phase | 주제 |
|-------|------|
| 1 | 네트워크 & 웹 기초 |
| 2 | OS 기초 (프로세스 / 스레드 / 메모리) |
| 3 | Linux & Shell |
| 4 | 환경 설정 & Gradle |
| 5 | Git & 협업 워크플로우 |
| 6 | Java & JVM / 자료구조 / 동시성 |
| 7 | Spring 핵심 이론 & 구조 |
| 8 | RESTful API & 문서화 |
| 9 | 예외 처리 & 로깅 전략 |
| 10 | TDD |
| 11 | 데이터베이스 & JPA |
| 12 | AOP |
| 13 | 커스텀 어노테이션 & 멀티모듈 |
| 14 | Spring Security / JWT / OAuth2 |
| 15 | 알림 서비스 |
| 16 | Redis 심화 |
| 17 | 디자인 패턴 |
| 18 | 클린 코드 & 성능 최적화 |
| 19 | Docker |
| 20 | 컨테이너 오케스트레이션 (Swarm / k3s / k8s) |
| 21 | AI 연동 (Spring AI / RAG / Agent / MCP) |
| 22 | NGINX & SSL |
| 23 | MSA & Spring Cloud & Saga |
| 24 | Kafka & Elasticsearch |
| 25 | 모니터링 & Observability |
| 26 | CI/CD 파이프라인 |
| 27 | AWS 배포 & 인프라 |
| 28 | 성능 테스트 & 분석 |
| 29 | 장애 대응 & 트러블슈팅 |
| 30 | 결제 시스템 |

---

## AI 활용 방식

AI를 적극적으로 활용해서 개발하고 있습니다. 다만 AI가 작성한 코드를 그대로 가져다 쓰는 방식은 아닙니다.

구현할 때마다 사용된 개념을 직접 정리하고, 에러가 발생하면 로그를 보고 스스로 원인을 분석한 뒤 해결 과정을 기록합니다. 커밋도, 터미널 명령어도 전부 직접 실행합니다.

AI는 개발 속도를 높이는 도구로 활용하되, 코드와 개념에 대한 이해는 온전히 제 것으로 만드는 것을 원칙으로 하고 있습니다. 학습한 내용은 velog에 제 언어로 다시 풀어서 정리합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.x |
| DB | MySQL, Redis |
| Build | Gradle |
| Container | Docker, Docker Compose, k3s |
| Infra | AWS (EC2, RDS, ECS, S3) |
| Monitoring | Prometheus, Grafana, ELK |
| CI/CD | GitHub Actions, Jenkins |
