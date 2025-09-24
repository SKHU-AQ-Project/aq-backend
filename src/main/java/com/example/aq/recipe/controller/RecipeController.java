package com.example.aq.recipe.controller;

import com.example.aq.recipe.dto.CreateRecipeRequest;
import com.example.aq.recipe.dto.RecipeResponse;
import com.example.aq.recipe.dto.UpdateRecipeRequest;
import com.example.aq.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "프롬프트 레시피", description = "프롬프트 레시피 관련 API")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    @Operation(summary = "레시피 작성", description = "새로운 프롬프트 레시피를 작성합니다")
    public ResponseEntity<RecipeResponse> createRecipe(
            Authentication authentication,
            @Valid @RequestBody CreateRecipeRequest request) {
        String userEmail = authentication.getName();
        RecipeResponse response = recipeService.createRecipe(userEmail, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{recipeId}")
    @Operation(summary = "레시피 조회", description = "특정 레시피의 상세 정보를 조회합니다")
    public ResponseEntity<RecipeResponse> getRecipe(
            @PathVariable Long recipeId,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        RecipeResponse response = recipeService.getRecipe(recipeId, userEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{recipeId}")
    @Operation(summary = "레시피 수정", description = "작성한 레시피를 수정합니다")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long recipeId,
            Authentication authentication,
            @Valid @RequestBody UpdateRecipeRequest request) {
        String userEmail = authentication.getName();
        RecipeResponse response = recipeService.updateRecipe(recipeId, userEmail, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{recipeId}")
    @Operation(summary = "레시피 삭제", description = "작성한 레시피를 삭제합니다")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long recipeId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        recipeService.deleteRecipe(recipeId, userEmail);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{recipeId}/use")
    @Operation(summary = "레시피 사용", description = "레시피 사용 횟수를 증가시킵니다")
    public ResponseEntity<Void> useRecipe(@PathVariable Long recipeId) {
        recipeService.incrementUsageCount(recipeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "레시피 목록 조회", description = "전체 레시피 목록을 조회합니다")
    public ResponseEntity<Page<RecipeResponse>> getRecipes(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<RecipeResponse> response = recipeService.getRecipes(pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 레시피 조회", description = "특정 카테고리의 레시피 목록을 조회합니다")
    public ResponseEntity<Page<RecipeResponse>> getRecipesByCategory(
            @PathVariable String category,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<RecipeResponse> response = recipeService.getRecipesByCategory(category, pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 레시피 조회", description = "특정 사용자의 레시피 목록을 조회합니다")
    public ResponseEntity<Page<RecipeResponse>> getRecipesByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<RecipeResponse> response = recipeService.getRecipesByUser(userId, pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "레시피 검색", description = "키워드로 레시피를 검색합니다")
    public ResponseEntity<Page<RecipeResponse>> searchRecipes(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<RecipeResponse> response = recipeService.searchRecipes(keyword, pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 레시피 조회", description = "인기 레시피 목록을 조회합니다")
    public ResponseEntity<Page<RecipeResponse>> getPopularRecipes(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<RecipeResponse> response = recipeService.getPopularRecipes(pageable, userEmail);
        return ResponseEntity.ok(response);
    }
}
