package com.example.aq.domain.interaction;

import com.example.aq.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Page<Follow> findByFollowerOrderByCreatedAtDesc(User follower, Pageable pageable);

    Page<Follow> findByFollowingOrderByCreatedAtDesc(User following, Pageable pageable);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower = :user")
    Long countByFollower(@Param("user") User user);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.following = :user")
    Long countByFollowing(@Param("user") User user);
}
