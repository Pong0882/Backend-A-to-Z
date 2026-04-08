# Dockerfile 멀티스테이지 빌드

## 개념

하나의 Dockerfile 안에서 여러 단계(stage)로 나눠서 빌드하는 방식.
앞 단계의 결과물만 다음 단계로 가져오고, 빌드 도구는 최종 이미지에 포함되지 않는다.

## 왜 필요한가

Java 앱을 Docker로 실행하려면 두 가지가 필요하다.

- **빌드 시**: JDK (컴파일러 포함) + Gradle → `.jar` 생성
- **실행 시**: JRE (런타임만) → `.jar` 실행

JDK 이미지는 약 400MB, JRE 이미지는 약 170MB다.
싱글스테이지로 빌드하면 JDK가 최종 이미지에 그대로 남는다. 실행에 필요 없는 컴파일러, 빌드 도구가 전부 포함되어 이미지가 불필요하게 크다.

멀티스테이지 빌드로 `.jar`만 추출해서 JRE 이미지에 올리면 최종 이미지 크기를 크게 줄일 수 있다.

## pong-to-rich Dockerfile

```dockerfile
# 1단계 — 빌드
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# 의존성 캐시 활용을 위해 gradle 설정 파일 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# 의존성 다운로드 (소스 변경 시에도 이 레이어는 캐시됨)
RUN ./gradlew dependencies --no-daemon

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon

# 2단계 — 실행
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 핵심 포인트

**`AS builder`** — 첫 번째 스테이지에 이름을 붙임. 이후 단계에서 `--from=builder` 로 참조한다.

**`COPY --from=builder`** — 첫 번째 스테이지에서 만들어진 `.jar`만 두 번째 스테이지로 가져온다. JDK, Gradle, 소스코드는 최종 이미지에 포함되지 않는다.

**의존성을 먼저 복사하는 이유** — Docker는 레이어 단위로 캐시한다. `build.gradle`이 바뀌지 않으면 `./gradlew dependencies` 레이어는 캐시를 재사용한다. 소스코드만 바뀐 경우 의존성 다운로드를 건너뛰어 빌드가 빨라진다.

## 싱글스테이지 vs 멀티스테이지

```
싱글스테이지: JDK(400MB) + 소스 + 빌드 결과물 → 최종 이미지 ~600MB
멀티스테이지: JRE(170MB) + .jar만            → 최종 이미지 ~200MB
```

## 트러블슈팅 — gradlew 실행 권한 없음

```
RUN ./gradlew bootJar --no-daemon
# exit code: 126 (Permission denied)
```

로컬 Windows에서 작업한 `gradlew` 파일은 실행 권한(`+x`)이 없는 상태로 복사된다.
Linux 컨테이너 안에서는 실행 권한이 있어야 스크립트를 실행할 수 있다.

```dockerfile
RUN chmod +x gradlew  # ← 이 줄이 없으면 빌드 실패
```
