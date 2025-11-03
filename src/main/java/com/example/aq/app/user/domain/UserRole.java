package com.example.aq.app.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("일반 사용자"),
    ADMIN("관리자"),
    MODERATOR("모더레이터");

    private final String description;
}
