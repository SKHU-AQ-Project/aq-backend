package com.example.aq.global.oauth.domain.userinfo;

import java.util.Map;

public abstract class UserInfo {

    protected Map<String, Object> attributes;

    public UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();
    public abstract String getEmail();
    public abstract String getNickname();
    public abstract String getProfileUrl();
}
