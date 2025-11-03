package com.example.aq.app.recipe.controller;

import com.example.aq.common.dto.BaseResponse;
import com.example.aq.common.dto.PageResponse;
import com.example.aq.app.recipe.domain.RecipeCategory;
import com.example.aq.app.recipe.dto.CreateRecipeRequest;
import com.example.aq.app.recipe.dto.RecipeResponse;
import com.example.aq.app.recipe.dto.UpdateRecipeRequest;
import com.example.aq.app.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "레시피", description = "AI 프롬프트 레시피 관련 API")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    @Operation(summary = "레시피 목록 조회", description = "최신순으로 레시피 목록을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<RecipeResponse>>> getRecipes(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<RecipeResponse> response = recipeService.getRecipes(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 레시피 조회", description = "좋아요 수 기준으로 인기 레시피를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<RecipeResponse>>> getPopularRecipes(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount"));
        PageResponse<RecipeResponse> response = recipeService.getPopularRecipes(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/most-used")
    @Operation(summary = "가장 많이 사용된 레시피 조회", description = "사용 횟수 기준으로 레시피를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<RecipeResponse>>> getMostUsedRecipes(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "useCount"));
        PageResponse<RecipeResponse> response = recipeService.getMostUsedRecipes(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/featured")
    @Operation(summary = "추천 레시피 조회", description = "추천된 레시피를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<RecipeResponse>>> getFeaturedRecipes(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<RecipeResponse> response = recipeService.getFeaturedRecipes(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "레시피 검색", description = "키워드로 레시피를 검색합니다")
    public ResponseEntity<BaseResponse<PageResponse<RecipeResponse>>> searchRecipes(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<RecipeResponse> response = recipeService.searchRecipes(keyword, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 레시피 조회", description = "카테고리별로 레시피를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<RecipeResponse>>> getRecipesByCategory(
            @Parameter(description = "카테고리") @PathVariable RecipeCategory category,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<RecipeResponse> response = recipeService.getRecipesByCategory(category, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 레시피 조회", description = "특정 사용자가 작성한 레시피를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<RecipeResponse>>> getRecipesByUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<RecipeResponse> response = recipeService.getRecipesByAuthor(userId, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/difficulty/{difficulty}")
    @Operation(summary = "난이도별 레시피 조회", description = "난이도별로 레시피를 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<RecipeResponse>>> getRecipesByDifficulty(
            @Parameter(description = "난이도 (1-5)") @PathVariable Integer difficulty,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<RecipeResponse> response = recipeService.getRecipesByDifficulty(difficulty, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "레시피 상세 조회", description = "특정 레시피의 상세 정보를 조회합니다")
    public ResponseEntity<BaseResponse<RecipeResponse>> getRecipe(
            @Parameter(description = "레시피 ID") @PathVariable Long id) {
        
        RecipeResponse response = recipeService.getRecipe(id);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "레시피 작성", description = "새로운 레시피를 작성합니다")
    public ResponseEntity<BaseResponse<RecipeResponse>> createRecipe(
            @Valid @RequestBody CreateRecipeRequest request) {
        
        // TODO: 현재 로그인한 사용자 ID를 가져오는 로직 구현 필요
        Long currentUserId = 1L; // 임시 값
        
        RecipeResponse response = recipeService.createRecipe(request, currentUserId);
        return ResponseEntity.ok(BaseResponse.success("레시피가 성공적으로 작성되었습니다", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "레시피 수정", description = "기존 레시피를 수정합니다")
    public ResponseEntity<BaseResponse<RecipeResponse>> updateRecipe(
            @Parameter(description = "레시피 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateRecipeRequest request) {
        
        // TODO: 현재 로그인한 사용자 ID를 가져오는 로직 구현 필요
        Long currentUserId = 1L; // 임시 값
        
        RecipeResponse response = recipeService.updateRecipe(id, request, currentUserId);
        return ResponseEntity.ok(BaseResponse.success("레시피가 성공적으로 수정되었습니다", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "레시피 삭제", description = "레시피를 삭제합니다")
    public ResponseEntity<BaseResponse<Void>> deleteRecipe(
            @Parameter(description = "레시피 ID") @PathVariable Long id) {
        
        // TODO: 현재 로그인한 사용자 ID를 가져오는 로직 구현 필요
        Long currentUserId = 1L; // 임시 값
        
        recipeService.deleteRecipe(id, currentUserId);
        return ResponseEntity.ok(BaseResponse.success("레시피가 성공적으로 삭제되었습니다", null));
    }

    @PostMapping("/{id}/use")
    @Operation(summary = "레시피 사용 횟수 증가", description = "레시피 사용 횟수를 증가시킵니다")
    public ResponseEntity<BaseResponse<RecipeResponse>> incrementUseCount(
            @Parameter(description = "레시피 ID") @PathVariable Long id) {
        
        RecipeResponse response = recipeService.incrementUseCount(id);
        return ResponseEntity.ok(BaseResponse.success("사용 횟수가 증가되었습니다", response));
    }
}

