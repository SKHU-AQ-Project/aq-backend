package com.example.aq.domain.review;

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
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByStatusOrderByCreatedAtDesc(ReviewStatus status, Pageable pageable);

    Page<Review> findByModelAndStatusOrderByCreatedAtDesc(AIModel model, ReviewStatus status, Pageable pageable);

    Page<Review> findByUserAndStatusOrderByCreatedAtDesc(User user, ReviewStatus status, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.status = 'PUBLISHED' AND :tag MEMBER OF r.tags ORDER BY r.createdAt DESC")
    Page<Review> findByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.status = 'PUBLISHED' AND (r.title LIKE %:keyword% OR r.content LIKE %:keyword%) ORDER BY r.createdAt DESC")
    Page<Review> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.status = 'PUBLISHED' ORDER BY r.likeCount DESC, r.viewCount DESC")
    Page<Review> findPopularReviews(Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.status = 'PUBLISHED' AND r.model = :model ORDER BY r.rating DESC")
    List<Review> findTopRatedReviewsByModel(@Param("model") AIModel model, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.model = :model AND r.status = 'PUBLISHED'")
    Double getAverageRatingByModel(@Param("model") AIModel model);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.model = :model AND r.status = 'PUBLISHED'")
    Long getReviewCountByModel(@Param("model") AIModel model);

    Long countByUserAndStatus(User user, ReviewStatus status);
}
