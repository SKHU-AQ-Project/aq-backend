package com.example.aq.domain.model;

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

    List<AIModel> findByStatus(ModelStatus status);
    
    Page<AIModel> findByStatus(ModelStatus status, Pageable pageable);

    @Query("SELECT m FROM AIModel m WHERE m.status = 'ACTIVE' AND m.category = :category")
    List<AIModel> findByCategory(@Param("category") ModelCategory category);

    @Query("SELECT m FROM AIModel m WHERE m.status = 'ACTIVE' AND :capability MEMBER OF m.capabilities")
    List<AIModel> findByCapability(@Param("capability") String capability);

    @Query("SELECT m FROM AIModel m WHERE m.status = 'ACTIVE' AND (m.name LIKE %:keyword% OR m.provider LIKE %:keyword%)")
    Page<AIModel> searchByNameOrProvider(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM AIModel m WHERE m.status = 'ACTIVE' AND m.hasFreeTier = true")
    List<AIModel> findModelsWithFreeTier();

    Optional<AIModel> findByNameAndProvider(String name, String provider);
}
