package com.example.aq.recipe.dto;

import com.example.aq.domain.recipe.RecipeCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponse {

    private Long id;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImage;
    private Long recommendedModelId;
    private String recommendedModelName;
    private String recommendedModelProvider;
    private String title;
    private String description;
    private String promptTemplate;
    private String expectedOutput;
    private String usageInstructions;
    private List<String> tags;
    private List<String> steps;
    private RecipeCategory category;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer usageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isLiked;
    private Boolean isBookmarked;
}
