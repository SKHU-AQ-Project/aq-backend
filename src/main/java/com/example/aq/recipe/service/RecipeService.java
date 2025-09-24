package com.example.aq.recipe.service;

import com.example.aq.domain.interaction.BookmarkRepository;
import com.example.aq.domain.interaction.BookmarkType;
import com.example.aq.domain.interaction.LikeRepository;
import com.example.aq.domain.interaction.LikeType;
import com.example.aq.domain.model.AIModel;
import com.example.aq.domain.model.AIModelRepository;
import com.example.aq.domain.recipe.PromptRecipe;
import com.example.aq.domain.recipe.PromptRecipeRepository;
import com.example.aq.domain.recipe.RecipeStatus;
import com.example.aq.domain.user.User;
import com.example.aq.domain.user.UserRepository;
import com.example.aq.recipe.dto.CreateRecipeRequest;
import com.example.aq.recipe.dto.RecipeResponse;
import com.example.aq.recipe.dto.UpdateRecipeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final PromptRecipeRepository recipeRepository;
    private final AIModelRepository modelRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public RecipeResponse createRecipe(String userEmail, CreateRecipeRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        AIModel recommendedModel = null;
        if (request.getRecommendedModelId() != null) {
            recommendedModel = modelRepository.findById(request.getRecommendedModelId())
                    .orElseThrow(() -> new RuntimeException("추천 모델을 찾을 수 없습니다"));
        }

        PromptRecipe recipe = PromptRecipe.builder()
                .user(user)
                .recommendedModel(recommendedModel)
                .title(request.getTitle())
                .description(request.getDescription())
                .promptTemplate(request.getPromptTemplate())
                .expectedOutput(request.getExpectedOutput())
                .usageInstructions(request.getUsageInstructions())
                .tags(request.getTags())
                .steps(request.getSteps())
                .category(request.getCategory())
                .build();

        PromptRecipe savedRecipe = recipeRepository.save(recipe);
        user.addPoints(15); // 레시피 작성 시 15점 추가

        log.info("새 레시피 작성: {} by {}", savedRecipe.getId(), user.getEmail());
        return convertToRecipeResponse(savedRecipe, user.getId());
    }

    public RecipeResponse getRecipe(Long recipeId, String userEmail) {
        PromptRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다"));

        if (recipe.getStatus() != RecipeStatus.PUBLISHED) {
            throw new RuntimeException("삭제된 레시피입니다");
        }

        recipe.incrementViewCount();
        recipeRepository.save(recipe);

        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return convertToRecipeResponse(recipe, userId);
    }

    @Transactional
    public RecipeResponse updateRecipe(Long recipeId, String userEmail, UpdateRecipeRequest request) {
        PromptRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        if (!recipe.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("레시피를 수정할 권한이 없습니다");
        }

        AIModel recommendedModel = null;
        if (request.getRecommendedModelId() != null) {
            recommendedModel = modelRepository.findById(request.getRecommendedModelId())
                    .orElseThrow(() -> new RuntimeException("추천 모델을 찾을 수 없습니다"));
        }

        recipe.updateRecipe(
                request.getTitle(),
                request.getDescription(),
                request.getPromptTemplate(),
                request.getExpectedOutput(),
                request.getUsageInstructions(),
                request.getTags(),
                request.getSteps(),
                request.getCategory()
        );

        log.info("레시피 수정: {} by {}", recipe.getId(), user.getEmail());
        return convertToRecipeResponse(recipe, user.getId());
    }

    @Transactional
    public void deleteRecipe(Long recipeId, String userEmail) {
        PromptRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        if (!recipe.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("레시피를 삭제할 권한이 없습니다");
        }

        recipe.delete();
        log.info("레시피 삭제: {} by {}", recipe.getId(), user.getEmail());
    }

    @Transactional
    public void incrementUsageCount(Long recipeId) {
        PromptRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다"));
        recipe.incrementUsageCount();
    }

    public Page<RecipeResponse> getRecipes(Pageable pageable, String userEmail) {
        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return recipeRepository.findByStatusOrderByCreatedAtDesc(RecipeStatus.PUBLISHED, pageable)
                .map(recipe -> convertToRecipeResponse(recipe, userId));
    }

    public Page<RecipeResponse> getRecipesByCategory(String category, Pageable pageable, String userEmail) {
        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return recipeRepository.findByCategoryAndStatusOrderByCreatedAtDesc(
                com.example.aq.domain.recipe.RecipeCategory.valueOf(category.toUpperCase()), 
                RecipeStatus.PUBLISHED, pageable)
                .map(recipe -> convertToRecipeResponse(recipe, userId));
    }

    public Page<RecipeResponse> getRecipesByUser(Long userId, Pageable pageable, String currentUserEmail) {
        Long currentUserId = currentUserEmail != null ? 
                userRepository.findByEmail(currentUserEmail).map(User::getId).orElse(null) : null;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        return recipeRepository.findByUserAndStatusOrderByCreatedAtDesc(user, RecipeStatus.PUBLISHED, pageable)
                .map(recipe -> convertToRecipeResponse(recipe, currentUserId));
    }

    public Page<RecipeResponse> searchRecipes(String keyword, Pageable pageable, String userEmail) {
        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return recipeRepository.searchByKeyword(keyword, pageable)
                .map(recipe -> convertToRecipeResponse(recipe, userId));
    }

    public Page<RecipeResponse> getPopularRecipes(Pageable pageable, String userEmail) {
        Long userId = userEmail != null ? 
                userRepository.findByEmail(userEmail).map(User::getId).orElse(null) : null;
        return recipeRepository.findPopularRecipes(pageable)
                .map(recipe -> convertToRecipeResponse(recipe, userId));
    }

    private RecipeResponse convertToRecipeResponse(PromptRecipe recipe, Long currentUserId) {
        Boolean isLiked = false;
        Boolean isBookmarked = false;
        
        if (currentUserId != null) {
            User currentUser = userRepository.findById(currentUserId).orElse(null);
            if (currentUser != null) {
                isLiked = likeRepository.existsByUserAndTypeAndTargetId(currentUser, LikeType.RECIPE, recipe.getId());
                isBookmarked = bookmarkRepository.existsByUserAndTypeAndTargetId(currentUser, BookmarkType.RECIPE, recipe.getId());
            }
        }

        return RecipeResponse.builder()
                .id(recipe.getId())
                .authorId(recipe.getUser().getId())
                .authorNickname(recipe.getUser().getNickname())
                .authorProfileImage(recipe.getUser().getProfileImage())
                .recommendedModelId(recipe.getRecommendedModel() != null ? recipe.getRecommendedModel().getId() : null)
                .recommendedModelName(recipe.getRecommendedModel() != null ? recipe.getRecommendedModel().getName() : null)
                .recommendedModelProvider(recipe.getRecommendedModel() != null ? recipe.getRecommendedModel().getProvider() : null)
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .promptTemplate(recipe.getPromptTemplate())
                .expectedOutput(recipe.getExpectedOutput())
                .usageInstructions(recipe.getUsageInstructions())
                .tags(recipe.getTags())
                .steps(recipe.getSteps())
                .category(recipe.getCategory())
                .viewCount(recipe.getViewCount())
                .likeCount(recipe.getLikeCount())
                .commentCount(recipe.getCommentCount())
                .usageCount(recipe.getUsageCount())
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();
    }
}
