package com.example.aq.user.controller;

import com.example.aq.user.dto.UpdateProfileRequest;
import com.example.aq.user.dto.UserProfileResponse;
import com.example.aq.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필을 조회합니다")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 프로필을 수정합니다")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = getCurrentUserId(authentication);
        UserProfileResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 프로필 조회", description = "특정 사용자의 프로필을 조회합니다")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "사용자 검색", description = "닉네임으로 사용자를 검색합니다")
    public ResponseEntity<Page<UserProfileResponse>> searchUsers(
            @RequestParam String nickname,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserProfileResponse> response = userService.searchUsers(nickname, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top")
    @Operation(summary = "인기 사용자 조회", description = "포인트가 높은 사용자들을 조회합니다")
    public ResponseEntity<Page<UserProfileResponse>> getTopUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserProfileResponse> response = userService.getTopUsers(pageable);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email).getId();
    }
}
