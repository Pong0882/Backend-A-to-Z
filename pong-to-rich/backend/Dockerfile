# 1단계 — 빌드
# JDK 포함된 이미지로 Gradle 빌드
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# 의존성 캐시 활용을 위해 gradle 설정 파일 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (소스 변경 시에도 이 레이어는 캐시됨)
RUN ./gradlew dependencies --no-daemon

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon

# 2단계 — 실행
# JRE만 포함된 경량 이미지 (JDK 불필요)
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
