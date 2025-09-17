package com.example.aq.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusType {
    ACTIVE("ACTIVE", "활성"),
    BANNED("BANNED", "정지"),
    WITHDRAWN("WITHDRAWN", "탈퇴");

    private final String key;
    private final String title;
}
