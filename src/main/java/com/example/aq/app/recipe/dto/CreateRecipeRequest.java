package com.example.aq.app.recipe.dto;

import com.example.aq.app.recipe.domain.RecipeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateRecipeRequest {
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotBlank(message = "설명은 필수입니다")
    @Size(max = 5000, message = "설명은 5000자 이하여야 합니다")
    private String description;

    @NotBlank(message = "프롬프트 템플릿은 필수입니다")
    @Size(max = 2000, message = "프롬프트 템플릿은 2000자 이하여야 합니다")
    private String promptTemplate;

    @Size(max = 1000, message = "사용 설명은 1000자 이하여야 합니다")
    private String usageInstructions;

    @Size(max = 2000, message = "예시 입력은 2000자 이하여야 합니다")
    private String exampleInput;

    @Size(max = 2000, message = "예시 출력은 2000자 이하여야 합니다")
    private String exampleOutput;

    @NotNull(message = "카테고리는 필수입니다")
    private RecipeCategory category;

    private List<String> tags;

    private List<String> suitableModels;

    private Integer difficultyLevel; // 1-5

    private Integer estimatedTimeMinutes;
}

