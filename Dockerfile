# 1단계: 빌드 스테이지 (소스 코드를 JAR 파일로 변환)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
# 실행 권한 부여 및 빌드 (테스트는 시간 절약을 위해 제외)
RUN chmod +x ./gradlew && ./gradlew clean build -x test

# 2단계: 실행 스테이지 (실제 서버 운영)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# 1단계(builder)에서 생성된 JAR 파일만 가져옴
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]