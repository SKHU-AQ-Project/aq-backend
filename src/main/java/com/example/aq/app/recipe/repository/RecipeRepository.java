package com.example.aq.app.recipe.repository;

import com.example.aq.app.recipe.domain.Recipe;
import com.example.aq.app.recipe.domain.RecipeCategory;
import com.example.aq.app.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true")
    Page<Recipe> findAllActive(Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true ORDER BY r.createdAt DESC")
    Page<Recipe> findLatestRecipes(Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true ORDER BY r.likeCount DESC")
    Page<Recipe> findPopularRecipes(Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true ORDER BY r.useCount DESC")
    Page<Recipe> findMostUsedRecipes(Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.author = :author")
    Page<Recipe> findByAuthor(@Param("author") User author, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.author.id = :authorId")
    Page<Recipe> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.category = :category")
    Page<Recipe> findByCategory(@Param("category") RecipeCategory category, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.difficultyLevel = :difficulty")
    Page<Recipe> findByDifficultyLevel(@Param("difficulty") Integer difficulty, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.promptTemplate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "EXISTS (SELECT t FROM r.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Recipe> searchRecipes(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.isFeatured = true ORDER BY r.createdAt DESC")
    Page<Recipe> findFeaturedRecipes(Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.isVerified = true ORDER BY r.createdAt DESC")
    Page<Recipe> findVerifiedRecipes(Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.createdAt >= :since")
    Page<Recipe> findRecentRecipes(@Param("since") LocalDateTime since, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.tags LIKE %:tag%")
    Page<Recipe> findByTag(@Param("tag") String tag, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.suitableModels LIKE %:modelName%")
    Page<Recipe> findBySuitableModel(@Param("modelName") String modelName, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.estimatedTimeMinutes <= :maxTime")
    Page<Recipe> findByMaxTime(@Param("maxTime") Integer maxTime, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true ORDER BY r.bookmarkCount DESC")
    Page<Recipe> findMostBookmarkedRecipes(Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM Recipe r WHERE r.active = true AND r.author = :author")
    Long countByAuthor(@Param("author") User author);
    
    @Query("SELECT COUNT(r) FROM Recipe r WHERE r.active = true AND r.category = :category")
    Long countByCategory(@Param("category") RecipeCategory category);
    
    @Query("SELECT r FROM Recipe r WHERE r.active = true AND r.author.id = :userId AND r.createdAt >= :since")
    List<Recipe> findRecentRecipesByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
