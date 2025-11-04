package com.example.aq.app.interaction.service;

import com.example.aq.app.interaction.domain.Bookmark;
import com.example.aq.app.interaction.domain.BookmarkType;
import com.example.aq.app.interaction.domain.Like;
import com.example.aq.app.interaction.domain.LikeType;
import com.example.aq.app.interaction.repository.BookmarkRepository;
import com.example.aq.app.interaction.repository.LikeRepository;
import com.example.aq.app.recipe.domain.Recipe;
import com.example.aq.app.recipe.repository.RecipeRepository;
import com.example.aq.app.review.domain.Review;
import com.example.aq.app.review.repository.ReviewRepository;
import com.example.aq.app.user.domain.User;
import com.example.aq.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class InteractionService {

    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final RecipeRepository recipeRepository;

    // Like 관련 메서드
    @Transactional
    public boolean toggleLike(Long userId, Long targetId, LikeType targetType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        boolean isLiked = likeRepository.existsByUserAndTargetIdAndTargetType(user, targetId, targetType);

        if (isLiked) {
            Like like = likeRepository.findByUserAndTargetIdAndTargetType(user, targetId, targetType)
                    .orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다"));
            likeRepository.delete(like);
            decrementLikeCount(targetId, targetType);
            log.info("좋아요가 취소되었습니다: {} {} by {}", targetType, targetId, userId);
            return false;
        } else {
            Like like = Like.builder()
                    .user(user)
                    .targetId(targetId)
                    .targetType(targetType)
                    .build();
            likeRepository.save(like);
            incrementLikeCount(targetId, targetType);
            log.info("좋아요가 추가되었습니다: {} {} by {}", targetType, targetId, userId);
            return true;
        }
    }

    public boolean isLiked(Long userId, Long targetId, LikeType targetType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return likeRepository.existsByUserAndTargetIdAndTargetType(user, targetId, targetType);
    }

    public List<Long> getLikedTargetIds(Long userId, LikeType targetType) {
        return likeRepository.findTargetIdsByUserIdAndTargetType(userId, targetType);
    }

    public Long getLikeCount(Long targetId, LikeType targetType) {
        return likeRepository.countByTargetIdAndTargetType(targetId, targetType);
    }

    // Bookmark 관련 메서드
    @Transactional
    public boolean toggleBookmark(Long userId, Long targetId, BookmarkType targetType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        boolean isBookmarked = bookmarkRepository.existsByUserAndTargetIdAndTargetType(user, targetId, targetType);

        if (isBookmarked) {
            // 북마크 취소
            Bookmark bookmark = bookmarkRepository.findByUserAndTargetIdAndTargetType(user, targetId, targetType)
                    .orElseThrow(() -> new RuntimeException("북마크를 찾을 수 없습니다"));
            bookmarkRepository.delete(bookmark);
            decrementBookmarkCount(targetId, targetType);
            log.info("북마크가 취소되었습니다: {} {} by {}", targetType, targetId, userId);
            return false;
        } else {
            // 북마크 추가
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .targetId(targetId)
                    .targetType(targetType)
                    .build();
            bookmarkRepository.save(bookmark);
            incrementBookmarkCount(targetId, targetType);
            log.info("북마크가 추가되었습니다: {} {} by {}", targetType, targetId, userId);
            return true;
        }
    }

    public boolean isBookmarked(Long userId, Long targetId, BookmarkType targetType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return bookmarkRepository.existsByUserAndTargetIdAndTargetType(user, targetId, targetType);
    }

    public List<Long> getBookmarkedTargetIds(Long userId, BookmarkType targetType) {
        return bookmarkRepository.findTargetIdsByUserIdAndTargetType(userId, targetType);
    }

    public Long getBookmarkCount(Long targetId, BookmarkType targetType) {
        return bookmarkRepository.countByTargetIdAndTargetType(targetId, targetType);
    }

    // 좋아요/북마크 수 증가/감소 헬퍼 메서드
    private void incrementLikeCount(Long targetId, LikeType targetType) {
        if (targetType == LikeType.REVIEW) {
            Review review = reviewRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다"));
            review.incrementLikeCount();
            reviewRepository.save(review);
        } else if (targetType == LikeType.RECIPE) {
            Recipe recipe = recipeRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다"));
            recipe.incrementLikeCount();
            recipeRepository.save(recipe);
        }
    }

    private void decrementLikeCount(Long targetId, LikeType targetType) {
        if (targetType == LikeType.REVIEW) {
            Review review = reviewRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다"));
            review.decrementLikeCount();
            reviewRepository.save(review);
        } else if (targetType == LikeType.RECIPE) {
            Recipe recipe = recipeRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다"));
            recipe.decrementLikeCount();
            recipeRepository.save(recipe);
        }
    }

    private void incrementBookmarkCount(Long targetId, BookmarkType targetType) {
        if (targetType == BookmarkType.RECIPE) {
            Recipe recipe = recipeRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다"));
            recipe.incrementBookmarkCount();
            recipeRepository.save(recipe);
        }
    }

    private void decrementBookmarkCount(Long targetId, BookmarkType targetType) {
        if (targetType == BookmarkType.RECIPE) {
            Recipe recipe = recipeRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다"));
            recipe.decrementBookmarkCount();
            recipeRepository.save(recipe);
        }
    }
}

