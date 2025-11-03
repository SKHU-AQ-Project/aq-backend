package com.example.aq.app.recipe.domain;

import com.example.aq.app.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "recipes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private String description;

    @NotBlank
    @Size(max = 2000)
    @Column(name = "prompt_template", nullable = false, columnDefinition = "TEXT")
    private String promptTemplate;

    @Size(max = 1000)
    @Column(name = "usage_instructions", columnDefinition = "TEXT")
    private String usageInstructions;

    @Size(max = 2000)
    @Column(name = "example_input", columnDefinition = "TEXT")
    private String exampleInput;

    @Size(max = 2000)
    @Column(name = "example_output", columnDefinition = "TEXT")
    private String exampleOutput;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeCategory category;

    @ElementCollection
    @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "recipe_suitable_models", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "model_name")
    private List<String> suitableModels = new ArrayList<>();

    @Column(name = "difficulty_level", nullable = false)
    private Integer difficultyLevel = 1; // 1-5

    @Column(name = "estimated_time_minutes")
    private Integer estimatedTimeMinutes;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "bookmark_count", nullable = false)
    private Integer bookmarkCount = 0;

    @Column(name = "use_count", nullable = false)
    private Integer useCount = 0;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(nullable = false)
    private Boolean active = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Recipe(User author, String title, String description, String promptTemplate,
                  String usageInstructions, String exampleInput, String exampleOutput,
                  RecipeCategory category, List<String> tags, List<String> suitableModels,
                  Integer difficultyLevel, Integer estimatedTimeMinutes) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.promptTemplate = promptTemplate;
        this.usageInstructions = usageInstructions;
        this.exampleInput = exampleInput;
        this.exampleOutput = exampleOutput;
        this.category = category;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.suitableModels = suitableModels != null ? suitableModels : new ArrayList<>();
        this.difficultyLevel = difficultyLevel != null ? difficultyLevel : 1;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    // 비즈니스 메서드
    public void updateRecipe(String title, String description, String promptTemplate,
                            String usageInstructions, String exampleInput, String exampleOutput,
                            RecipeCategory category, List<String> tags, List<String> suitableModels,
                            Integer difficultyLevel, Integer estimatedTimeMinutes) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        if (description != null && !description.trim().isEmpty()) {
            this.description = description.trim();
        }
        if (promptTemplate != null && !promptTemplate.trim().isEmpty()) {
            this.promptTemplate = promptTemplate.trim();
        }
        this.usageInstructions = usageInstructions;
        this.exampleInput = exampleInput;
        this.exampleOutput = exampleOutput;
        if (category != null) {
            this.category = category;
        }
        this.tags = tags != null ? tags : new ArrayList<>();
        this.suitableModels = suitableModels != null ? suitableModels : new ArrayList<>();
        if (difficultyLevel != null && difficultyLevel >= 1 && difficultyLevel <= 5) {
            this.difficultyLevel = difficultyLevel;
        }
        this.estimatedTimeMinutes = estimatedTimeMinutes;
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

    public void incrementBookmarkCount() {
        this.bookmarkCount++;
    }

    public void decrementBookmarkCount() {
        this.bookmarkCount = Math.max(0, this.bookmarkCount - 1);
    }

    public void incrementUseCount() {
        this.useCount++;
    }

    public void feature() {
        this.isFeatured = true;
    }

    public void unfeature() {
        this.isFeatured = false;
    }

    public void verify() {
        this.isVerified = true;
    }

    public void unverify() {
        this.isVerified = false;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    // 편의 메서드
    public String getAuthorNickname() {
        return author.getNickname();
    }

    public String getAuthorProfileImage() {
        return author.getProfileImageUrl();
    }
}
