package com.example.aq.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserInfoResponseDto {
    
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private String socialType;
    private String roleType;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
