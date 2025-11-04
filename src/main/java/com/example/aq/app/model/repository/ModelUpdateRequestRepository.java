package com.example.aq.app.model.repository;

import com.example.aq.app.model.domain.ModelUpdateRequest;
import com.example.aq.app.model.domain.UpdateRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelUpdateRequestRepository extends JpaRepository<ModelUpdateRequest, Long> {
    
    Page<ModelUpdateRequest> findByStatus(UpdateRequestStatus status, Pageable pageable);
    
    Page<ModelUpdateRequest> findByModelId(Long modelId, Pageable pageable);
    
    @Query("SELECT r FROM ModelUpdateRequest r WHERE r.user.id = :userId")
    Page<ModelUpdateRequest> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT r FROM ModelUpdateRequest r WHERE r.model.id = :modelId AND r.status = :status")
    List<ModelUpdateRequest> findByModelIdAndStatus(@Param("modelId") Long modelId, 
                                                     @Param("status") UpdateRequestStatus status);
}

