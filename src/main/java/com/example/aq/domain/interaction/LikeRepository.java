package com.example.aq.domain.interaction;

import com.example.aq.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndTypeAndTargetId(User user, LikeType type, Long targetId);

    boolean existsByUserAndTypeAndTargetId(User user, LikeType type, Long targetId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.type = :type AND l.targetId = :targetId")
    Long countByTypeAndTargetId(@Param("type") LikeType type, @Param("targetId") Long targetId);
}
