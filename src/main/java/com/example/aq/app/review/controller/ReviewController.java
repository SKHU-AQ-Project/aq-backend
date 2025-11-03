package com.example.aq.app.review.controller;

import com.example.aq.common.dto.BaseResponse;
import com.example.aq.common.dto.PageResponse;
import com.example.aq.common.util.SecurityUtil;
import com.example.aq.app.review.dto.CreateReviewRequest;
import com.example.aq.app.review.dto.ReviewResponse;
import com.example.aq.app.review.dto.UpdateReviewRequest;
import com.example.aq.app.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "리뷰", description = "AI 모델 리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "리뷰 목록 조회", description = "최신순으로 리뷰 목록을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ReviewResponse>>> getReviews(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ReviewResponse> response = reviewService.getReviews(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 리뷰 조회", description = "좋아요 수 기준으로 인기 리뷰를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ReviewResponse>>> getPopularReviews(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount"));
        PageResponse<ReviewResponse> response = reviewService.getPopularReviews(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/top-rated")
    @Operation(summary = "높은 평점 리뷰 조회", description = "평점 기준으로 높은 평점 리뷰를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ReviewResponse>>> getTopRatedReviews(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"));
        PageResponse<ReviewResponse> response = reviewService.getTopRatedReviews(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "리뷰 검색", description = "키워드로 리뷰를 검색합니다")
    public ResponseEntity<BaseResponse<PageResponse<ReviewResponse>>> searchReviews(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ReviewResponse> response = reviewService.searchReviews(keyword, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/model/{modelId}")
    @Operation(summary = "모델별 리뷰 조회", description = "특정 AI 모델의 리뷰를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ReviewResponse>>> getReviewsByModel(
            @Parameter(description = "모델 ID") @PathVariable Long modelId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ReviewResponse> response = reviewService.getReviewsByModel(modelId, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 리뷰 조회", description = "특정 사용자가 작성한 리뷰를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ReviewResponse>>> getReviewsByUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ReviewResponse> response = reviewService.getReviewsByAuthor(userId, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다")
    public ResponseEntity<BaseResponse<ReviewResponse>> getReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long id) {
        
        ReviewResponse response = reviewService.getReview(id);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "리뷰 작성", description = "새로운 리뷰를 작성합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<ReviewResponse>> createReview(
            @Valid @RequestBody CreateReviewRequest request) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        ReviewResponse response = reviewService.createReview(request, currentUserId);
        return ResponseEntity.ok(BaseResponse.success("리뷰가 성공적으로 작성되었습니다", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<ReviewResponse>> updateReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        ReviewResponse response = reviewService.updateReview(id, request, currentUserId);
        return ResponseEntity.ok(BaseResponse.success("리뷰가 성공적으로 수정되었습니다", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다 (인증 필요)")
    public ResponseEntity<BaseResponse<Void>> deleteReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long id) {
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        reviewService.deleteReview(id, currentUserId);
        return ResponseEntity.ok(BaseResponse.success("리뷰가 성공적으로 삭제되었습니다", null));
    }
}
