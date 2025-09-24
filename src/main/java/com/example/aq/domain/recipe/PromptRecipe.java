package com.example.aq.domain.recipe;

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
@Table(name = "prompt_recipes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PromptRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_model_id")
    private AIModel recommendedModel;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String promptTemplate;

    @Column(columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(columnDefinition = "TEXT")
    private String usageInstructions;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @OrderColumn(name = "step_order")
    @Column(name = "step", columnDefinition = "TEXT")
    private List<String> steps = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeStatus status = RecipeStatus.PUBLISHED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeCategory category;

    private Integer viewCount = 0;

    private Integer likeCount = 0;

    private Integer commentCount = 0;

    private Integer usageCount = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public PromptRecipe(User user, AIModel recommendedModel, String title, String description, 
                       String promptTemplate, String expectedOutput, String usageInstructions,
                       List<String> tags, List<String> steps, RecipeCategory category) {
        this.user = user;
        this.recommendedModel = recommendedModel;
        this.title = title;
        this.description = description;
        this.promptTemplate = promptTemplate;
        this.expectedOutput = expectedOutput;
        this.usageInstructions = usageInstructions;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.steps = steps != null ? steps : new ArrayList<>();
        this.category = category;
    }

    public void updateRecipe(String title, String description, String promptTemplate, 
                           String expectedOutput, String usageInstructions, List<String> tags, 
                           List<String> steps, RecipeCategory category) {
        this.title = title;
        this.description = description;
        this.promptTemplate = promptTemplate;
        this.expectedOutput = expectedOutput;
        this.usageInstructions = usageInstructions;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.steps = steps != null ? steps : new ArrayList<>();
        this.category = category;
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

    public void incrementUsageCount() {
        this.usageCount++;
    }

    public void delete() {
        this.status = RecipeStatus.DELETED;
    }
}
