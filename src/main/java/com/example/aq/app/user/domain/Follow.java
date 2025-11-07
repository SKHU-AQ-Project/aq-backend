package com.example.aq.app.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower; // 팔로우하는 사람

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following; // 팔로우당하는 사람

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Follow(User follower, User following) {
        validateFollowRelationship(follower, following);
        this.follower = follower;
        this.following = following;
    }

    // 비즈니스 로직: 자기 자신을 팔로우할 수 없음
    private void validateFollowRelationship(User follower, User following) {
        if (follower.getId().equals(following.getId())) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다");
        }
    }

    // 편의 메서드
    public Long getFollowerId() {
        return follower.getId();
    }

    public Long getFollowingId() {
        return following.getId();
    }

    public String getFollowerNickname() {
        return follower.getNickname();
    }

    public String getFollowingNickname() {
        return following.getNickname();
    }
}

