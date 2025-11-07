package com.example.aq.app.recipe.service;

import com.example.aq.common.dto.PageResponse;
import com.example.aq.common.exception.ResourceNotFoundException;
import com.example.aq.common.exception.UnauthorizedException;
import com.example.aq.common.util.SecurityUtil;
import com.example.aq.app.recipe.domain.Recipe;
import com.example.aq.app.recipe.domain.RecipeCategory;
import com.example.aq.app.recipe.dto.CreateRecipeRequest;
import com.example.aq.app.recipe.dto.RecipeResponse;
import com.example.aq.app.recipe.dto.UpdateRecipeRequest;
import com.example.aq.app.recipe.repository.RecipeRepository;
import com.example.aq.app.user.domain.User;
import com.example.aq.app.user.repository.UserRepository;
import com.example.aq.app.interaction.repository.LikeRepository;
import com.example.aq.app.interaction.repository.BookmarkRepository;
import com.example.aq.app.interaction.domain.LikeType;
import com.example.aq.app.interaction.domain.BookmarkType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;

    public PageResponse<RecipeResponse> getRecipes(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findLatestRecipes(pageable);
        // Lazy 컬렉션 초기화
        recipes.forEach(recipe -> {
            recipe.getTags().size();
            recipe.getSuitableModels().size();
        });
        return PageResponse.of(recipes.map(RecipeResponse::of));
    }

    public PageResponse<RecipeResponse> getPopularRecipes(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findPopularRecipes(pageable);
        // Lazy 컬렉션 초기화
        recipes.forEach(recipe -> {
            recipe.getTags().size();
            recipe.getSuitableModels().size();
        });
        return PageResponse.of(recipes.map(RecipeResponse::of));
    }

    public PageResponse<RecipeResponse> getMostUsedRecipes(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findMostUsedRecipes(pageable);
        // Lazy 컬렉션 초기화
        recipes.forEach(recipe -> {
            recipe.getTags().size();
            recipe.getSuitableModels().size();
        });
        return PageResponse.of(recipes.map(RecipeResponse::of));
    }

    public PageResponse<RecipeResponse> getFeaturedRecipes(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findFeaturedRecipes(pageable);
        // Lazy 컬렉션 초기화
        recipes.forEach(recipe -> {
            recipe.getTags().size();
            recipe.getSuitableModels().size();
        });
        return PageResponse.of(recipes.map(RecipeResponse::of));
    }

    public PageResponse<RecipeResponse> searchRecipes(String keyword, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.searchRecipes(keyword, pageable);
        // Lazy 컬렉션 초기화
        recipes.forEach(recipe -> {
            recipe.getTags().size();
            recipe.getSuitableModels().size();
        });
        return PageResponse.of(recipes.map(RecipeResponse::of));
    }

    public PageResponse<RecipeResponse> getRecipesByCategory(RecipeCategory category, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByCategory(category, pageable);
        // Lazy 컬렉션 초기화
        recipes.forEach(recipe -> {
            recipe.getTags().size();
            recipe.getSuitableModels().size();
        });
        return PageResponse.of(recipes.map(RecipeResponse::of));
    }

    public PageResponse<RecipeResponse> getRecipesByAuthor(Long authorId, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByAuthorId(authorId, pageable);
        // Lazy 컬렉션 초기화
        recipes.forEach(recipe -> {
            recipe.getTags().size();
            recipe.getSuitableModels().size();
        });
        return PageResponse.of(recipes.map(RecipeResponse::of));
    }

    public PageResponse<RecipeResponse> getRecipesByDifficulty(Integer difficulty, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByDifficultyLevel(difficulty, pageable);
        // Lazy 컬렉션 초기화
        recipes.forEach(recipe -> {
            recipe.getTags().size();
            recipe.getSuitableModels().size();
        });
        return PageResponse.of(recipes.map(RecipeResponse::of));
    }

    @Transactional
    public RecipeResponse getRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("레시피", "id", id));
        
        if (!recipe.getActive()) {
            throw new ResourceNotFoundException("레시피", "id", id);
        }
        
        // Lazy 컬렉션 초기화
        recipe.getTags().size();
        recipe.getSuitableModels().size();
        
        // 조회수 증가
        recipe.incrementViewCount();
        
        // 현재 사용자의 좋아요/북마크 상태 확인
        boolean isLiked = false;
        boolean isBookmarked = false;
        
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            User currentUser = userRepository.findById(currentUserId).orElse(null);
            
            if (currentUser != null) {
                isLiked = likeRepository.existsByUserAndTargetIdAndTargetType(
                    currentUser, id, LikeType.RECIPE);
                isBookmarked = bookmarkRepository.existsByUserAndTargetIdAndTargetType(
                    currentUser, id, BookmarkType.RECIPE);
            }
        } catch (Exception e) {
            // 비로그인 사용자의 경우 무시
            log.debug("User not authenticated, returning default interaction states");
        }
        
        return RecipeResponse.of(recipe, isLiked, isBookmarked);
    }

    @Transactional
    public RecipeResponse createRecipe(CreateRecipeRequest request, Long authorId) {
        // 작성자 존재 확인
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", authorId));

        // 레시피 생성
        Recipe recipe = Recipe.builder()
                .author(author)
                .title(request.getTitle())
                .description(request.getDescription())
                .promptTemplate(request.getPromptTemplate())
                .usageInstructions(request.getUsageInstructions())
                .exampleInput(request.getExampleInput())
                .exampleOutput(request.getExampleOutput())
                .category(request.getCategory())
                .tags(request.getTags())
                .suitableModels(request.getSuitableModels())
                .difficultyLevel(request.getDifficultyLevel())
                .estimatedTimeMinutes(request.getEstimatedTimeMinutes())
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);

        log.info("레시피가 생성되었습니다: {} by {}", savedRecipe.getId(), author.getNickname());
        return RecipeResponse.of(savedRecipe);
    }

    @Transactional
    public RecipeResponse updateRecipe(Long id, UpdateRecipeRequest request, Long userId) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("레시피", "id", id));

        if (!recipe.getActive()) {
            throw new ResourceNotFoundException("레시피", "id", id);
        }

        // 작성자 확인
        if (!recipe.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("레시피를 수정할 권한이 없습니다");
        }

        // 레시피 업데이트
        recipe.updateRecipe(
                request.getTitle(),
                request.getDescription(),
                request.getPromptTemplate(),
                request.getUsageInstructions(),
                request.getExampleInput(),
                request.getExampleOutput(),
                request.getCategory(),
                request.getTags(),
                request.getSuitableModels(),
                request.getDifficultyLevel(),
                request.getEstimatedTimeMinutes()
        );

        Recipe savedRecipe = recipeRepository.save(recipe);

        log.info("레시피가 수정되었습니다: {}", savedRecipe.getId());
        return RecipeResponse.of(savedRecipe);
    }

    @Transactional
    public void deleteRecipe(Long id, Long userId) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("레시피", "id", id));

        // 작성자 확인
        if (!recipe.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("레시피를 삭제할 권한이 없습니다");
        }

        // 레시피 비활성화
        recipe.deactivate();
        recipeRepository.save(recipe);

        log.info("레시피가 삭제되었습니다: {}", id);
    }

    @Transactional
    public RecipeResponse incrementUseCount(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("레시피", "id", id));
        
        if (!recipe.getActive()) {
            throw new ResourceNotFoundException("레시피", "id", id);
        }
        
        recipe.incrementUseCount();
        recipeRepository.save(recipe);
        
        return RecipeResponse.of(recipe);
    }
}

