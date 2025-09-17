package com.example.aq.global.oauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aq.global.exception.GeneralException;
import com.example.aq.global.oauth.domain.CustomUserDetails;
import com.example.aq.global.oauth.domain.IdTokenAttributes;
import com.example.aq.user.domain.SocialType;
import com.example.aq.user.domain.User;
import com.example.aq.user.repository.UserRepository;
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
            DecodedJWT decodedJWT = JWT.decode(idToken);
            SocialType socialType = checkIssuer(decodedJWT.getIssuer());

            Map<String, Object> attributes = tokenToattributes(idToken, socialType);
            IdTokenAttributes idTokenAttributes = new IdTokenAttributes(attributes, socialType);

            User findUser = checkUser(idTokenAttributes);

            return new CustomUserDetails(
                    Collections.singleton(new SimpleGrantedAuthority(findUser.getRoleType().toString())),
                    findUser.getEmail(),
                    findUser.getRoleType(),
                    findUser.getId()
            );
        } catch (JWTDecodeException | JwtException e) {
            log.warn("ID 토큰 인증 오류: {}", e.getMessage());
            throw new GeneralException("INVALID_TOKEN", "유효하지 않은 토큰입니다.");
        }
    }

    private SocialType checkIssuer(String issuer) {
        if ("https://kauth.kakao.com".equals(issuer)) return SocialType.KAKAO;
        throw new GeneralException("INVALID_TOKEN", "지원하지 않는 소셜 로그인입니다.");
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

    private Map<String, Object> tokenToattributes(String idToken, SocialType socialType) {
        try {
            if (socialType == SocialType.KAKAO) return kakaoJwtDecoder.decode(idToken).getClaims();
        } catch (JwtException e) {
            log.warn("ID 토큰 검증 실패 ({}): {}", socialType, e.getMessage());
            throw e;
        }
        return null;
    }
}
