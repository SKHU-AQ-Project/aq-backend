package com.example.aq.interaction.controller;

import com.example.aq.domain.interaction.BookmarkType;
import com.example.aq.domain.interaction.LikeType;
import com.example.aq.interaction.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@Tag(name = "상호작용", description = "좋아요, 북마크, 팔로우 관련 API")
public class InteractionController {

    private final InteractionService interactionService;

    @PostMapping("/like")
    @Operation(summary = "좋아요 토글", description = "좋아요를 추가하거나 취소합니다")
    public ResponseEntity<Map<String, Object>> toggleLike(
            Authentication authentication,
            @RequestParam LikeType type,
            @RequestParam Long targetId) {
        String userEmail = authentication.getName();
        boolean isLiked = interactionService.toggleLike(userEmail, type, targetId);
        
        return ResponseEntity.ok(Map.of(
                "isLiked", isLiked,
                "type", type,
                "targetId", targetId
        ));
    }

    @PostMapping("/bookmark")
    @Operation(summary = "북마크 토글", description = "북마크를 추가하거나 취소합니다")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            Authentication authentication,
            @RequestParam BookmarkType type,
            @RequestParam Long targetId) {
        String userEmail = authentication.getName();
        boolean isBookmarked = interactionService.toggleBookmark(userEmail, type, targetId);
        
        return ResponseEntity.ok(Map.of(
                "isBookmarked", isBookmarked,
                "type", type,
                "targetId", targetId
        ));
    }

    @PostMapping("/follow/{userId}")
    @Operation(summary = "팔로우 토글", description = "사용자를 팔로우하거나 언팔로우합니다")
    public ResponseEntity<Map<String, Object>> toggleFollow(
            Authentication authentication,
            @PathVariable Long userId) {
        String userEmail = authentication.getName();
        boolean isFollowing = interactionService.toggleFollow(userEmail, userId);
        
        return ResponseEntity.ok(Map.of(
                "isFollowing", isFollowing,
                "targetUserId", userId
        ));
    }

    @GetMapping("/like/status")
    @Operation(summary = "좋아요 상태 확인", description = "특정 콘텐츠의 좋아요 상태를 확인합니다")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            Authentication authentication,
            @RequestParam LikeType type,
            @RequestParam Long targetId) {
        String userEmail = authentication.getName();
        boolean isLiked = interactionService.isLiked(userEmail, type, targetId);
        
        return ResponseEntity.ok(Map.of(
                "isLiked", isLiked,
                "type", type,
                "targetId", targetId
        ));
    }

    @GetMapping("/bookmark/status")
    @Operation(summary = "북마크 상태 확인", description = "특정 콘텐츠의 북마크 상태를 확인합니다")
    public ResponseEntity<Map<String, Object>> getBookmarkStatus(
            Authentication authentication,
            @RequestParam BookmarkType type,
            @RequestParam Long targetId) {
        String userEmail = authentication.getName();
        boolean isBookmarked = interactionService.isBookmarked(userEmail, type, targetId);
        
        return ResponseEntity.ok(Map.of(
                "isBookmarked", isBookmarked,
                "type", type,
                "targetId", targetId
        ));
    }

    @GetMapping("/follow/status/{userId}")
    @Operation(summary = "팔로우 상태 확인", description = "특정 사용자의 팔로우 상태를 확인합니다")
    public ResponseEntity<Map<String, Object>> getFollowStatus(
            Authentication authentication,
            @PathVariable Long userId) {
        String userEmail = authentication.getName();
        boolean isFollowing = interactionService.isFollowing(userEmail, userId);
        
        return ResponseEntity.ok(Map.of(
                "isFollowing", isFollowing,
                "targetUserId", userId
        ));
    }
}
