package com.example.aq.global.jwt;

import com.example.aq.domain.user.repository.UserRepository;
import com.example.aq.global.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String USER_ID_CLAIM = "userId";

    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;

    public String createAccessToken(String email, Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .setExpiration(new Date(now.getTime() + jwtConfig.getAccess().getExpirationAsLong()))
                .claim(EMAIL_CLAIM, email)
                .claim(USER_ID_CLAIM, userId)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .setExpiration(new Date(now.getTime() + jwtConfig.getRefresh().getExpirationAsLong()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Optional<String> extractEmail(String token) {
        try {
            return Optional.ofNullable(Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get(EMAIL_CLAIM, String.class));
        } catch (Exception e) {
            log.error("토큰에서 이메일 추출 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Long> extractUserId(String token) {
        try {
            return Optional.ofNullable(Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get(USER_ID_CLAIM, Long.class));
        } catch (Exception e) {
            log.error("토큰에서 사용자 ID 추출 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> {
                            throw new RuntimeException("일치하는 회원이 없습니다.");
                        }
                );
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
