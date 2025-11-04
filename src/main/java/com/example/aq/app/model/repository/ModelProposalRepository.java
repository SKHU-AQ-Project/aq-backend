package com.example.aq.app.model.repository;

import com.example.aq.app.model.domain.ModelProposal;
import com.example.aq.app.model.domain.ModelProposalStatus;
import com.example.aq.app.model.domain.ModelCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelProposalRepository extends JpaRepository<ModelProposal, Long> {
    
    Page<ModelProposal> findByStatus(ModelProposalStatus status, Pageable pageable);
    
    Page<ModelProposal> findByStatusOrderByLikeCountDesc(ModelProposalStatus status, Pageable pageable);
    
    Page<ModelProposal> findByStatusOrderByCreatedAtDesc(ModelProposalStatus status, Pageable pageable);
    
    @Query("SELECT p FROM ModelProposal p WHERE p.status = :status AND p.likeCount >= :minLikeCount ORDER BY p.likeCount DESC")
    Page<ModelProposal> findPendingWithMinLikeCount(@Param("status") ModelProposalStatus status, 
                                                      @Param("minLikeCount") Integer minLikeCount, 
                                                      Pageable pageable);
    
    @Query("SELECT p FROM ModelProposal p WHERE p.status = :status AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.provider) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ModelProposal> searchByKeyword(@Param("status") ModelProposalStatus status, 
                                        @Param("keyword") String keyword, 
                                        Pageable pageable);
    
    @Query("SELECT p FROM ModelProposal p WHERE p.status = :status AND p.category = :category")
    Page<ModelProposal> findByStatusAndCategory(@Param("status") ModelProposalStatus status, 
                                                 @Param("category") ModelCategory category, 
                                                 Pageable pageable);
    
    @Query("SELECT p FROM ModelProposal p WHERE p.user.id = :userId")
    Page<ModelProposal> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    Optional<ModelProposal> findByNameAndProvider(String name, String provider);
}

