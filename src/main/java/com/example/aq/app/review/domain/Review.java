package com.example.aq.app.review.domain;

import com.example.aq.app.model.domain.AIModel;
import com.example.aq.app.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private AIModel model;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Size(max = 5000)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Size(max = 100)
    private String useCase;

    @Size(max = 2000)
    @Column(name = "input_example", columnDefinition = "TEXT")
    private String inputExample;

    @Size(max = 2000)
    @Column(name = "output_example", columnDefinition = "TEXT")
    private String outputExample;

    @ElementCollection
    @CollectionTable(name = "review_tags", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(name = "screenshot_url")
    private String screenshotUrl;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(nullable = false)
    private Boolean active = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Review(AIModel model, User author, String title, String content, Integer rating,
                  String useCase, String inputExample, String outputExample, List<String> tags,
                  String screenshotUrl) {
        this.model = model;
        this.author = author;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.useCase = useCase;
        this.inputExample = inputExample;
        this.outputExample = outputExample;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.screenshotUrl = screenshotUrl;
    }

    // 비즈니스 메서드
    public void updateReview(String title, String content, Integer rating, String useCase,
                            String inputExample, String outputExample, List<String> tags,
                            String screenshotUrl) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        if (content != null && !content.trim().isEmpty()) {
            this.content = content.trim();
        }
        if (rating != null && rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
        this.useCase = useCase;
        this.inputExample = inputExample;
        this.outputExample = outputExample;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.screenshotUrl = screenshotUrl;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
    }

    public void feature() {
        this.isFeatured = true;
    }

    public void unfeature() {
        this.isFeatured = false;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    // 편의 메서드
    public String getModelName() {
        return model.getName();
    }

    public String getModelProvider() {
        return model.getProvider();
    }

    public String getAuthorNickname() {
        return author.getNickname();
    }

    public String getAuthorProfileImage() {
        return author.getProfileImageUrl();
    }
}
