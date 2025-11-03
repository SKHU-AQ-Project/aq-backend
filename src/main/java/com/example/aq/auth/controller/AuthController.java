package com.example.aq.auth.controller;

import com.example.aq.auth.dto.AuthResponse;
import com.example.aq.auth.dto.LoginRequest;
import com.example.aq.auth.dto.SignUpRequest;
import com.example.aq.auth.service.AuthService;
import com.example.aq.common.dto.BaseResponse;
import com.example.aq.common.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "인증", description = "회원가입, 로그인, 로그아웃, 탈퇴 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다. 이메일 인증이 필요합니다.")
    public ResponseEntity<BaseResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok(BaseResponse.success("회원가입이 완료되었습니다. 이메일을 확인하여 인증을 완료해주세요.", null));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "이메일 인증", description = "이메일 인증 토큰으로 이메일을 인증합니다")
    public ResponseEntity<BaseResponse<Void>> verifyEmail(
            @Parameter(description = "인증 토큰") @RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(BaseResponse.success("이메일 인증이 완료되었습니다.", null));
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "인증 이메일 재전송", description = "인증 이메일을 재전송합니다")
    public ResponseEntity<BaseResponse<Void>> resendVerificationEmail(
            @Parameter(description = "이메일") @RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok(BaseResponse.success("인증 이메일이 재전송되었습니다.", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(BaseResponse.success("로그인에 성공했습니다.", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃합니다")
    public ResponseEntity<BaseResponse<Void>> logout(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("로그인이 필요합니다");
        }
        
        Long userId = (Long) authentication.getPrincipal();
        authService.logout(userId);
        return ResponseEntity.ok(BaseResponse.success("로그아웃되었습니다.", null));
    }

    @DeleteMapping("/account")
    @Operation(summary = "계정 탈퇴", description = "계정을 탈퇴합니다")
    public ResponseEntity<BaseResponse<Void>> deleteAccount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("로그인이 필요합니다");
        }
        
        Long userId = (Long) authentication.getPrincipal();
        authService.deleteAccount(userId);
        return ResponseEntity.ok(BaseResponse.success("계정이 삭제되었습니다.", null));
    }
}

