package com.example.aq.recipe.dto;

import com.example.aq.domain.recipe.RecipeCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecipeRequest {

    private Long recommendedModelId;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotBlank(message = "설명은 필수입니다")
    @Size(max = 2000, message = "설명은 2000자 이하여야 합니다")
    private String description;

    @NotBlank(message = "프롬프트 템플릿은 필수입니다")
    @Size(max = 10000, message = "프롬프트 템플릿은 10000자 이하여야 합니다")
    private String promptTemplate;

    @Size(max = 2000, message = "기대 출력은 2000자 이하여야 합니다")
    private String expectedOutput;

    @Size(max = 1000, message = "사용 방법은 1000자 이하여야 합니다")
    private String usageInstructions;

    private List<String> tags;

    private List<String> steps;

    @NotNull(message = "카테고리는 필수입니다")
    private RecipeCategory category;
}
