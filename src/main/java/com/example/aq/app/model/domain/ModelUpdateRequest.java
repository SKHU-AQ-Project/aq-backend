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
@Table(name = "model_update_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ModelUpdateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private AIModel model;

    @Size(max = 100)
    private String name;

    @Size(max = 50)
    private String provider;

    @Size(max = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ModelCategory category;

    @ElementCollection
    @CollectionTable(name = "update_request_capabilities", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "capability")
    private List<String> capabilities = new ArrayList<>();

    @Column(name = "input_price_per_token", precision = 10, scale = 6)
    private BigDecimal inputPricePerToken;

    @Column(name = "output_price_per_token", precision = 10, scale = 6)
    private BigDecimal outputPricePerToken;

    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Column(name = "has_free_tier")
    private Boolean hasFreeTier;

    @Column(name = "api_endpoint")
    private String apiEndpoint;

    @Column(name = "documentation_url")
    private String documentationUrl;

    @NotBlank
    @Size(max = 500)
    @Column(name = "reason", nullable = false)
    private String reason; // 수정 요청 사유

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UpdateRequestStatus status = UpdateRequestStatus.PENDING;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ModelUpdateRequest(User user, AIModel model, String name, String provider, String description,
                             ModelCategory category, List<String> capabilities, BigDecimal inputPricePerToken,
                             BigDecimal outputPricePerToken, Integer maxTokens, Boolean hasFreeTier,
                             String apiEndpoint, String documentationUrl, String reason) {
        this.user = user;
        this.model = model;
        this.name = name;
        this.provider = provider;
        this.description = description;
        this.category = category;
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
        this.inputPricePerToken = inputPricePerToken;
        this.outputPricePerToken = outputPricePerToken;
        this.maxTokens = maxTokens;
        this.hasFreeTier = hasFreeTier;
        this.apiEndpoint = apiEndpoint;
        this.documentationUrl = documentationUrl;
        this.reason = reason;
    }

    // 비즈니스 메서드
    public void approve(Long adminId) {
        this.status = UpdateRequestStatus.APPROVED;
        this.processedBy = adminId;
        this.processedAt = LocalDateTime.now();
    }

    public void reject(Long adminId) {
        this.status = UpdateRequestStatus.REJECTED;
        this.processedBy = adminId;
        this.processedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status == UpdateRequestStatus.PENDING;
    }
}

