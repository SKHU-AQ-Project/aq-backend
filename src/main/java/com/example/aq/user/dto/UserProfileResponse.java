package com.example.aq.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private String bio;
    private Integer points;
    private Integer level;
    private List<String> interests;
    private LocalDateTime createdAt;
    private Long followerCount;
    private Long followingCount;
    private Long reviewCount;
    private Long recipeCount;
}
