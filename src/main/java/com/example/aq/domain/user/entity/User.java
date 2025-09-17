package com.example.aq.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "profile_url")
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_type")
    private StatusType statusType;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder
    public User(String oauthId, String email, String nickname, String profileUrl, 
                RoleType roleType, SocialType socialType, LocalDateTime lastLoginAt) {
        this.oauthId = oauthId;
        this.email = email;
        this.nickname = nickname;
        this.password = UUID.randomUUID().toString();
        this.profileUrl = profileUrl;
        this.roleType = roleType;
        this.socialType = socialType;
        this.statusType = StatusType.ACTIVE;
        this.lastLoginAt = lastLoginAt;
    }

    public boolean isActive() {
        return this.statusType == StatusType.ACTIVE;
    }

    public boolean isWithdrawn() {
        return this.statusType == StatusType.WITHDRAWN;
    }

    public void withdrawAccount() {
        this.statusType = StatusType.WITHDRAWN;
        this.refreshToken = null;
        this.nickname = "탈퇴한 사용자";
        this.profileUrl = null;
    }

    public void updateRole(RoleType roleType) {
        if (roleType != null) this.roleType = roleType;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void markLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
