package com.example.aq.app.user.dto;

import com.example.aq.app.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String bio;
    private String profileImageUrl;
    private Integer level;
    private Integer points;
    private LocalDateTime followedAt; // 팔로우한 시간

    public static FollowUserResponse from(User user, LocalDateTime followedAt) {
        return FollowUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .level(user.getLevel())
                .points(user.getPoints())
                .followedAt(followedAt)
                .build();
    }
}

