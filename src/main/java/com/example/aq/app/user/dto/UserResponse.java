package com.example.aq.app.user.dto;

import com.example.aq.app.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String bio;
    private Integer points;
    private Integer level;
    private List<String> interests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public UserResponse(Long id, String email, String nickname, String profileImageUrl,
                       String bio, Integer points, Integer level, List<String> interests,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.points = points;
        this.level = level;
        this.interests = interests;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .bio(user.getBio())
                .points(user.getPoints())
                .level(user.getLevel())
                .interests(user.getInterests())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
