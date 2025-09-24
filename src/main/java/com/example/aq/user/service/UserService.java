package com.example.aq.user.service;

import com.example.aq.domain.interaction.FollowRepository;
import com.example.aq.domain.recipe.PromptRecipeRepository;
import com.example.aq.domain.review.ReviewRepository;
import com.example.aq.domain.user.User;
import com.example.aq.domain.user.UserRepository;
import com.example.aq.user.dto.UpdateProfileRequest;
import com.example.aq.user.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final PromptRecipeRepository recipeRepository;

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        Long followerCount = followRepository.countByFollowing(user);
        Long followingCount = followRepository.countByFollower(user);
        Long reviewCount = reviewRepository.countByUserAndStatus(user, com.example.aq.domain.review.ReviewStatus.PUBLISHED);
        Long recipeCount = recipeRepository.countByUserAndStatus(user, com.example.aq.domain.recipe.RecipeStatus.PUBLISHED);

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .bio(user.getBio())
                .points(user.getPoints())
                .level(user.getLevel())
                .interests(user.getInterests())
                .createdAt(user.getCreatedAt())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .reviewCount(reviewCount)
                .recipeCount(recipeCount)
                .build();
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new RuntimeException("이미 존재하는 닉네임입니다");
            }
        }

        user.updateProfile(
                request.getNickname() != null ? request.getNickname() : user.getNickname(),
                request.getBio() != null ? request.getBio() : user.getBio(),
                request.getProfileImage() != null ? request.getProfileImage() : user.getProfileImage()
        );

        if (request.getInterests() != null) {
            user.updateInterests(request.getInterests());
        }

        log.info("사용자 프로필 업데이트: {}", user.getEmail());
        return getUserProfile(userId);
    }

    public Page<UserProfileResponse> searchUsers(String nickname, Pageable pageable) {
        return userRepository.findByNicknameContaining(nickname, pageable)
                .map(this::convertToUserProfileResponse);
    }

    public Page<UserProfileResponse> getTopUsers(Pageable pageable) {
        return userRepository.findTopUsersByPoints(pageable)
                .map(this::convertToUserProfileResponse);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    private UserProfileResponse convertToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .bio(user.getBio())
                .points(user.getPoints())
                .level(user.getLevel())
                .interests(user.getInterests())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
