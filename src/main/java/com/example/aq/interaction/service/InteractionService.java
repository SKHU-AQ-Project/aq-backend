package com.example.aq.interaction.service;

import com.example.aq.domain.interaction.*;
import com.example.aq.domain.user.User;
import com.example.aq.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InteractionService {

    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public boolean toggleLike(String userEmail, LikeType type, Long targetId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        boolean isLiked = likeRepository.existsByUserAndTypeAndTargetId(user, type, targetId);
        
        if (isLiked) {
            likeRepository.findByUserAndTypeAndTargetId(user, type, targetId)
                    .ifPresent(likeRepository::delete);
            log.info("좋아요 취소: {} {} by {}", type, targetId, user.getEmail());
            return false;
        } else {
            Like like = Like.builder()
                    .user(user)
                    .type(type)
                    .targetId(targetId)
                    .build();
            likeRepository.save(like);
            log.info("좋아요 추가: {} {} by {}", type, targetId, user.getEmail());
            return true;
        }
    }

    public boolean toggleBookmark(String userEmail, BookmarkType type, Long targetId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        boolean isBookmarked = bookmarkRepository.existsByUserAndTypeAndTargetId(user, type, targetId);
        
        if (isBookmarked) {
            bookmarkRepository.findByUserAndTypeAndTargetId(user, type, targetId)
                    .ifPresent(bookmarkRepository::delete);
            log.info("북마크 취소: {} {} by {}", type, targetId, user.getEmail());
            return false;
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .type(type)
                    .targetId(targetId)
                    .build();
            bookmarkRepository.save(bookmark);
            log.info("북마크 추가: {} {} by {}", type, targetId, user.getEmail());
            return true;
        }
    }

    public boolean toggleFollow(String userEmail, Long targetUserId) {
        User follower = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("팔로우할 사용자를 찾을 수 없습니다"));

        if (follower.getId().equals(following.getId())) {
            throw new RuntimeException("자기 자신을 팔로우할 수 없습니다");
        }

        boolean isFollowing = followRepository.existsByFollowerAndFollowing(follower, following);
        
        if (isFollowing) {
            followRepository.findByFollowerAndFollowing(follower, following)
                    .ifPresent(followRepository::delete);
            log.info("팔로우 취소: {} -> {} by {}", follower.getEmail(), following.getEmail(), follower.getEmail());
            return false;
        } else {
            Follow follow = Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build();
            followRepository.save(follow);
            log.info("팔로우 추가: {} -> {} by {}", follower.getEmail(), following.getEmail(), follower.getEmail());
            return true;
        }
    }

    public boolean isLiked(String userEmail, LikeType type, Long targetId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return likeRepository.existsByUserAndTypeAndTargetId(user, type, targetId);
    }

    public boolean isBookmarked(String userEmail, BookmarkType type, Long targetId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return bookmarkRepository.existsByUserAndTypeAndTargetId(user, type, targetId);
    }

    public boolean isFollowing(String userEmail, Long targetUserId) {
        User follower = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
}
