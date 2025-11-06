package com.example.aq.app.review.repository;

import com.example.aq.app.model.domain.AIModel;
import com.example.aq.app.review.domain.Review;
import com.example.aq.app.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    @Query("SELECT r FROM Review r WHERE r.id = :id")
    Optional<Review> findById(@Param("id") Long id);
    
    @Query("SELECT r FROM Review r WHERE r.active = true")
    Page<Review> findAllActive(Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true ORDER BY r.createdAt DESC")
    Page<Review> findLatestReviews(Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true ORDER BY r.likeCount DESC")
    Page<Review> findPopularReviews(Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true ORDER BY r.rating DESC")
    Page<Review> findTopRatedReviews(Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.model = :model")
    Page<Review> findByModel(@Param("model") AIModel model, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.author = :author")
    Page<Review> findByAuthor(@Param("author") User author, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.model.id = :modelId")
    Page<Review> findByModelId(@Param("modelId") Long modelId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.author.id = :authorId")
    Page<Review> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.useCase) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "EXISTS (SELECT t FROM r.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Review> searchReviews(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.isFeatured = true ORDER BY r.createdAt DESC")
    Page<Review> findFeaturedReviews(Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.createdAt >= :since")
    Page<Review> findRecentReviews(@Param("since") LocalDateTime since, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.rating >= :minRating")
    Page<Review> findByMinRating(@Param("minRating") Integer minRating, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.tags LIKE %:tag%")
    Page<Review> findByTag(@Param("tag") String tag, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.model.provider = :provider")
    Page<Review> findByModelProvider(@Param("provider") String provider, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.model.category = :category")
    Page<Review> findByModelCategory(@Param("category") String category, Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.active = true AND r.model = :model")
    Long countByModel(@Param("model") AIModel model);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.active = true AND r.model = :model")
    Double getAverageRatingByModel(@Param("model") AIModel model);
    
    @Query("SELECT r FROM Review r WHERE r.active = true AND r.author.id = :userId AND r.createdAt >= :since")
    List<Review> findRecentReviewsByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
