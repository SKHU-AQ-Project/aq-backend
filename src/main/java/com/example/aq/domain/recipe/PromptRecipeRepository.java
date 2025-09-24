package com.example.aq.domain.recipe;

import com.example.aq.domain.model.AIModel;
import com.example.aq.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromptRecipeRepository extends JpaRepository<PromptRecipe, Long> {

    Page<PromptRecipe> findByStatusOrderByCreatedAtDesc(RecipeStatus status, Pageable pageable);

    Page<PromptRecipe> findByUserAndStatusOrderByCreatedAtDesc(User user, RecipeStatus status, Pageable pageable);

    Page<PromptRecipe> findByCategoryAndStatusOrderByCreatedAtDesc(RecipeCategory category, RecipeStatus status, Pageable pageable);

    @Query("SELECT r FROM PromptRecipe r WHERE r.status = 'PUBLISHED' AND :tag MEMBER OF r.tags ORDER BY r.createdAt DESC")
    Page<PromptRecipe> findByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT r FROM PromptRecipe r WHERE r.status = 'PUBLISHED' AND (r.title LIKE %:keyword% OR r.description LIKE %:keyword%) ORDER BY r.createdAt DESC")
    Page<PromptRecipe> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM PromptRecipe r WHERE r.status = 'PUBLISHED' ORDER BY r.likeCount DESC, r.usageCount DESC")
    Page<PromptRecipe> findPopularRecipes(Pageable pageable);

    @Query("SELECT r FROM PromptRecipe r WHERE r.status = 'PUBLISHED' AND r.recommendedModel = :model ORDER BY r.usageCount DESC")
    List<PromptRecipe> findTopUsedRecipesByModel(@Param("model") AIModel model, Pageable pageable);

    @Query("SELECT r FROM PromptRecipe r WHERE r.status = 'PUBLISHED' AND r.category = :category ORDER BY r.usageCount DESC")
    List<PromptRecipe> findTopUsedRecipesByCategory(@Param("category") RecipeCategory category, Pageable pageable);

    Long countByUserAndStatus(User user, RecipeStatus status);
}
