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

import java.security.SecureRandom;
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
    private static final int CODE_EXPIRATION_MINUTES = 10;
    private static final SecureRandom random = new SecureRandom();

    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();
        user.disable(); // 이메일 인증 전까지 비활성화
        userRepository.save(user);

        String code = generateVerificationCode();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .email(request.getEmail())
                .token(code)
                .expiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES))
                .build();
        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(request.getEmail(), code);

        log.info("회원가입 완료: {}", request.getEmail());
    }

    public void verifyEmail(String email, String code) {
        EmailVerificationToken verificationToken = tokenRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드를 찾을 수 없습니다. 이메일을 확인해주세요."));

        if (!verificationToken.getToken().equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다");
        }

        if (!verificationToken.isValid()) {
            throw new IllegalArgumentException("만료되었거나 이미 사용된 인증 코드입니다");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "email", email));

        verificationToken.verify();
        user.enable();

        tokenRepository.save(verificationToken);
        userRepository.save(user);

        log.info("이메일 인증 완료: {}", email);
    }

    private String generateVerificationCode() {
        // 6자리 숫자 코드 생성
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
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

        // 새 인증 코드 생성
        String code = generateVerificationCode();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .email(email)
                .token(code)
                .expiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES))
                .build();
        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(email, code);

        log.info("인증 이메일 재전송: {}", email);
    }
}

