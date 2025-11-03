package com.example.aq.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String nickname;

    @Builder
    public AuthResponse(String token, Long userId, String email, String nickname) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
    }
}

