# 1. Base Image 선택
FROM openjdk:21-jdk-slim-bullseye

# 2. JAR 파일 이름을 변수로 정의
ARG JAR_FILE="pixo-website-0.0.1-SNAPSHOT.jar"

# 3. JAR 파일을 Cloud Build 환경 안으로 복사
# ⚠️ 핵심 수정: build/libs 폴더 전체를 복사하는 대신, build 폴더의 내용 전체를 복사합니다.
# 이렇게 하면 build/libs/ 에 있는 JAR 파일이 컨테이너의 루트에 복사됩니다.
COPY build/libs/${JAR_FILE} /app.jar

# 4. 엔트리 포인트 설정
ENTRYPOINT ["java", "-jar", "/app.jar"]