package com.example.aq.app.model.repository;

import com.example.aq.app.model.domain.ModelProposal;
import com.example.aq.app.model.domain.ModelProposalStatus;
import com.example.aq.app.model.domain.ModelCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelProposalRepository extends JpaRepository<ModelProposal, Long> {
    
    @EntityGraph(attributePaths = {"capabilities"})
    Optional<ModelProposal> findById(Long id);
    
    @EntityGraph(attributePaths = {"capabilities"})
    @Query("SELECT p FROM ModelProposal p WHERE p.name = :name AND p.provider = :provider")
    Optional<ModelProposal> findByNameAndProvider(@Param("name") String name, @Param("provider") String provider);
    
    @EntityGraph(attributePaths = {"capabilities"})
    @Query("SELECT p FROM ModelProposal p WHERE p.status = :status")
    Page<ModelProposal> findByStatus(@Param("status") ModelProposalStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"capabilities"})
    @Query("SELECT p FROM ModelProposal p WHERE p.status = :status ORDER BY p.likeCount DESC")
    Page<ModelProposal> findByStatusOrderByLikeCountDesc(@Param("status") ModelProposalStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"capabilities"})
    @Query("SELECT p FROM ModelProposal p WHERE p.status = :status ORDER BY p.createdAt DESC")
    Page<ModelProposal> findByStatusOrderByCreatedAtDesc(@Param("status") ModelProposalStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"capabilities"})
    @Query("SELECT p FROM ModelProposal p WHERE p.status = :status AND p.likeCount >= :minLikeCount ORDER BY p.likeCount DESC")
    Page<ModelProposal> findPendingWithMinLikeCount(@Param("status") ModelProposalStatus status, 
                                                      @Param("minLikeCount") Integer minLikeCount, 
                                                      Pageable pageable);
    
    @EntityGraph(attributePaths = {"capabilities"})
    @Query("SELECT p FROM ModelProposal p WHERE p.status = :status AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.provider) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ModelProposal> searchByKeyword(@Param("status") ModelProposalStatus status, 
                                        @Param("keyword") String keyword, 
                                        Pageable pageable);
    
    @EntityGraph(attributePaths = {"capabilities"})
    @Query("SELECT p FROM ModelProposal p WHERE p.status = :status AND p.category = :category")
    Page<ModelProposal> findByStatusAndCategory(@Param("status") ModelProposalStatus status, 
                                                 @Param("category") ModelCategory category, 
                                                 Pageable pageable);
    
    @EntityGraph(attributePaths = {"capabilities"})
    @Query("SELECT p FROM ModelProposal p WHERE p.user.id = :userId")
    Page<ModelProposal> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
