package com.example.aq.app.interaction.repository;

import com.example.aq.app.interaction.domain.Bookmark;
import com.example.aq.app.interaction.domain.BookmarkType;
import com.example.aq.app.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    Optional<Bookmark> findByUserAndTargetIdAndTargetType(User user, Long targetId, BookmarkType targetType);
    
    boolean existsByUserAndTargetIdAndTargetType(User user, Long targetId, BookmarkType targetType);
    
    @Query("SELECT b FROM Bookmark b WHERE b.user = :user AND b.targetType = :targetType")
    List<Bookmark> findByUserAndTargetType(@Param("user") User user, @Param("targetType") BookmarkType targetType);
    
    @Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId AND b.targetType = :targetType")
    List<Bookmark> findByUserIdAndTargetType(@Param("userId") Long userId, @Param("targetType") BookmarkType targetType);
    
    @Query("SELECT b FROM Bookmark b WHERE b.targetId = :targetId AND b.targetType = :targetType")
    List<Bookmark> findByTargetIdAndTargetType(@Param("targetId") Long targetId, @Param("targetType") BookmarkType targetType);
    
    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.targetId = :targetId AND b.targetType = :targetType")
    Long countByTargetIdAndTargetType(@Param("targetId") Long targetId, @Param("targetType") BookmarkType targetType);
    
    @Query("SELECT b.targetId FROM Bookmark b WHERE b.user = :user AND b.targetType = :targetType")
    List<Long> findTargetIdsByUserAndTargetType(@Param("user") User user, @Param("targetType") BookmarkType targetType);
    
    @Query("SELECT b.targetId FROM Bookmark b WHERE b.user.id = :userId AND b.targetType = :targetType")
    List<Long> findTargetIdsByUserIdAndTargetType(@Param("userId") Long userId, @Param("targetType") BookmarkType targetType);
}
