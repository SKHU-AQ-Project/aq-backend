package com.example.aq.domain.interaction;

import com.example.aq.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByTypeAndTargetIdAndStatusOrderByCreatedAtDesc(CommentType type, Long targetId, CommentStatus status, Pageable pageable);

    List<Comment> findByParentAndStatusOrderByCreatedAtAsc(Comment parent, CommentStatus status);

    @Query("SELECT c FROM Comment c WHERE c.user = :user AND c.status = 'ACTIVE' ORDER BY c.createdAt DESC")
    Page<Comment> findByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.type = :type AND c.targetId = :targetId AND c.status = 'ACTIVE'")
    Long countByTypeAndTargetId(@Param("type") CommentType type, @Param("targetId") Long targetId);
}
