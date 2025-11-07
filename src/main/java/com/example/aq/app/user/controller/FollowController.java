package com.example.aq.app.user.controller;

import com.example.aq.app.user.dto.FollowListResponse;
import com.example.aq.app.user.dto.FollowStatsResponse;
import com.example.aq.app.user.service.FollowService;
import com.example.aq.common.dto.BaseResponse;
import com.example.aq.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "팔로우", description = "팔로우/팔로워 관련 API")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    @Operation(summary = "팔로우 토글", description = "사용자를 팔로우하거나 언팔로우합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<Map<String, Object>>> toggleFollow(
            @Parameter(description = "팔로우할 사용자 ID") @PathVariable Long userId) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        boolean isFollowing = followService.toggleFollow(currentUserId, userId);
        FollowStatsResponse stats = followService.getFollowStats(userId, currentUserId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isFollowing", isFollowing);
        result.put("followerCount", stats.getFollowerCount());
        result.put("followingCount", stats.getFollowingCount());
        
        return ResponseEntity.ok(BaseResponse.success(
                isFollowing ? "팔로우했습니다" : "언팔로우했습니다",
                result));
    }

    @GetMapping("/{userId}/followers")
    @Operation(summary = "팔로워 목록 조회", description = "사용자의 팔로워 목록을 조회합니다")
    public ResponseEntity<BaseResponse<FollowListResponse>> getFollowers(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        FollowListResponse response = followService.getFollowers(userId, page, size);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/{userId}/following")
    @Operation(summary = "팔로잉 목록 조회", description = "사용자가 팔로우하는 사용자 목록을 조회합니다")
    public ResponseEntity<BaseResponse<FollowListResponse>> getFollowing(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        FollowListResponse response = followService.getFollowing(userId, page, size);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/{userId}/follow-stats")
    @Operation(summary = "팔로우 통계 조회", description = "팔로워/팔로잉 수와 팔로우 상태를 조회합니다")
    public ResponseEntity<BaseResponse<FollowStatsResponse>> getFollowStats(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        Long currentUserId = null;
        try {
            currentUserId = SecurityUtil.getCurrentUserId();
        } catch (Exception e) {
            // 비로그인 사용자의 경우 무시
        }
        
        FollowStatsResponse response = followService.getFollowStats(userId, currentUserId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/{userId}/is-following")
    @Operation(summary = "팔로우 여부 확인", description = "현재 사용자가 특정 사용자를 팔로우하는지 확인합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<Map<String, Boolean>>> checkIsFollowing(
            @Parameter(description = "확인할 사용자 ID") @PathVariable Long userId) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        boolean isFollowing = followService.isFollowing(currentUserId, userId);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFollowing", isFollowing);
        
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/following/ids")
    @Operation(summary = "팔로잉 ID 목록 조회", description = "현재 사용자가 팔로우하는 사용자 ID 목록을 조회합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<List<Long>>> getFollowingIds() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        List<Long> followingIds = followService.getFollowingIds(currentUserId);
        return ResponseEntity.ok(BaseResponse.success(followingIds));
    }
}

