package com.example.aq.review.controller;

import com.example.aq.review.dto.CreateReviewRequest;
import com.example.aq.review.dto.ReviewResponse;
import com.example.aq.review.dto.UpdateReviewRequest;
import com.example.aq.review.service.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰", description = "AI 모델 리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "리뷰 작성", description = "새로운 AI 모델 리뷰를 작성합니다")
    public ResponseEntity<ReviewResponse> createReview(
            Authentication authentication,
            @Valid @RequestBody CreateReviewRequest request) {
        String userEmail = authentication.getName();
        ReviewResponse response = reviewService.createReview(userEmail, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 조회", description = "특정 리뷰의 상세 정보를 조회합니다")
    public ResponseEntity<ReviewResponse> getReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        ReviewResponse response = reviewService.getReview(reviewId, userEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            Authentication authentication,
            @Valid @RequestBody UpdateReviewRequest request) {
        String userEmail = authentication.getName();
        ReviewResponse response = reviewService.updateReview(reviewId, userEmail, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        reviewService.deleteReview(reviewId, userEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "리뷰 목록 조회", description = "전체 리뷰 목록을 조회합니다")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<ReviewResponse> response = reviewService.getReviews(pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/model/{modelId}")
    @Operation(summary = "모델별 리뷰 조회", description = "특정 모델의 리뷰 목록을 조회합니다")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByModel(
            @PathVariable Long modelId,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<ReviewResponse> response = reviewService.getReviewsByModel(modelId, pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 리뷰 조회", description = "특정 사용자의 리뷰 목록을 조회합니다")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<ReviewResponse> response = reviewService.getReviewsByUser(userId, pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "리뷰 검색", description = "키워드로 리뷰를 검색합니다")
    public ResponseEntity<Page<ReviewResponse>> searchReviews(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<ReviewResponse> response = reviewService.searchReviews(keyword, pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 리뷰 조회", description = "인기 리뷰 목록을 조회합니다")
    public ResponseEntity<Page<ReviewResponse>> getPopularReviews(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<ReviewResponse> response = reviewService.getPopularReviews(pageable, userEmail);
        return ResponseEntity.ok(response);
    }
}
