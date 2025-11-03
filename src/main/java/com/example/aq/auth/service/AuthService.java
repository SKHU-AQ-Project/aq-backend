package com.example.aq.auth.service;

import com.example.aq.auth.domain.EmailVerificationToken;
import com.example.aq.auth.dto.AuthResponse;
import com.example.aq.auth.dto.LoginRequest;
import com.example.aq.auth.dto.SignUpRequest;
import com.example.aq.auth.repository.EmailVerificationTokenRepository;
import com.example.aq.auth.util.JwtTokenUtil;
import com.example.aq.app.user.domain.User;
import com.example.aq.app.user.repository.UserRepository;
import com.example.aq.common.exception.ResourceNotFoundException;
import com.example.aq.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenUtil jwtTokenUtil;

    private static final int TOKEN_EXPIRATION_HOURS = 24;

    public void signUp(SignUpRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
        }

        // 사용자 생성 (이메일 인증 전까지는 비활성화)
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();
        user.disable(); // 이메일 인증 전까지 비활성화
        userRepository.save(user);

        // 이메일 인증 토큰 생성
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .email(request.getEmail())
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS))
                .build();
        tokenRepository.save(verificationToken);

        // 인증 이메일 전송
        emailService.sendVerificationEmail(request.getEmail(), token);

        log.info("회원가입 완료: {}", request.getEmail());
    }

    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 토큰입니다"));

        if (!verificationToken.isValid()) {
            throw new IllegalArgumentException("만료되었거나 이미 사용된 인증 토큰입니다");
        }

        User user = userRepository.findByEmail(verificationToken.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "email", verificationToken.getEmail()));

        verificationToken.verify();
        user.enable();

        tokenRepository.save(verificationToken);
        userRepository.save(user);

        log.info("이메일 인증 완료: {}", verificationToken.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        if (!user.getEnabled()) {
            throw new UnauthorizedException("이메일 인증이 완료되지 않았습니다. 이메일을 확인해주세요");
        }

        String token = jwtTokenUtil.generateToken(user.getId(), user.getEmail());

        log.info("로그인 완료: {}", user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    public void logout(Long userId) {
        // JWT는 stateless이므로 서버 측에서 특별한 처리가 필요 없지만,
        // 필요시 리프레시 토큰 무효화 등을 구현할 수 있습니다
        log.info("로그아웃 완료: {}", userId);
    }

    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", userId));

        user.disable();
        userRepository.save(user);

        // 관련 토큰 삭제
        tokenRepository.deleteByEmail(user.getEmail());

        log.info("계정 삭제 완료: {}", user.getEmail());
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "email", email));

        if (user.getEnabled()) {
            throw new IllegalArgumentException("이미 인증된 이메일입니다");
        }

        // 기존 토큰 삭제
        tokenRepository.deleteByEmail(email);

        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .email(email)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS))
                .build();
        tokenRepository.save(verificationToken);

        // 인증 이메일 전송
        emailService.sendVerificationEmail(email, token);

        log.info("인증 이메일 재전송: {}", email);
    }
}

