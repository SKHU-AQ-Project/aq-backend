package com.example.aq.review.service;

import com.example.aq.common.exception.BusinessException;
import com.example.aq.common.exception.ErrorCode;
import com.example.aq.domain.interaction.BookmarkRepository;
import com.example.aq.domain.interaction.BookmarkType;
import com.example.aq.domain.interaction.LikeRepository;
import com.example.aq.domain.interaction.LikeType;
import com.example.aq.domain.model.AIModel;
import com.example.aq.domain.model.AIModelRepository;
import com.example.aq.domain.review.Review;
import com.example.aq.domain.review.ReviewRepository;
import com.example.aq.domain.review.ReviewStatus;
import com.example.aq.domain.user.User;
import com.example.aq.domain.user.UserRepository;
import com.example.aq.review.dto.CreateReviewRequest;
import com.example.aq.review.dto.ReviewResponse;
import com.example.aq.review.dto.UpdateReviewRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AIModelRepository modelRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public ReviewResponse createReview(String userEmail, CreateReviewRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage()));
        AIModel model = modelRepository.findById(request.getModelId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MODEL_NOT_FOUND.getCode(), ErrorCode.MODEL_NOT_FOUND.getMessage()));

        Review review = Review.builder()
                .user(user)
                .model(model)
                .title(request.getTitle())
                .content(request.getContent())
                .rating(request.getRating())
                .useCase(request.getUseCase())
                .inputExample(request.getInputExample())
                .outputExample(request.getOutputExample())
                .tags(request.getTags())
                .screenshotUrl(request.getScreenshotUrl())
                .build();

        Review savedReview = reviewRepository.save(review);
        user.addPoints(10); // 리뷰 작성 시 10점 추가

        log.info("새 리뷰 작성: {} by {}", savedReview.getId(), user.getEmail());
        return convertToReviewResponse(savedReview, user.getId());
    }

    public ReviewResponse getReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND.getCode(), ErrorCode.REVIEW_NOT_FOUND.getMessage()));

        if (review.getStatus() != ReviewStatus.PUBLISHED) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND.getCode(), ErrorCode.REVIEW_NOT_FOUND.getMessage());
        }

        review.incrementViewCount();
        reviewRepository.save(review);

        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return convertToReviewResponse(review, userId);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, String userEmail, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND.getCode(), ErrorCode.REVIEW_NOT_FOUND.getMessage()));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage()));
        
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED.getCode(), ErrorCode.REVIEW_ACCESS_DENIED.getMessage());
        }

        review.updateReview(
                request.getTitle(),
                request.getContent(),
                request.getRating(),
                request.getUseCase(),
                request.getInputExample(),
                request.getOutputExample(),
                request.getTags(),
                request.getScreenshotUrl()
        );

        log.info("리뷰 수정: {} by {}", review.getId(), user.getEmail());
        return convertToReviewResponse(review, user.getId());
    }

    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND.getCode(), ErrorCode.REVIEW_NOT_FOUND.getMessage()));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage()));
        
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED.getCode(), ErrorCode.REVIEW_ACCESS_DENIED.getMessage());
        }

        review.delete();
        log.info("리뷰 삭제: {} by {}", review.getId(), user.getEmail());
    }

    public Page<ReviewResponse> getReviews(Pageable pageable, String userEmail) {
        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return reviewRepository.findByStatusOrderByCreatedAtDesc(ReviewStatus.PUBLISHED, pageable)
                .map(review -> convertToReviewResponse(review, userId));
    }

    public Page<ReviewResponse> getReviewsByModel(Long modelId, Pageable pageable, String userEmail) {
        AIModel model = modelRepository.findById(modelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODEL_NOT_FOUND.getCode(), ErrorCode.MODEL_NOT_FOUND.getMessage()));

        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return reviewRepository.findByModelAndStatusOrderByCreatedAtDesc(model, ReviewStatus.PUBLISHED, pageable)
                .map(review -> convertToReviewResponse(review, userId));
    }

    public Page<ReviewResponse> getReviewsByUser(Long userId, Pageable pageable, String currentUserEmail) {
        Long currentUserId = currentUserEmail != null ? 
                userRepository.findByEmail(currentUserEmail).map(User::getId).orElse(null) : null;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage()));
        
        return reviewRepository.findByUserAndStatusOrderByCreatedAtDesc(user, ReviewStatus.PUBLISHED, pageable)
                .map(review -> convertToReviewResponse(review, currentUserId));
    }

    public Page<ReviewResponse> searchReviews(String keyword, Pageable pageable, String userEmail) {
        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return reviewRepository.searchByKeyword(keyword, pageable)
                .map(review -> convertToReviewResponse(review, userId));
    }

    public Page<ReviewResponse> getPopularReviews(Pageable pageable, String userEmail) {
        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return reviewRepository.findPopularReviews(pageable)
                .map(review -> convertToReviewResponse(review, userId));
    }

    private ReviewResponse convertToReviewResponse(Review review, Long currentUserId) {
        Boolean isLiked = false;
        Boolean isBookmarked = false;
        
        if (currentUserId != null) {
            User currentUser = userRepository.findById(currentUserId).orElse(null);
            if (currentUser != null) {
                isLiked = likeRepository.existsByUserAndTypeAndTargetId(currentUser, LikeType.REVIEW, review.getId());
                isBookmarked = bookmarkRepository.existsByUserAndTypeAndTargetId(currentUser, BookmarkType.REVIEW, review.getId());
            }
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .modelId(review.getModel().getId())
                .modelName(review.getModel().getName())
                .modelProvider(review.getModel().getProvider())
                .authorId(review.getUser().getId())
                .authorNickname(review.getUser().getNickname())
                .authorProfileImage(review.getUser().getProfileImage())
                .title(review.getTitle())
                .content(review.getContent())
                .rating(review.getRating())
                .useCase(review.getUseCase())
                .inputExample(review.getInputExample())
                .outputExample(review.getOutputExample())
                .tags(review.getTags())
                .screenshotUrl(review.getScreenshotUrl())
                .viewCount(review.getViewCount())
                .likeCount(review.getLikeCount())
                .commentCount(review.getCommentCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();
    }
}