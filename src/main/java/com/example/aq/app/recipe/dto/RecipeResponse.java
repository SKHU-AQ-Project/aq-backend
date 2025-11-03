package com.example.aq.app.recipe.dto;

import com.example.aq.app.recipe.domain.Recipe;
import com.example.aq.app.recipe.domain.RecipeCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class RecipeResponse {
    private Long id;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImage;
    private String title;
    private String description;
    private String promptTemplate;
    private String usageInstructions;
    private String exampleInput;
    private String exampleOutput;
    private RecipeCategory category;
    private List<String> tags;
    private List<String> suitableModels;
    private Integer difficultyLevel;
    private Integer estimatedTimeMinutes;
    private Integer viewCount;
    private Integer likeCount;
    private Integer bookmarkCount;
    private Integer useCount;
    private Boolean isFeatured;
    private Boolean isVerified;
    private Boolean isLiked;
    private Boolean isBookmarked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public RecipeResponse(Long id, Long authorId, String authorNickname, String authorProfileImage,
                         String title, String description, String promptTemplate, String usageInstructions,
                         String exampleInput, String exampleOutput, RecipeCategory category, List<String> tags,
                         List<String> suitableModels, Integer difficultyLevel, Integer estimatedTimeMinutes,
                         Integer viewCount, Integer likeCount, Integer bookmarkCount, Integer useCount,
                         Boolean isFeatured, Boolean isVerified, Boolean isLiked, Boolean isBookmarked,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.authorId = authorId;
        this.authorNickname = authorNickname;
        this.authorProfileImage = authorProfileImage;
        this.title = title;
        this.description = description;
        this.promptTemplate = promptTemplate;
        this.usageInstructions = usageInstructions;
        this.exampleInput = exampleInput;
        this.exampleOutput = exampleOutput;
        this.category = category;
        this.tags = tags;
        this.suitableModels = suitableModels;
        this.difficultyLevel = difficultyLevel;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.bookmarkCount = bookmarkCount;
        this.useCount = useCount;
        this.isFeatured = isFeatured;
        this.isVerified = isVerified;
        this.isLiked = isLiked;
        this.isBookmarked = isBookmarked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RecipeResponse of(Recipe recipe) {
        return RecipeResponse.builder()
                .id(recipe.getId())
                .authorId(recipe.getAuthor().getId())
                .authorNickname(recipe.getAuthorNickname())
                .authorProfileImage(recipe.getAuthorProfileImage())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .promptTemplate(recipe.getPromptTemplate())
                .usageInstructions(recipe.getUsageInstructions())
                .exampleInput(recipe.getExampleInput())
                .exampleOutput(recipe.getExampleOutput())
                .category(recipe.getCategory())
                .tags(recipe.getTags())
                .suitableModels(recipe.getSuitableModels())
                .difficultyLevel(recipe.getDifficultyLevel())
                .estimatedTimeMinutes(recipe.getEstimatedTimeMinutes())
                .viewCount(recipe.getViewCount())
                .likeCount(recipe.getLikeCount())
                .bookmarkCount(recipe.getBookmarkCount())
                .useCount(recipe.getUseCount())
                .isFeatured(recipe.getIsFeatured())
                .isVerified(recipe.getIsVerified())
                .isLiked(false) // TODO: 실제 사용자 좋아요 상태 확인
                .isBookmarked(false) // TODO: 실제 사용자 북마크 상태 확인
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .build();
    }
}

