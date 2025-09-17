package com.example.aq.global.common.dto;

import com.example.aq.domain.user.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {
    private String email;
    private String nickname;
    private String profileUrl;
    private String lastLoginAt;
    private RoleType roleType;
}
