package com.example.aq.app.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String nickname;

    @Size(max = 200)
    private String bio;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(nullable = false)
    private Integer level = 1;

    @ElementCollection
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private Boolean enabled = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(String email, String password, String nickname, String bio, 
                String profileImageUrl, List<String> interests) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.interests = interests != null ? interests : new ArrayList<>();
    }

    // 비즈니스 메서드
    public void updateProfile(String nickname, String bio, String profileImageUrl, List<String> interests) {
        if (nickname != null && !nickname.trim().isEmpty()) {
            this.nickname = nickname.trim();
        }
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.interests = interests != null ? interests : new ArrayList<>();
    }

    public void addPoints(Integer points) {
        this.points += points;
        updateLevel();
    }

    public void updateLevel() {
        this.level = Math.min(10, (this.points / 100) + 1);
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }
}
