package com.example.aq.app.interaction.repository;

import com.example.aq.app.interaction.domain.Like;
import com.example.aq.app.interaction.domain.LikeType;
import com.example.aq.app.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByUserAndTargetIdAndTargetType(User user, Long targetId, LikeType targetType);
    
    boolean existsByUserAndTargetIdAndTargetType(User user, Long targetId, LikeType targetType);
    
    @Query("SELECT l FROM Like l WHERE l.user = :user AND l.targetType = :targetType")
    List<Like> findByUserAndTargetType(@Param("user") User user, @Param("targetType") LikeType targetType);
    
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.targetType = :targetType")
    List<Like> findByUserIdAndTargetType(@Param("userId") Long userId, @Param("targetType") LikeType targetType);
    
    @Query("SELECT l FROM Like l WHERE l.targetId = :targetId AND l.targetType = :targetType")
    List<Like> findByTargetIdAndTargetType(@Param("targetId") Long targetId, @Param("targetType") LikeType targetType);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.targetId = :targetId AND l.targetType = :targetType")
    Long countByTargetIdAndTargetType(@Param("targetId") Long targetId, @Param("targetType") LikeType targetType);
    
    @Query("SELECT l.targetId FROM Like l WHERE l.user = :user AND l.targetType = :targetType")
    List<Long> findTargetIdsByUserAndTargetType(@Param("user") User user, @Param("targetType") LikeType targetType);
    
    @Query("SELECT l.targetId FROM Like l WHERE l.user.id = :userId AND l.targetType = :targetType")
    List<Long> findTargetIdsByUserIdAndTargetType(@Param("userId") Long userId, @Param("targetType") LikeType targetType);
}
