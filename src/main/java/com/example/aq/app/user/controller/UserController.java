package com.example.aq.app.user.controller;

import com.example.aq.common.dto.BaseResponse;
import com.example.aq.app.user.dto.UpdateUserRequest;
import com.example.aq.app.user.dto.UserResponse;
import com.example.aq.app.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "사용자", description = "사용자 정보 관련 API")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "사용자 정보 조회", description = "사용자 ID로 사용자 정보를 조회합니다")
    public ResponseEntity<BaseResponse<UserResponse>> getUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id) {
        
        UserResponse response = userService.getUser(id);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 사용자 조회", description = "이메일로 사용자 정보를 조회합니다")
    public ResponseEntity<BaseResponse<UserResponse>> getUserByEmail(
            @Parameter(description = "이메일") @PathVariable String email) {
        
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/nickname/{nickname}")
    @Operation(summary = "닉네임으로 사용자 조회", description = "닉네임으로 사용자 정보를 조회합니다")
    public ResponseEntity<BaseResponse<UserResponse>> getUserByNickname(
            @Parameter(description = "닉네임") @PathVariable String nickname) {
        
        UserResponse response = userService.getUserByNickname(nickname);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다")
    public ResponseEntity<BaseResponse<UserResponse>> updateUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        
        // TODO: 현재 로그인한 사용자 ID를 가져오는 로직 구현 필요
        Long currentUserId = 1L; // 임시 값
        
        UserResponse response = userService.updateUser(id, request, currentUserId);
        return ResponseEntity.ok(BaseResponse.success("사용자 정보가 성공적으로 수정되었습니다", response));
    }

    @PostMapping("/{id}/points")
    @Operation(summary = "사용자 포인트 추가", description = "사용자에게 포인트를 추가합니다")
    public ResponseEntity<BaseResponse<UserResponse>> addPoints(
            @Parameter(description = "사용자 ID") @PathVariable Long id,
            @Parameter(description = "추가할 포인트") @RequestParam Integer points) {
        
        UserResponse response = userService.addPoints(id, points);
        return ResponseEntity.ok(BaseResponse.success("포인트가 추가되었습니다", response));
    }
}

