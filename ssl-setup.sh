#!/bin/bash

# DuckDNS 도메인 기반 SSL 인증서 설정 스크립트
# 사용법: ./ssl-setup.sh your-domain.duckdns.org

if [ -z "$1" ]; then
    echo "사용법: $0 <duckdns-domain>"
    echo "예시: $0 myapp.duckdns.org"
    exit 1
fi

DOMAIN=$1
EMAIL="admin@aq-project.duckdns.org"  # Let's Encrypt 인증서 갱신 알림용

echo "🔧 DuckDNS 도메인 기반 SSL 인증서 설정 시작..."
echo "도메인: $DOMAIN"

# Certbot 설치 확인 및 설치
if ! command -v certbot &> /dev/null; then
    echo "Certbot 설치 중..."
    sudo apt update
    sudo apt install -y certbot
fi

# Let's Encrypt 인증서 발급
echo "Let's Encrypt 인증서 발급 중..."
sudo certbot certonly --standalone \
    --email $EMAIL \
    --agree-tos \
    --no-eff-email \
    -d $DOMAIN

if [ $? -eq 0 ]; then
    echo "인증서 발급 성공!"
    
    # 인증서를 PKCS12 형식으로 변환
    echo "PKCS12 형식으로 변환 중..."
    sudo openssl pkcs12 -export \
        -in /etc/letsencrypt/live/$DOMAIN/fullchain.pem \
        -inkey /etc/letsencrypt/live/$DOMAIN/privkey.pem \
        -out /etc/letsencrypt/live/$DOMAIN/keystore.p12 \
        -name springboot \
        -passout pass:aq-project-5967
    
    # 권한 설정
    sudo chown $USER:$USER /etc/letsencrypt/live/$DOMAIN/keystore.p12
    sudo chmod 644 /etc/letsencrypt/live/$DOMAIN/keystore.p12
    
    echo "SSL 설정 완료!"
    echo "인증서 위치: /etc/letsencrypt/live/$DOMAIN/"
    echo "키스토어: /etc/letsencrypt/live/$DOMAIN/keystore.p12"
    
    # 환경변수 파일 생성
    cat > .env.ssl << EOF
# SSL 설정
SSL_ENABLED=true
SSL_KEYSTORE_PATH=/etc/letsencrypt/live/$DOMAIN/keystore.p12
SSL_KEYSTORE_PASSWORD=aq-project-5967
SSL_KEY_ALIAS=springboot
SERVER_SSL_PORT=8443

# 도메인 설정
DOMAIN=$DOMAIN
NEXT_PUBLIC_BACKEND_URL=https://$DOMAIN:8443
NEXT_PUBLIC_FRONTEND_URL=https://$DOMAIN
EOF
    
    echo "환경변수 파일 생성: .env.ssl"
    echo ""
    echo "다음 단계:"
    echo "1. .env.ssl 파일을 확인하고 필요한 정보 수정"
    echo "2. Docker 컨테이너 재시작"
    echo "3. https://$DOMAIN:8443 으로 접속 테스트"
    
else
    echo "인증서 발급 실패"
    echo "다음을 확인해주세요:"
    echo "- 도메인이 올바른지 확인"
    echo "- 포트 80, 443이 열려있는지 확인"
    echo "- 방화벽 설정 확인"
    exit 1
fi
