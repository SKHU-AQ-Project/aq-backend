package com.example.aq.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatsResponse {
    private Long userId;
    private Long followerCount;  // 팔로워 수
    private Long followingCount; // 팔로잉 수
    private Boolean isFollowing; // 현재 사용자가 이 사용자를 팔로우하는지 여부

    public static FollowStatsResponse of(Long userId, Long followerCount, Long followingCount, Boolean isFollowing) {
        return FollowStatsResponse.builder()
                .userId(userId)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .isFollowing(isFollowing)
                .build();
    }
}

