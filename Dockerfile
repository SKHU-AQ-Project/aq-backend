# 1단계: 빌드
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 2단계: 런타임
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 타임존/UTF-8 세팅
ENV TZ=Asia/Seoul
ENV LANG=C.UTF-8

# SSL keystore 비밀번호를 빌드 인자로 받기 (GitHub Secrets에서 주입)
ARG SSL_KEYSTORE_PASSWORD

# SSL keystore 생성
RUN keytool -genkeypair -alias springboot -keyalg RSA -keysize 2048 \
    -storetype PKCS12 -keystore /app/keystore.p12 \
    -validity 365 -storepass ${SSL_KEYSTORE_PASSWORD} \
    -dname "CN=13.209.3.82, OU=SKHU, O=AQ-Project, L=Seoul, S=Seoul, C=KR" \
    -noprompt

# 빌드된 JAR 파일만 런타임 이미지로 복사합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# HTTP와 HTTPS 포트 노출
EXPOSE 8080 8443

# SSL 환경변수 설정
ENV SSL_ENABLED=true
ENV SSL_KEYSTORE_PATH=/app/keystore.p12

ENTRYPOINT ["java", "-jar", "app.jar"]
