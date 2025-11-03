package com.example.aq.app.interaction.controller;

import com.example.aq.common.dto.BaseResponse;
import com.example.aq.common.util.SecurityUtil;
import com.example.aq.app.interaction.domain.BookmarkType;
import com.example.aq.app.interaction.domain.LikeType;
import com.example.aq.app.interaction.service.InteractionService;
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
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "상호작용", description = "좋아요 및 북마크 관련 API")
public class InteractionController {

    private final InteractionService interactionService;

    // Like 관련 API
    @PostMapping("/likes")
    @Operation(summary = "좋아요 토글", description = "좋아요를 추가하거나 취소합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<Map<String, Object>>> toggleLike(
            @Parameter(description = "대상 ID") @RequestParam Long targetId,
            @Parameter(description = "대상 타입 (REVIEW, RECIPE, COMMENT)") @RequestParam LikeType targetType) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        boolean isLiked = interactionService.toggleLike(currentUserId, targetId, targetType);
        Long likeCount = interactionService.getLikeCount(targetId, targetType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likeCount", likeCount);
        
        return ResponseEntity.ok(BaseResponse.success(
                isLiked ? "좋아요가 추가되었습니다" : "좋아요가 취소되었습니다",
                result));
    }

    @GetMapping("/likes/check")
    @Operation(summary = "좋아요 상태 확인", description = "특정 대상에 대한 좋아요 상태를 확인합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<Map<String, Object>>> checkLike(
            @Parameter(description = "대상 ID") @RequestParam Long targetId,
            @Parameter(description = "대상 타입 (REVIEW, RECIPE, COMMENT)") @RequestParam LikeType targetType) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        boolean isLiked = interactionService.isLiked(currentUserId, targetId, targetType);
        Long likeCount = interactionService.getLikeCount(targetId, targetType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likeCount", likeCount);
        
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/likes/my")
    @Operation(summary = "내가 좋아요한 목록 조회", description = "사용자가 좋아요한 대상 ID 목록을 조회합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<List<Long>>> getMyLikes(
            @Parameter(description = "대상 타입 (REVIEW, RECIPE, COMMENT)") @RequestParam LikeType targetType) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        List<Long> likedIds = interactionService.getLikedTargetIds(currentUserId, targetType);
        return ResponseEntity.ok(BaseResponse.success(likedIds));
    }

    // Bookmark 관련 API
    @PostMapping("/bookmarks")
    @Operation(summary = "북마크 토글", description = "북마크를 추가하거나 취소합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<Map<String, Object>>> toggleBookmark(
            @Parameter(description = "대상 ID") @RequestParam Long targetId,
            @Parameter(description = "대상 타입 (REVIEW, RECIPE, MODEL)") @RequestParam BookmarkType targetType) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        boolean isBookmarked = interactionService.toggleBookmark(currentUserId, targetId, targetType);
        Long bookmarkCount = interactionService.getBookmarkCount(targetId, targetType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isBookmarked", isBookmarked);
        result.put("bookmarkCount", bookmarkCount);
        
        return ResponseEntity.ok(BaseResponse.success(
                isBookmarked ? "북마크가 추가되었습니다" : "북마크가 취소되었습니다",
                result));
    }

    @GetMapping("/bookmarks/check")
    @Operation(summary = "북마크 상태 확인", description = "특정 대상에 대한 북마크 상태를 확인합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<Map<String, Object>>> checkBookmark(
            @Parameter(description = "대상 ID") @RequestParam Long targetId,
            @Parameter(description = "대상 타입 (REVIEW, RECIPE, MODEL)") @RequestParam BookmarkType targetType) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        boolean isBookmarked = interactionService.isBookmarked(currentUserId, targetId, targetType);
        Long bookmarkCount = interactionService.getBookmarkCount(targetId, targetType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isBookmarked", isBookmarked);
        result.put("bookmarkCount", bookmarkCount);
        
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/bookmarks/my")
    @Operation(summary = "내가 북마크한 목록 조회", description = "사용자가 북마크한 대상 ID 목록을 조회합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<List<Long>>> getMyBookmarks(
            @Parameter(description = "대상 타입 (REVIEW, RECIPE, MODEL)") @RequestParam BookmarkType targetType) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        List<Long> bookmarkedIds = interactionService.getBookmarkedTargetIds(currentUserId, targetType);
        return ResponseEntity.ok(BaseResponse.success(bookmarkedIds));
    }
}

