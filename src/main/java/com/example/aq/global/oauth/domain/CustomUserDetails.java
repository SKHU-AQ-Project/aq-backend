package com.example.aq.global.oauth.domain;

import com.example.aq.user.domain.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Collection<? extends GrantedAuthority> authorities;
    private final String username;
    private final RoleType roleType;
    private final Long userId;

    public CustomUserDetails(Collection<? extends GrantedAuthority> authorities, String username, RoleType roleType, Long userId) {
        this.authorities = authorities;
        this.username = username;
        this.roleType = roleType;
        this.userId = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // OAuth2 로그인이므로 비밀번호 사용하지 않음
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
