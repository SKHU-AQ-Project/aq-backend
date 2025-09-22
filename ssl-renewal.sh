#!/bin/bash

# SSL 인증서 자동 갱신 스크립트
# EC2에서 cron 작업으로 실행

DOMAIN="aq-project.duckdns.org"
CONTAINER_NAME="my-app"
KEYSTORE_PASSWORD="aq-project-5967"  # 실제 비밀번호로 변경

echo "SSL 인증서 갱신 시작..."

# Certbot으로 인증서 갱신 시도
if sudo certbot renew --quiet; then
    echo "인증서 갱신 성공"
    
    # PKCS12 형식으로 변환
    echo "PKCS12 형식으로 변환 중..."
    sudo openssl pkcs12 -export \
        -in /etc/letsencrypt/live/$DOMAIN/fullchain.pem \
        -inkey /etc/letsencrypt/live/$DOMAIN/privkey.pem \
        -out /etc/letsencrypt/live/$DOMAIN/keystore.p12 \
        -name springboot \
        -passout pass:$KEYSTORE_PASSWORD
    
    # 권한 설정
    sudo chown root:root /etc/letsencrypt/live/$DOMAIN/keystore.p12
    sudo chmod 644 /etc/letsencrypt/live/$DOMAIN/keystore.p12
    
    # Docker 컨테이너 재시작
    echo "Docker 컨테이너 재시작 중..."
    docker restart $CONTAINER_NAME
    
    if [ $? -eq 0 ]; then
        echo "컨테이너 재시작 성공"
    else
        echo "컨테이너 재시작 실패"
        exit 1
    fi
    
    echo "SSL 인증서 갱신 및 서비스 재시작 완료"
else
    echo "⚠인증서 갱신이 필요하지 않거나 실패했습니다"
fi
