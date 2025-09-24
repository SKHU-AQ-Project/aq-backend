package com.example.aq.domain.review;

import com.example.aq.domain.model.AIModel;
import com.example.aq.domain.user.User;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private AIModel model;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    private String useCase;

    @Column(columnDefinition = "TEXT")
    private String inputExample;

    @Column(columnDefinition = "TEXT")
    private String outputExample;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "review_tags", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    private String screenshotUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.PUBLISHED;

    private Integer viewCount = 0;

    private Integer likeCount = 0;

    private Integer commentCount = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Review(User user, AIModel model, String title, String content, Integer rating, 
                  String useCase, String inputExample, String outputExample, List<String> tags, String screenshotUrl) {
        this.user = user;
        this.model = model;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.useCase = useCase;
        this.inputExample = inputExample;
        this.outputExample = outputExample;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.screenshotUrl = screenshotUrl;
    }

    public void updateReview(String title, String content, Integer rating, String useCase, 
                           String inputExample, String outputExample, List<String> tags, String screenshotUrl) {
        this.title = title;
        this.content = content;
        this.rating = rating;
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

    public void delete() {
        this.status = ReviewStatus.DELETED;
    }
}
