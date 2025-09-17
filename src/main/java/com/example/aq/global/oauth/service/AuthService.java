package com.example.aq.global.oauth.service;

import com.example.aq.domain.user.entity.User;
import com.example.aq.domain.user.repository.UserRepository;
import com.example.aq.global.common.dto.LoginResponseDto;
import com.example.aq.global.common.dto.UserInfoResponseDto;
import com.example.aq.global.jwt.JwtService;
import com.example.aq.global.oauth.domain.CustomUserDetails;
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
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String lastLoginAtStr = (user.getLastLoginAt() != null) ? user.getLastLoginAt().toString() : null;

        return new UserInfoResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getProfileUrl(),
                lastLoginAtStr,
                user.getRoleType()
        );
    }

    public LoginResponseDto reissueTokens(String oldAccessToken, String oldRefreshToken) {
        if (!jwtService.isTokenValid(oldRefreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        User user = userRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new RuntimeException("만료된 토큰입니다."));

        String newAccessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String newRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(newRefreshToken);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }
}
