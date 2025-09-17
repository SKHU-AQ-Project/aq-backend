package com.example.aq.global.oauth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import com.example.aq.domain.user.entity.SocialType;
import com.example.aq.domain.user.entity.User;
import com.example.aq.domain.user.repository.UserRepository;
import com.example.aq.global.oauth.domain.CustomUserDetails;
import com.example.aq.global.oauth.domain.IdTokenAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdTokenService {

    private final JwtDecoder kakaoJwtDecoder;
    private final UserRepository userRepository;

    public CustomUserDetails loadUserByAccessToken(String idToken) {
        try {
            // JWT 토큰에서 issuer 정보를 추출하기 위해 단순 파싱
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("잘못된 JWT 토큰 형식입니다.");
            }
            
            // Base64 디코딩하여 payload 추출
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> payloadMap = mapper.readValue(payload, Map.class);
            
            String issuer = (String) payloadMap.get("iss");
            SocialType socialType = checkIssuer(issuer);

            Map<String, Object> attributes = tokenToAttributes(idToken, socialType);
            IdTokenAttributes idTokenAttributes = new IdTokenAttributes(attributes, socialType);

            User findUser = checkUser(idTokenAttributes);

            return new CustomUserDetails(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + findUser.getRoleType().toString())),
                    findUser.getEmail(),
                    findUser.getRoleType(),
                    findUser.getId()
            );
        } catch (Exception e) {
            log.warn("ID 토큰 인증 오류: {}", e.getMessage());
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

    private SocialType checkIssuer(String issuer) {
        if ("https://kauth.kakao.com".equals(issuer)) return SocialType.KAKAO;
        // 추후 다른 소셜 로그인 추가 시 확장 가능
        throw new RuntimeException("지원하지 않는 소셜 로그인입니다.");
    }

    private User checkUser(IdTokenAttributes idTokenAttributes) {
        User findUser = userRepository.findByEmail(idTokenAttributes.getUserInfo().getEmail()).orElse(null);
        if (findUser == null) {
            return createUser(idTokenAttributes);
        }
        findUser.markLogin();
        return findUser;
    }

    private User createUser(IdTokenAttributes idTokenAttributes) {
        User createdUser = idTokenAttributes.toUser();
        return userRepository.save(createdUser);
    }

    private Map<String, Object> tokenToAttributes(String idToken, SocialType socialType) {
        try {
            if (socialType == SocialType.KAKAO) {
                return kakaoJwtDecoder.decode(idToken).getClaims();
            }
            // 추후 다른 소셜 로그인 추가 시 확장 가능
        } catch (JwtException e) {
            log.warn("ID 토큰 검증 실패 ({}): {}", socialType, e.getMessage());
            throw e;
        }
        return null;
    }
}
