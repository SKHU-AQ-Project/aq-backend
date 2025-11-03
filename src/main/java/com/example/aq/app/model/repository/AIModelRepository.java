package com.example.aq.app.model.repository;

import com.example.aq.app.model.domain.AIModel;
import com.example.aq.app.model.domain.ModelCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIModelRepository extends JpaRepository<AIModel, Long> {
    
    Optional<AIModel> findByNameAndProvider(String name, String provider);
    
    @Query("SELECT m FROM AIModel m WHERE m.active = true")
    List<AIModel> findAllActive();
    
    @Query("SELECT m FROM AIModel m WHERE m.active = true")
    Page<AIModel> findAllActive(Pageable pageable);
    
    @Query("SELECT m FROM AIModel m WHERE m.active = true AND m.category = :category")
    Page<AIModel> findByCategory(@Param("category") ModelCategory category, Pageable pageable);
    
    @Query("SELECT m FROM AIModel m WHERE m.active = true AND m.provider = :provider")
    Page<AIModel> findByProvider(@Param("provider") String provider, Pageable pageable);
    
    @Query("SELECT m FROM AIModel m WHERE m.active = true AND m.hasFreeTier = true")
    Page<AIModel> findFreeTierModels(Pageable pageable);
    
    @Query("SELECT m FROM AIModel m WHERE m.active = true ORDER BY m.averageRating DESC NULLS LAST")
    Page<AIModel> findTopRatedModels(Pageable pageable);
    
    @Query("SELECT m FROM AIModel m WHERE m.active = true ORDER BY m.reviewCount DESC")
    Page<AIModel> findMostReviewedModels(Pageable pageable);
    
    @Query("SELECT m FROM AIModel m WHERE m.active = true AND " +
           "(LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.provider) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<AIModel> searchModels(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT m.provider FROM AIModel m WHERE m.active = true ORDER BY m.provider")
    List<String> findAllProviders();
    
    @Query("SELECT m FROM AIModel m WHERE m.active = true AND m.capabilities LIKE %:capability%")
    Page<AIModel> findByCapability(@Param("capability") String capability, Pageable pageable);
}
