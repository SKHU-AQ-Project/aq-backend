package com.example.aq.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {
    KAKAO("KAKAO", "카카오"),
    GOOGLE("GOOGLE", "구글"),
    APPLE("APPLE", "애플");

    private final String key;
    private final String title;
}
