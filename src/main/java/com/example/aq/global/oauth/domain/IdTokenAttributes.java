package com.example.aq.global.oauth.domain;

import com.example.aq.global.oauth.domain.userinfo.KakaoUserInfo;
import com.example.aq.global.oauth.domain.userinfo.UserInfo;
import com.example.aq.user.domain.RoleType;
import com.example.aq.user.domain.SocialType;
import com.example.aq.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class IdTokenAttributes {

    private final UserInfo userInfo;
    private final SocialType socialType;

    @Builder
    public IdTokenAttributes(Map<String, Object> attributes, SocialType socialType) {
        this.userInfo = getUserInfo(attributes, socialType);
        this.socialType = socialType;
    }

    private UserInfo getUserInfo(Map<String, Object> attributes, SocialType socialType) {
        return switch (socialType) {
            case KAKAO -> new KakaoUserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 타입입니다: " + socialType);
        };
    }

    public User toUser() {
        return User.builder()
                .oauthId(userInfo.getId())
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .profileUrl(userInfo.getProfileUrl())
                .roleType(RoleType.GUEST)
                .socialType(socialType)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }
}
