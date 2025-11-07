package com.example.aq.app.user.service;

import com.example.aq.app.user.domain.Follow;
import com.example.aq.app.user.domain.User;
import com.example.aq.app.user.dto.FollowListResponse;
import com.example.aq.app.user.dto.FollowStatsResponse;
import com.example.aq.app.user.dto.FollowUserResponse;
import com.example.aq.app.user.repository.FollowRepository;
import com.example.aq.app.user.repository.UserRepository;
import com.example.aq.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    /**
     * 팔로우 토글 (팔로우 또는 언팔로우)
     * @return true: 팔로우됨, false: 언팔로우됨
     */
    @Transactional
    public boolean toggleFollow(Long currentUserId, Long targetUserId) {
        // 자기 자신을 팔로우하려는 경우
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다");
        }

        // 사용자 존재 확인
        User follower = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("팔로우할 사용자를 찾을 수 없습니다"));

        // 이미 팔로우 중인지 확인
        boolean exists = followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId);

        if (exists) {
            // 언팔로우
            Follow follow = followRepository.findByFollowerIdAndFollowingId(currentUserId, targetUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("팔로우 관계를 찾을 수 없습니다"));
            followRepository.delete(follow);
            log.info("User {} unfollowed user {}", currentUserId, targetUserId);
            return false;
        } else {
            // 팔로우
            Follow follow = Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build();
            followRepository.save(follow);
            log.info("User {} followed user {}", currentUserId, targetUserId);
            return true;
        }
    }

    /**
     * 팔로워 목록 조회
     */
    public FollowListResponse getFollowers(Long userId, int page, int size) {
        // 사용자 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Follow> followPage = followRepository.findFollowersByUserId(userId, pageable);

        List<FollowUserResponse> followers = followPage.getContent().stream()
                .map(follow -> FollowUserResponse.from(follow.getFollower(), follow.getCreatedAt()))
                .collect(Collectors.toList());

        return FollowListResponse.of(
                followers,
                followPage.getNumber(),
                followPage.getTotalPages(),
                followPage.getTotalElements(),
                followPage.hasNext()
        );
    }

    /**
     * 팔로잉 목록 조회
     */
    public FollowListResponse getFollowing(Long userId, int page, int size) {
        // 사용자 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Follow> followPage = followRepository.findFollowingByUserId(userId, pageable);

        List<FollowUserResponse> following = followPage.getContent().stream()
                .map(follow -> FollowUserResponse.from(follow.getFollowing(), follow.getCreatedAt()))
                .collect(Collectors.toList());

        return FollowListResponse.of(
                following,
                followPage.getNumber(),
                followPage.getTotalPages(),
                followPage.getTotalElements(),
                followPage.hasNext()
        );
    }

    /**
     * 팔로우 통계 조회
     */
    public FollowStatsResponse getFollowStats(Long userId, Long currentUserId) {
        // 사용자 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));

        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        
        Boolean isFollowing = null;
        if (currentUserId != null && !currentUserId.equals(userId)) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUserId, userId);
        }

        return FollowStatsResponse.of(userId, followerCount, followingCount, isFollowing);
    }

    /**
     * 팔로우 여부 확인
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    /**
     * 팔로잉하는 사용자 ID 목록
     */
    public List<Long> getFollowingIds(Long userId) {
        return followRepository.findFollowingIdsByFollowerId(userId);
    }

    /**
     * 팔로워 사용자 ID 목록
     */
    public List<Long> getFollowerIds(Long userId) {
        return followRepository.findFollowerIdsByFollowingId(userId);
    }
}

