# 이론 정리

각 Phase별 학습 내용을 정리하는 공간

---

## 구조

```
notes/
├── general/                    # 프로젝트 무관 참고 지식 (Phase에 없는 개념)
├── phase-1-network/            # 네트워크 & HTTP 기초 & IO 모델
├── phase-2-os/                 # OS 기초 (프로세스/스레드/메모리)
├── phase-3-linux/              # Linux & Shell
├── phase-4-env-gradle/         # 환경 설정 & Gradle
├── phase-5-git/                # Git & 협업 워크플로우
├── phase-6-java-jvm/           # Java & JVM / 자료구조 / 동시성
├── phase-7-spring-core/        # Spring 핵심 이론 & 구조
├── phase-8-rest-api/           # RESTful API & 문서화
├── phase-9-exception-logging/  # 예외 처리 & 로깅 전략
├── phase-10-tdd/               # TDD
├── phase-11-db-jpa/            # DB / JPA / 인덱스 / 트랜잭션
├── phase-12-aop/               # AOP
├── phase-13-custom-annotation/ # 커스텀 어노테이션 & 멀티모듈
├── phase-14-security-jwt/      # Spring Security / JWT / OAuth2
├── phase-15-notification/      # 알림 서비스
├── phase-16-redis/             # Redis 심화
├── phase-17-design-pattern/    # 디자인 패턴
├── phase-18-clean-code/        # 클린 코드 & 성능 최적화
├── phase-19-docker/            # Docker & 컨테이너
├── phase-20-k8s/               # 컨테이너 오케스트레이션 (Swarm / k3s / k8s)
├── phase-21-ai/                # AI 연동 (Spring AI / RAG / Agent / MCP)
├── phase-22-nginx/             # NGINX & SSL
├── phase-23-msa/               # MSA & Spring Cloud & Saga
├── phase-24-kafka-es/          # Kafka & Elasticsearch
├── phase-25-monitoring/        # 모니터링 & Observability
├── phase-26-cicd/              # CI/CD 파이프라인
├── phase-27-aws/               # AWS 배포 & 인프라
├── phase-28-performance/       # 성능 테스트 & 분석
├── phase-29-disaster/          # 장애 대응 & 트러블슈팅
└── phase-30-payment/           # 결제 시스템
```

## 작성 방식

- 각 폴더 안에 주제별 `.md` 파일로 정리
- 이론 → 구현 → 성능 비교 → 깨달은 점 순서로 작성
- 이론 정리 커밋과 구현 커밋은 분리 (`study:` vs `feat:`)
