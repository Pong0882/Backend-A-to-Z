# pong-to-rich 🏓📈

> 국내 + 미국 주식 자동매매 플랫폼
> 모의투자부터 실전매매까지, 전략 공유 커뮤니티 포함
> 모놀리식으로 시작 → 점진적으로 MSA 전환

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [아키텍처 진화 전략](#3-아키텍처-진화-전략)
4. [DB 전략 & 성능 개선 시나리오](#4-db-전략--성능-개선-시나리오)
5. [핵심 기능](#5-핵심-기능)
6. [도메인 & ERD](#6-도메인--erd)
7. [API 설계](#7-api-설계)
8. [디렉토리 구조](#8-디렉토리-구조)
9. [개발 단계별 계획](#9-개발-단계별-계획)

---

## 1. 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | pong-to-rich |
| 목적 | 백엔드 + DevOps 전 영역 학습을 하나의 실제 서비스로 체감 |
| 대상 | 국내 / 미국 주식 자동매매 + 커뮤니티 플랫폼 |
| 시작 형태 | 모놀리식 |
| 최종 형태 | MSA (Spring Cloud 기반) |
| 증권사 API | 한국투자증권 KIS Developers (모의 + 실전) |

---

## 2. 기술 스택

### 선택한 이유와 나중에 해볼 것들

| 분류 | 선택 | 이유 | 나중에 해볼 것 |
|------|------|------|---------------|
| Language | Java 17 | LTS, Record/Sealed Class 활용 | Java 21 Virtual Thread 마이그레이션 |
| Framework | Spring Boot 4.x | 최신 LTS, Auto Configuration | WebFlux 전환 (Reactive) |
| DB | MySQL (단독 시작) | 느린 지점 직접 체감 후 개선 | 인덱스 → 파티셔닝 → ES → 샤딩 단계별 개선 |
| Cache | Redis | 세션, 캐시, 분산 락, Pub/Sub | Redis Cluster 전환 |
| Message Queue | Kafka | 주문 큐잉, 이벤트 기반 아키텍처 | Kafka Streams → Spark 파이프라인 |
| 검색 | MySQL LIKE (시작) | 느린 검색 체감 후 ES로 교체 | Elasticsearch + nori analyzer |
| 빌드 | Gradle | 멀티 모듈 지원 | Gradle 캐시 최적화 |
| Container | Docker | 로컬 환경 통일 | Docker Swarm → k3s → k8s(EKS) |
| Infra | AWS | 실제 배포 환경 | EC2 → ECS(Fargate) → EKS |
| Monitoring | Prometheus + Grafana | 메트릭 수집 및 시각화 | ELK 로그 파이프라인, Jaeger 분산 추적 |
| CI/CD | GitHub Actions | PR 자동 테스트 / 배포 | Jenkins 추가, ArgoCD GitOps |
| 실시간 | WebSocket + STOMP | 시세 스트리밍, 채팅 | SSE 병행 (단방향 알림) |

---

## 3. 아키텍처 진화 전략

### STEP 1 — 모놀리식 (시작)
```
클라이언트 → NGINX → Spring Boot (단일 앱) → MySQL + Redis
```
- 전체 기능을 단일 애플리케이션으로 구현
- 레이어드 아키텍처 (Controller → Service → Repository)
- 이 단계에서 적용할 것들
  - RESTful API 설계
  - Spring Security + JWT
  - JPA + QueryDSL
  - AOP (로깅, 권한 체크, 실행 시간 측정)
  - 커스텀 어노테이션 (`@CurrentUser`, `@RateLimit`, `@Loggable`)
  - 예외 처리 + 로깅 전략
  - 캐싱 전략 (Redis)
  - 동시성 제어 (낙관적 락 / 비관적 락 / 분산 락)
  - 트랜잭션 전파 / 격리 수준
  - Filter / Interceptor / ArgumentResolver

### STEP 2 — 모놀리식 심화
- 멀티 모듈 구조로 전환 (`api` / `domain` / `infra` / `common`)
- Spring Batch로 수익률 집계 배치 구현
- 성능 테스트 + 병목 분석 (k6)
- DB 성능 개선 시나리오 진행

### STEP 3 — MSA 전환
```
클라이언트
    → API Gateway (Spring Cloud Gateway)
        → 회원 서비스
        → 주문 서비스
        → 시세 서비스
        → 전략 서비스
        → 커뮤니티 서비스
        → 알림 서비스
```
- Eureka 서비스 디스커버리
- Config Server 설정 중앙화
- Feign Client 서비스 간 통신
- Kafka 이벤트 기반 비동기 통신
- Saga 패턴 + 보상 트랜잭션 (주문 → 체결 → 실패 → 롤백)
- Circuit Breaker (Resilience4j)
- `@CurrentUser` — Gateway JWT 검증 → `X-User-Id` 헤더 전파

### STEP 4 — 인프라 확장
- Docker Swarm → k3s → EKS
- HPA 수평 자동 확장
- Blue-Green / Canary 배포
- AWS WAF, CloudFront, Route53
- 성능 테스트: 단일 인스턴스 vs 수평 확장 비교

---

## 4. DB 전략 & 성능 개선 시나리오

### 선택: MySQL 단독으로 시작한 이유
- 처음부터 최적화된 환경에서 시작하면 성능 문제를 체감할 수 없음
- 느려지는 지점을 직접 경험하고 단계적으로 개선하는 것이 학습 목적에 맞음

### 성능 개선 단계

```
PHASE 1 — MySQL 단독 (인덱스 없음)
  └─ 종목 검색, 시세 히스토리 조회 느림 체감

PHASE 2 — 인덱스 추가
  └─ 단일 인덱스 → 복합 인덱스 → Covering Index
  └─ EXPLAIN ANALYZE로 실행 계획 비교

PHASE 3 — 파티셔닝
  └─ 시세 히스토리 테이블 날짜 기반 파티셔닝
  └─ 파티션 전후 성능 비교

PHASE 4 — Read Replica
  └─ MySQL Replication 구성
  └─ 읽기 트래픽 분산

PHASE 5 — Redis 캐싱
  └─ 실시간 시세, 인기 종목 캐싱
  └─ 캐시 히트율 및 응답시간 비교

PHASE 6 — Elasticsearch 도입
  └─ 종목 검색 MySQL LIKE → ES 전환
  └─ 검색 성능 비교 (대용량 데이터)

PHASE 7 — 샤딩
  └─ 시세 히스토리 수평 샤딩
  └─ 샤딩 전후 성능 비교
```

---

## 5. 핵심 기능

### 회원
- 회원가입 / 로그인 (JWT + Refresh Token)
- 소셜 로그인 (Google / Kakao / Naver)
- 증권사 API 키 등록 (모의 / 실전 분리, 암호화 저장)
- 프로필 관리

### 시세
- 국내 / 미국 실시간 시세 (KIS WebSocket)
- 종목 검색 (MySQL → ES 단계적 전환)
- 시세 히스토리 조회 (일/주/월/년)
- 관심 종목 등록 / 알림

### 자동매매
- 매매 전략 등록 (조건 설정: 이동평균, RSI, 목표가, 손절가)
- 전략 실행 / 중지 / 수정
- 주문 체결 / 실패 처리
- 체결 알림 (WebSocket / FCM / 이메일)
- 주문 내역 조회

### 포트폴리오
- 보유 종목 현황
- 수익률 계산 (실시간 / 기간별)
- 수익률 통계 (Spring Batch 집계)

### 커뮤니티
- 전략 공유 게시판
- 댓글 / 좋아요
- 인기 전략 랭킹 (Redis ZSet)
- 실시간 채팅 (WebSocket + STOMP)

### 결제 / 충전
- 포인트 충전 (토스페이먼츠)
- 결제 취소 / 환불
- 충전 내역 조회

### 알림
- 실시간 알림 (체결 / 목표가 도달 / 손절)
- 이메일 알림
- FCM 푸시 알림

---

## 6. 도메인 & ERD

> 2026-04-16 설계 확정. 1단계(핵심 도메인)만 먼저 구현하고 커뮤니티/결제/알림/AI는 단계별 추가.

### 도메인 목록

| 도메인 | 테이블 | 설명 | 구현 단계 |
|--------|--------|------|----------|
| 회원 | `users` | 회원 기본 정보 (닉네임 랜덤 생성, Soft Delete) | 1단계 |
| 회원 | `oauth_accounts` | 소셜 로그인 연동 (카카오/구글/네이버) | 1단계 |
| 회원 | `broker_accounts` | 증권사 API 키 + 예수금 (broker/account_type 분리) | 1단계 |
| 종목 | `stocks` | 국내/미국 종목 (market 컬럼으로 시장 구분) | 1단계 |
| 종목 | `stock_prices` | 일봉 시세 히스토리 — 가격 DECIMAL(12,4) | 1단계 |
| 종목 | `stock_candles` | 분봉/시간봉 시세 (15M/1H/4H) — 실시간 대량 적재 | 1단계 |
| 종목 | `watchlists` | 관심 종목 + 가격 알림 | 1단계 |
| 전략 | `strategies` | 매매 전략 — 스케줄러 기반, 수량 단위 | 1단계 |
| 전략 | `strategy_conditions` | 전략 조건 — indicator + params JSON 혼합 | 1단계 |
| 주문 | `orders` | 주문 내역 — 시장가/지정가 둘 다 지원 | 1단계 |
| 주문 | `order_executions` | 체결 내역 (1주문 N체결) | 1단계 |
| 포트폴리오 | `portfolios` | 포트폴리오 (유저당 1개) | 1단계 |
| 포트폴리오 | `holdings` | 보유 종목 + 평균매수가 + 숨김 기능 | 1단계 |
| 포트폴리오 | `profit_summaries` | 수익률 집계 (Spring Batch) | 나중에 |
| 커뮤니티 | `posts` | 게시글 | 나중에 |
| 커뮤니티 | `comments` | 댓글 | 나중에 |
| 커뮤니티 | `likes` | 좋아요 | 나중에 |
| 결제 | `payments` | 결제 내역 | 나중에 |
| 결제 | `point_histories` | 포인트 충전/사용 내역 | 나중에 |
| 알림 | `notifications` | 알림 내역 | 나중에 |
| AI/ML | `ai_predictions` | AI 예측 결과 (종목별 상승/하락 확률) | STEP 6 |
| AI/ML | `stock_reports` | 종목별 AI 리포트 (뉴스 + 지표 종합) | STEP 6 |
| AI/ML | `backtests` | 백테스팅 결과 (전략별 과거 성과) | STEP 6 |

> Redis 저장: KIS access_token → `kis:token:{userId}` (TTL 자동 만료)
> MongoDB 저장: AI 대화 히스토리 (`chat_sessions`) → STEP 6
> ES 저장: 뉴스 스크랩 + 감정 점수 (`news` 인덱스) → STEP 6

### ERD 다이어그램

![pong-to-rich ERD](https://github.com/user-attachments/assets/3983e3d5-0471-474a-9a08-7b0f93f5a5a3)

### 확정 스키마

```sql
-- users
id, email, password(nullable), nickname(unique), profile_image,
point_balance, role, login_type, is_active, deleted_at, created_at, updated_at

-- oauth_accounts
id, user_id, provider(KAKAO/GOOGLE/NAVER), provider_id, created_at
UNIQUE (provider, provider_id)

-- broker_accounts
id, user_id, broker(KIS/KIWOOM/SAMSUNG), account_type(MOCK/REAL),
appkey, appsecret, balance, balance_synced_at, is_active, created_at, updated_at
UNIQUE (user_id, broker, account_type)

-- stocks
id, code, name, market(KRX/NASDAQ/NYSE)
UNIQUE (code, market)

-- stock_prices (일봉)
id, stock_id, trade_date(DATE), open_price, high_price, low_price, close_price(DECIMAL 12,4), volume
UNIQUE (stock_id, trade_date)

-- stock_candles (분봉/시간봉)
id, stock_id, interval(15M/1H/4H), trade_time(DATETIME), open_price, high_price, low_price, close_price(DECIMAL 12,4), volume
UNIQUE (stock_id, interval, trade_time)

-- watchlists
id, user_id, stock_id, alert_price(nullable), created_at
UNIQUE (user_id, stock_id)

-- strategies
id, user_id, broker_account_id, stock_id, name, status(ACTIVE/INACTIVE/PAUSED),
order_quantity, last_checked_at, created_at, updated_at

-- strategy_conditions
id, strategy_id, type(BUY/SELL), indicator(VARCHAR 50), params(JSON), created_at

-- orders
id, user_id, strategy_id(nullable), broker_account_id, stock_id,
order_type(BUY/SELL), price_type(MARKET/LIMIT), status(PENDING/PARTIAL/FILLED/CANCELLED/FAILED),
quantity, price(nullable), filled_quantity, created_at, updated_at

-- order_executions
id, order_id, quantity, price(DECIMAL 12,4), executed_at

-- portfolios
id, user_id(unique), created_at, updated_at

-- holdings
id, portfolio_id, stock_id, quantity, average_price(DECIMAL 12,4), is_hidden, created_at, updated_at
UNIQUE (portfolio_id, stock_id)
```

### 주요 관계

```
users 1 ─── N oauth_accounts
users 1 ─── N broker_accounts
users 1 ─── N strategies
users 1 ─── 1 portfolios
users 1 ─── N orders
users 1 ─── N watchlists

broker_accounts 1 ─── N strategies
broker_accounts 1 ─── N orders

strategies 1 ─── N strategy_conditions
strategies 1 ─── N orders

stocks 1 ─── N stock_prices
stocks 1 ─── N stock_candles
stocks 1 ─── N watchlists
stocks 1 ─── N orders
stocks 1 ─── N holdings

orders 1 ─── N order_executions
portfolios 1 ─── N holdings
```

---

## 7. API 설계

### 인증
```
POST   /api/v1/auth/signup          회원가입
POST   /api/v1/auth/login           로그인
POST   /api/v1/auth/logout          로그아웃
POST   /api/v1/auth/refresh         토큰 재발급
GET    /api/v1/auth/oauth2/{provider} 소셜 로그인
```

### 회원
```
GET    /api/v1/users/me             내 정보 조회
PUT    /api/v1/users/me             내 정보 수정
POST   /api/v1/users/me/broker      증권사 API 키 등록
DELETE /api/v1/users/me/broker/{id} 증권사 API 키 삭제
```

### 종목
```
GET    /api/v1/stocks               종목 검색 (keyword, market)
GET    /api/v1/stocks/{code}        종목 상세
GET    /api/v1/stocks/{code}/prices 시세 히스토리 (period)
POST   /api/v1/watchlists           관심 종목 추가
DELETE /api/v1/watchlists/{id}      관심 종목 삭제
```

### 전략
```
GET    /api/v1/strategies           내 전략 목록
POST   /api/v1/strategies           전략 등록
GET    /api/v1/strategies/{id}      전략 상세
PUT    /api/v1/strategies/{id}      전략 수정
DELETE /api/v1/strategies/{id}      전략 삭제
POST   /api/v1/strategies/{id}/start 전략 실행
POST   /api/v1/strategies/{id}/stop  전략 중지
```

### 주문
```
GET    /api/v1/orders               주문 내역 조회
GET    /api/v1/orders/{id}          주문 상세
POST   /api/v1/orders               수동 주문
DELETE /api/v1/orders/{id}          주문 취소
```

### 포트폴리오
```
GET    /api/v1/portfolios           포트폴리오 목록
GET    /api/v1/portfolios/{id}      포트폴리오 상세
GET    /api/v1/portfolios/{id}/holdings   보유 종목
GET    /api/v1/portfolios/{id}/profits    수익률 통계
```

### 커뮤니티
```
GET    /api/v1/posts                게시글 목록
POST   /api/v1/posts                게시글 작성
GET    /api/v1/posts/{id}           게시글 상세
PUT    /api/v1/posts/{id}           게시글 수정
DELETE /api/v1/posts/{id}           게시글 삭제
POST   /api/v1/posts/{id}/comments  댓글 작성
POST   /api/v1/posts/{id}/likes     좋아요
DELETE /api/v1/posts/{id}/likes     좋아요 취소
```

### 결제
```
POST   /api/v1/payments             결제 승인 요청
POST   /api/v1/payments/{id}/cancel 결제 취소
GET    /api/v1/payments             결제 내역
GET    /api/v1/points               포인트 잔액 / 내역
```

### 알림
```
GET    /api/v1/notifications        알림 목록
PATCH  /api/v1/notifications/{id}/read  읽음 처리
DELETE /api/v1/notifications/{id}   알림 삭제
GET    /api/v1/notifications/stream SSE 실시간 알림 구독
```

---

## 8. 디렉토리 구조

### STEP 1 — 모놀리식 초기 구조

```
pong-to-rich/
├── src/main/java/com/pongtorich/
│   ├── PongToRichApplication.java
│   │
│   ├── global/                         # 전역 공통
│   │   ├── config/                     # Security, Redis, Kafka 설정
│   │   ├── exception/                  # 전역 예외 처리
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ErrorCode.java
│   │   ├── response/                   # 공통 응답 포맷
│   │   │   └── ApiResponse.java
│   │   ├── aop/                        # AOP (로깅, 실행시간)
│   │   ├── annotation/                 # 커스텀 어노테이션
│   │   │   ├── CurrentUser.java
│   │   │   ├── RateLimit.java
│   │   │   └── Loggable.java
│   │   ├── filter/                     # JWT 필터
│   │   └── interceptor/                # 공통 인터셉터
│   │
│   ├── domain/
│   │   ├── user/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   └── dto/
│   │   ├── stock/
│   │   ├── strategy/
│   │   ├── order/
│   │   ├── portfolio/
│   │   ├── community/
│   │   ├── payment/
│   │   └── notification/
│   │
│   └── infra/                          # 외부 연동
│       ├── kis/                        # KIS API 클라이언트
│       ├── redis/
│       ├── kafka/
│       └── fcm/
│
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── application-test.yml
│
└── src/test/
```

### STEP 3 — MSA 전환 후 구조

```
pong-to-rich/
├── gateway-service/
├── eureka-service/
├── config-service/
├── user-service/
├── stock-service/
├── strategy-service/
├── order-service/
├── portfolio-service/
├── community-service/
├── notification-service/
├── payment-service/
└── common/                 # 공통 모듈 (@CurrentUser 등)
```

---

## 9. 개발 단계별 계획

### STEP 1 — 기반 구조 (모놀리식)
- [ ] 프로젝트 초기 세팅 (Spring Boot, Gradle, Docker Compose)
- [ ] 공통 응답 포맷 / 예외 처리 / 로깅 구조 설계
- [ ] DB 설계 및 Flyway 마이그레이션 세팅
- [ ] 회원가입 / 로그인 (JWT + Redis)
- [ ] 소셜 로그인 (OAuth2)
- [ ] KIS API 연동 (모의투자 환경)
- [ ] 종목 조회 / 시세 WebSocket 연동

### STEP 2 — 핵심 기능
- [ ] 매매 전략 등록 / 실행
- [ ] 자동매매 스케줄러 구현
- [ ] 주문 체결 / 실패 처리
- [ ] 포트폴리오 관리
- [ ] 실시간 알림 (SSE / WebSocket)
- [ ] 커뮤니티 게시판

### STEP 3 — 성능 개선
- [ ] 인덱스 설계 및 성능 비교
- [ ] Redis 캐싱 적용
- [ ] Elasticsearch 검색 전환
- [ ] Spring Batch 수익률 집계
- [ ] k6 성능 테스트

### STEP 4 — MSA 전환
- [ ] 멀티 모듈 구조 전환
- [ ] Spring Cloud 구성 (Eureka, Gateway, Config)
- [ ] Kafka 이벤트 기반 전환
- [ ] Saga 패턴 구현 (주문 → 체결 → 실패 → 롤백)
- [ ] `@CurrentUser` Gateway 헤더 전파 방식 전환

### STEP 5 — 인프라
- [ ] Docker Compose → Docker Swarm → k3s
- [ ] GitHub Actions CI/CD
- [ ] AWS 배포 (ECS → EKS)
- [ ] 모니터링 (Prometheus + Grafana + ELK)
- [ ] 장애 대응 훈련

### STEP 6 — AI / ML 연동
- [ ] 뉴스 스크랩 + Elasticsearch 저장
- [ ] 뉴스 감정 분석 (Python 서비스 연동)
- [ ] AI Agent 연동 (Claude MCP — 종목 분석, 전략 최적화, 자연어 → 전략 변환)
- [ ] 백테스팅 엔진 구현 (stock_prices 과거 데이터 기반)
- [ ] AI 대화 히스토리 저장 (MongoDB)
- [ ] AI 예측 결과 저장 (`ai_predictions` 테이블)
- [ ] 종목별 AI 리포트 자동 생성 (`stock_reports` 테이블)

### STEP 7 — MLOps 자동화
- [ ] ML 모델 학습/추론 서비스 구축 (Python — PyTorch/scikit-learn)
- [ ] 모델 자동 재학습 파이프라인 (Kafka 이벤트 트리거)
  - 매일 새로운 시세 데이터 → 학습 데이터 자동 업데이트
  - 재학습 → 백테스팅 검증 → 기존 모델보다 좋으면 자동 교체
- [ ] MLflow 모델 버전 관리
- [ ] Airflow 파이프라인 스케줄링
- [ ] Harness 테스트 — ML 모델 성능 CI/CD
  - PR 머지 시 백테스팅 자동 실행
  - 수익률/샤프지수 기준 통과 여부 체크
  - 실패하면 머지 차단
