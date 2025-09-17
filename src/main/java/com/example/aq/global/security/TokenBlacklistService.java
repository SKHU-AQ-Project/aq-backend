package com.example.aq.global.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TokenBlacklistService {

    // 메모리 기반 블랙리스트 (실제 운영환경에서는 Redis나 DB 사용 권장)
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token) {
        try {
            blacklistedTokens.add(token);
            log.info("토큰이 블랙리스트에 추가되었습니다.");
        } catch (Exception e) {
            log.error("토큰 블랙리스트 추가 중 오류 발생: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            return blacklistedTokens.contains(token);
        } catch (Exception e) {
            log.error("토큰 블랙리스트 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
}
