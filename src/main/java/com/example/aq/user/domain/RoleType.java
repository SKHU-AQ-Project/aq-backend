package com.example.aq.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    GUEST("GUEST", "게스트"),
    USER("USER", "일반 사용자"),
    ADMIN("ADMIN", "관리자");

    private final String key;
    private final String title;
    
    @Override
    public String toString() {
        // Spring Security에서 사용할 때는 ROLE_ 접두사 추가
        return "ROLE_" + this.name();
    }
}
