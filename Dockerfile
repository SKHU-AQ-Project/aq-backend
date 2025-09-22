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

# SSL 디렉토리 생성 (외부 인증서 마운트용)
RUN mkdir -p /app/ssl

# 기본 keystore 생성 (외부 인증서가 없는 경우를 위한 fallback)
RUN keytool -genkeypair -alias springboot -keyalg RSA -keysize 2048 \
    -storetype PKCS12 -keystore /app/keystore.p12 \
    -validity 365 -storepass ${SSL_KEYSTORE_PASSWORD} \
    -dname "CN=localhost, OU=SKHU, O=AQ-Project, L=Seoul, S=Seoul, C=KR" \
    -noprompt

# 빌드된 JAR 파일만 런타임 이미지로 복사합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# HTTP와 HTTPS 포트 노출
EXPOSE 8080 8443

# SSL 환경변수 설정
ENV SSL_ENABLED=true
ENV SSL_KEYSTORE_PATH=/app/keystore.p12
ENV SSL_KEYSTORE_PASSWORD=aq-project-5967
ENV SSL_KEY_ALIAS=springboot
ENV SERVER_SSL_PORT=8443

ENTRYPOINT ["java", "-jar", "app.jar"]
