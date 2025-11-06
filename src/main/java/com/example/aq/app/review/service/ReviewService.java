package com.example.aq.app.review.service;

import com.example.aq.common.dto.PageResponse;
import com.example.aq.common.exception.ResourceNotFoundException;
import com.example.aq.common.exception.UnauthorizedException;
import com.example.aq.app.model.domain.AIModel;
import com.example.aq.app.model.repository.AIModelRepository;
import com.example.aq.app.review.domain.Review;
import com.example.aq.app.review.repository.ReviewRepository;
import com.example.aq.app.user.domain.User;
import com.example.aq.app.user.repository.UserRepository;
import com.example.aq.app.review.dto.CreateReviewRequest;
import com.example.aq.app.review.dto.ReviewResponse;
import com.example.aq.app.review.dto.UpdateReviewRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AIModelRepository aiModelRepository;
    private final UserRepository userRepository;

    public PageResponse<ReviewResponse> getReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findLatestReviews(pageable);
        // Lazy 컬렉션 초기화
        reviews.forEach(review -> review.getTags().size());
        return PageResponse.of(reviews.map(ReviewResponse::of));
    }

    public PageResponse<ReviewResponse> getPopularReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findPopularReviews(pageable);
        // Lazy 컬렉션 초기화
        reviews.forEach(review -> review.getTags().size());
        return PageResponse.of(reviews.map(ReviewResponse::of));
    }

    public PageResponse<ReviewResponse> getTopRatedReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findTopRatedReviews(pageable);
        // Lazy 컬렉션 초기화
        reviews.forEach(review -> review.getTags().size());
        return PageResponse.of(reviews.map(ReviewResponse::of));
    }

    public PageResponse<ReviewResponse> searchReviews(String keyword, Pageable pageable) {
        Page<Review> reviews = reviewRepository.searchReviews(keyword, pageable);
        // Lazy 컬렉션 초기화
        reviews.forEach(review -> review.getTags().size());
        return PageResponse.of(reviews.map(ReviewResponse::of));
    }

    public PageResponse<ReviewResponse> getReviewsByModel(Long modelId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByModelId(modelId, pageable);
        // Lazy 컬렉션 초기화
        reviews.forEach(review -> review.getTags().size());
        return PageResponse.of(reviews.map(ReviewResponse::of));
    }

    public PageResponse<ReviewResponse> getReviewsByAuthor(Long authorId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByAuthorId(authorId, pageable);
        // Lazy 컬렉션 초기화
        reviews.forEach(review -> review.getTags().size());
        return PageResponse.of(reviews.map(ReviewResponse::of));
    }

    @Transactional
    public ReviewResponse getReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("리뷰", "id", id));
        
        // Lazy 컬렉션 초기화
        review.getTags().size();
        
        // 조회수 증가
        review.incrementViewCount();
        // save 호출 제거 - @Transactional로 자동 반영
        
        return ReviewResponse.of(review);
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request, Long authorId) {
        // 모델 존재 확인
        AIModel model = aiModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException("AI 모델", "id", request.getModelId()));

        // 작성자 존재 확인
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", authorId));

        // 리뷰 생성
        Review review = Review.builder()
                .model(model)
                .author(author)
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

        // 모델의 리뷰 수 증가
        model.incrementReviewCount();
        aiModelRepository.save(model);

        // 모델의 평균 평점 업데이트
        updateModelAverageRating(model);

        log.info("리뷰가 생성되었습니다: {} by {}", savedReview.getId(), author.getNickname());
        return ReviewResponse.of(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, UpdateReviewRequest request, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("리뷰", "id", id));

        // 작성자 확인
        if (!review.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("리뷰를 수정할 권한이 없습니다");
        }

        // 리뷰 업데이트
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

        Review savedReview = reviewRepository.save(review);

        // 모델의 평균 평점 업데이트
        updateModelAverageRating(review.getModel());

        log.info("리뷰가 수정되었습니다: {}", savedReview.getId());
        return ReviewResponse.of(savedReview);
    }

    @Transactional
    public void deleteReview(Long id, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("리뷰", "id", id));

        // 작성자 확인
        if (!review.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("리뷰를 삭제할 권한이 없습니다");
        }

        // 리뷰 비활성화
        review.deactivate();
        reviewRepository.save(review);

        // 모델의 리뷰 수 감소
        AIModel model = review.getModel();
        model.decrementReviewCount();
        aiModelRepository.save(model);

        // 모델의 평균 평점 업데이트
        updateModelAverageRating(model);

        log.info("리뷰가 삭제되었습니다: {}", id);
    }

    private void updateModelAverageRating(AIModel model) {
        List<Review> activeReviews = reviewRepository.findByModel(model, Pageable.unpaged()).getContent();
        
        if (activeReviews.isEmpty()) {
            model.updateRating(null);
        } else {
            double averageRating = activeReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            
            BigDecimal roundedRating = BigDecimal.valueOf(averageRating)
                    .setScale(2, RoundingMode.HALF_UP);
            
            model.updateRating(roundedRating);
        }
        
        aiModelRepository.save(model);
    }
}
