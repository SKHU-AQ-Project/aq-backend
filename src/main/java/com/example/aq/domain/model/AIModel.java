package com.example.aq.domain.model;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String provider;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModelCategory category;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "model_capabilities", joinColumns = @JoinColumn(name = "model_id"))
    @Column(name = "capability")
    private List<String> capabilities = new ArrayList<>();

    @Column(precision = 10, scale = 6)
    private BigDecimal inputPricePerToken;

    @Column(precision = 10, scale = 6)
    private BigDecimal outputPricePerToken;

    private Integer maxTokens;

    private Boolean hasFreeTier = false;

    private String apiEndpoint;

    private String documentationUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModelStatus status = ModelStatus.ACTIVE;

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
        this.hasFreeTier = hasFreeTier;
        this.apiEndpoint = apiEndpoint;
        this.documentationUrl = documentationUrl;
    }

    public void updatePricing(BigDecimal inputPricePerToken, BigDecimal outputPricePerToken) {
        this.inputPricePerToken = inputPricePerToken;
        this.outputPricePerToken = outputPricePerToken;
    }

    public void updateCapabilities(List<String> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
    }

    public void deactivate() {
        this.status = ModelStatus.INACTIVE;
    }
}
