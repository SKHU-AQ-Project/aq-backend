package com.example.aq.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private final String accessToken;
    private final String refreshToken;
}
