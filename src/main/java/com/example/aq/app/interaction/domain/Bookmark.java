package com.example.aq.app.interaction.domain;

import com.example.aq.app.user.domain.User;
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
@Table(name = "bookmarks", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_id", "target_type"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private BookmarkType targetType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Bookmark(User user, Long targetId, BookmarkType targetType) {
        this.user = user;
        this.targetId = targetId;
        this.targetType = targetType;
    }

    // 편의 메서드
    public String getUserNickname() {
        return user.getNickname();
    }

    public String getUserProfileImage() {
        return user.getProfileImageUrl();
    }
}
