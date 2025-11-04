package com.example.aq.app.model.domain;

import com.example.aq.app.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "model_proposals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ModelProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String provider;

    @Size(max = 1000)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModelCategory category;

    @ElementCollection
    @CollectionTable(name = "proposal_capabilities", joinColumns = @JoinColumn(name = "proposal_id"))
    @Column(name = "capability")
    private List<String> capabilities = new ArrayList<>();

    @Column(name = "input_price_per_token", precision = 10, scale = 6)
    private BigDecimal inputPricePerToken;

    @Column(name = "output_price_per_token", precision = 10, scale = 6)
    private BigDecimal outputPricePerToken;

    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Column(name = "has_free_tier", nullable = false)
    private Boolean hasFreeTier = false;

    @Column(name = "api_endpoint")
    private String apiEndpoint;

    @Column(name = "documentation_url")
    private String documentationUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModelProposalStatus status = ModelProposalStatus.PENDING;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "model_id")
    private Long modelId; // 승인되어 생성된 AIModel의 ID

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ModelProposal(User user, String name, String provider, String description, ModelCategory category,
                        List<String> capabilities, BigDecimal inputPricePerToken, BigDecimal outputPricePerToken,
                        Integer maxTokens, Boolean hasFreeTier, String apiEndpoint, String documentationUrl) {
        this.user = user;
        this.name = name;
        this.provider = provider;
        this.description = description;
        this.category = category;
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
        this.inputPricePerToken = inputPricePerToken;
        this.outputPricePerToken = outputPricePerToken;
        this.maxTokens = maxTokens;
        this.hasFreeTier = hasFreeTier != null ? hasFreeTier : false;
        this.apiEndpoint = apiEndpoint;
        this.documentationUrl = documentationUrl;
    }

    // 비즈니스 메서드
    public void approve(Long adminId) {
        this.status = ModelProposalStatus.APPROVED;
        this.approvedBy = adminId;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.status = ModelProposalStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public boolean isPending() {
        return this.status == ModelProposalStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == ModelProposalStatus.APPROVED;
    }
}

