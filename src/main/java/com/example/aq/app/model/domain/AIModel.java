package com.example.aq.app.model.domain;

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
@Table(name = "ai_models")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AIModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @CollectionTable(name = "model_capabilities", joinColumns = @JoinColumn(name = "model_id"))
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

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public AIModel(String name, String provider, String description, ModelCategory category,
                   List<String> capabilities, BigDecimal inputPricePerToken, BigDecimal outputPricePerToken,
                   Integer maxTokens, Boolean hasFreeTier, String apiEndpoint, String documentationUrl) {
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
    public void updateModel(String name, String description, List<String> capabilities,
                           BigDecimal inputPricePerToken, BigDecimal outputPricePerToken,
                           Integer maxTokens, Boolean hasFreeTier, String apiEndpoint, String documentationUrl) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        this.description = description;
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
        this.inputPricePerToken = inputPricePerToken;
        this.outputPricePerToken = outputPricePerToken;
        this.maxTokens = maxTokens;
        if (hasFreeTier != null) {
            this.hasFreeTier = hasFreeTier;
        }
        this.apiEndpoint = apiEndpoint;
        this.documentationUrl = documentationUrl;
    }

    public void updateRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public void incrementReviewCount() {
        this.reviewCount++;
    }

    public void decrementReviewCount() {
        this.reviewCount = Math.max(0, this.reviewCount - 1);
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
