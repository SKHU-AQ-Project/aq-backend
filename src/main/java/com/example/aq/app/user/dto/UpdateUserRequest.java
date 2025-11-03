package com.example.aq.app.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다")
    private String nickname;

    @Size(max = 200, message = "소개는 200자 이하여야 합니다")
    private String bio;

    private String profileImageUrl;

    private List<String> interests;
}

