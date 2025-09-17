package com.example.aq.global.oauth.service;

import com.example.aq.global.dto.LoginResponseDto;
import com.example.aq.global.dto.UserInfoResponseDto;
import com.example.aq.global.exception.GeneralException;
import com.example.aq.global.oauth.domain.CustomUserDetails;
import com.example.aq.global.security.JwtService;
import com.example.aq.global.security.TokenBlacklistService;
import com.example.aq.user.domain.User;
import com.example.aq.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final IdTokenService idTokenService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public LoginResponseDto login(String idToken) {
        CustomUserDetails userDetails = idTokenService.loadUserByAccessToken(idToken);
        String email = userDetails.getUsername();
        Long userId = userDetails.getUserId();

        String accessToken = jwtService.createAccessToken(email, userId);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.updateRefreshToken(email, refreshToken);
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."));

        String lastLoginAtStr = (user.getLastLoginAt() != null) ? user.getLastLoginAt().toString() : null;

        return new UserInfoResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getProfileUrl(),
                lastLoginAtStr,
                user.getRoleType()
        );
    }

    @Transactional
    public void logout(Long userId, String accessToken) {
        // 액세스 토큰을 블랙리스트에 추가
        if (accessToken != null && !accessToken.isEmpty()) {
            tokenBlacklistService.blacklistToken(accessToken);
        }

        // 사용자의 리프레시 토큰 제거
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."));
        
        user.updateRefreshToken(null);
        log.info("로그아웃 완료: userId={}", userId);
    }

    public LoginResponseDto reissueTokens(String oldAccessToken, String oldRefreshToken) {
        if (!jwtService.isTokenValid(oldRefreshToken)) {
            throw new GeneralException("INVALID_TOKEN", "유효하지 않은 리프레시 토큰입니다.");
        }

        User user = userRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new GeneralException("EXPIRED_TOKEN", "만료된 토큰입니다."));

        String newAccessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String newRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(newRefreshToken);

        tokenBlacklistService.blacklistToken(oldAccessToken);
        tokenBlacklistService.blacklistToken(oldRefreshToken);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }
}
