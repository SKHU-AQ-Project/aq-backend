package com.example.aq.app.user.repository;

import com.example.aq.app.user.domain.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로우 관계 존재 여부 확인
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 특정 팔로우 관계 조회
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 팔로워 목록 조회 (페이지네이션)
    @Query("SELECT f FROM Follow f JOIN FETCH f.follower WHERE f.following.id = :userId")
    Page<Follow> findFollowersByUserId(@Param("userId") Long userId, Pageable pageable);

    // 팔로잉 목록 조회 (페이지네이션)
    @Query("SELECT f FROM Follow f JOIN FETCH f.following WHERE f.follower.id = :userId")
    Page<Follow> findFollowingByUserId(@Param("userId") Long userId, Pageable pageable);

    // 팔로워 수 카운트
    long countByFollowingId(Long userId);

    // 팔로잉 수 카운트
    long countByFollowerId(Long userId);

    // 특정 사용자의 모든 팔로워 삭제 (사용자 삭제 시)
    void deleteByFollowerId(Long userId);

    void deleteByFollowingId(Long userId);

    // 팔로잉하는 사용자 ID 목록 조회
    @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId")
    List<Long> findFollowingIdsByFollowerId(@Param("userId") Long userId);

    // 팔로워 사용자 ID 목록 조회
    @Query("SELECT f.follower.id FROM Follow f WHERE f.following.id = :userId")
    List<Long> findFollowerIdsByFollowingId(@Param("userId") Long userId);
}

