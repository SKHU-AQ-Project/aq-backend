package com.example.aq.global.oauth.domain;

import com.example.aq.domain.user.entity.RoleType;
import com.example.aq.domain.user.entity.SocialType;
import com.example.aq.domain.user.entity.User;
import com.example.aq.global.oauth.domain.userinfo.KakaoUserInfo;
import com.example.aq.global.oauth.domain.userinfo.UserInfo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class IdTokenAttributes {

    private UserInfo userInfo;
    private SocialType socialType;

    public IdTokenAttributes(Map<String, Object> attributes, SocialType socialType) {
        this.socialType = socialType;
        if (socialType == SocialType.KAKAO) {
            this.userInfo = new KakaoUserInfo(attributes);
        }
        // 추후 다른 소셜 로그인 추가 시 확장 가능
    }

    public User toUser() {
        return User.builder()
                .socialType(socialType)
                .roleType(RoleType.GUEST)
                .oauthId(userInfo.getId())
                .nickname(userInfo.getNickname())
                .profileUrl(userInfo.getProfileUrl())
                .email(userInfo.getEmail())
                .lastLoginAt(LocalDateTime.now())
                .build();
    }
}
