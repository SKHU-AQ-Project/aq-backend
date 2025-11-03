package com.example.aq.app.review.dto;

import com.example.aq.app.review.domain.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long modelId;
    private String modelName;
    private String modelProvider;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImage;
    private String title;
    private String content;
    private Integer rating;
    private String useCase;
    private String inputExample;
    private String outputExample;
    private List<String> tags;
    private String screenshotUrl;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLiked;
    private Boolean isBookmarked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ReviewResponse(Long id, Long modelId, String modelName, String modelProvider,
                         Long authorId, String authorNickname, String authorProfileImage,
                         String title, String content, Integer rating, String useCase,
                         String inputExample, String outputExample, List<String> tags,
                         String screenshotUrl, Integer viewCount, Integer likeCount,
                         Integer commentCount, Boolean isLiked, Boolean isBookmarked,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.modelId = modelId;
        this.modelName = modelName;
        this.modelProvider = modelProvider;
        this.authorId = authorId;
        this.authorNickname = authorNickname;
        this.authorProfileImage = authorProfileImage;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.useCase = useCase;
        this.inputExample = inputExample;
        this.outputExample = outputExample;
        this.tags = tags;
        this.screenshotUrl = screenshotUrl;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
        this.isBookmarked = isBookmarked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .modelId(review.getModel().getId())
                .modelName(review.getModelName())
                .modelProvider(review.getModelProvider())
                .authorId(review.getAuthor().getId())
                .authorNickname(review.getAuthorNickname())
                .authorProfileImage(review.getAuthorProfileImage())
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
                .isLiked(false) // TODO: 실제 사용자 좋아요 상태 확인
                .isBookmarked(false) // TODO: 실제 사용자 북마크 상태 확인
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
