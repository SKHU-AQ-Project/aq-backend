package com.example.aq.global.dto;

import com.example.aq.user.domain.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private final String email;
    private final String nickname;
    private final String profileUrl;
    private final String lastLoginAt;
    private final RoleType roleType;
}
