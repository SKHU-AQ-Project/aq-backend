package com.example.aq.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenUtil {

    @Value("${jwt.secret:your-secret-key-change-this-in-production}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24시간 기본값
    private Long expiration;

    private static final String EMAIL_CLAIM = "email";
    private static final String USER_ID_CLAIM = "userId";

    public String generateToken(Long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim(USER_ID_CLAIM, userId)
                .withClaim(EMAIL_CLAIM, email)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC512(secret));
    }

    public Long getUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            return decodedJWT.getClaim(USER_ID_CLAIM).asLong();
        } catch (Exception e) {
            log.error("토큰에서 사용자 ID 추출 실패", e);
            return null;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            return decodedJWT.getClaim(EMAIL_CLAIM).asString();
        } catch (Exception e) {
            log.error("토큰에서 이메일 추출 실패", e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("토큰 검증 실패", e);
            return false;
        }
    }

    private DecodedJWT verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC512(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}

