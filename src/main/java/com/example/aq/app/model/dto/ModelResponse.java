package com.example.aq.app.model.dto;

import com.example.aq.app.model.domain.AIModel;
import com.example.aq.app.model.domain.ModelCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ModelResponse {
    private Long id;
    private String name;
    private String provider;
    private String description;
    private ModelCategory category;
    private List<String> capabilities;
    private BigDecimal inputPricePerToken;
    private BigDecimal outputPricePerToken;
    private Integer maxTokens;
    private Boolean hasFreeTier;
    private String apiEndpoint;
    private String documentationUrl;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ModelResponse(Long id, String name, String provider, String description, ModelCategory category,
                        List<String> capabilities, BigDecimal inputPricePerToken, BigDecimal outputPricePerToken,
                        Integer maxTokens, Boolean hasFreeTier, String apiEndpoint, String documentationUrl,
                        BigDecimal averageRating, Integer reviewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.description = description;
        this.category = category;
        this.capabilities = capabilities;
        this.inputPricePerToken = inputPricePerToken;
        this.outputPricePerToken = outputPricePerToken;
        this.maxTokens = maxTokens;
        this.hasFreeTier = hasFreeTier;
        this.apiEndpoint = apiEndpoint;
        this.documentationUrl = documentationUrl;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ModelResponse of(AIModel model) {
        return ModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .provider(model.getProvider())
                .description(model.getDescription())
                .category(model.getCategory())
                .capabilities(model.getCapabilities())
                .inputPricePerToken(model.getInputPricePerToken())
                .outputPricePerToken(model.getOutputPricePerToken())
                .maxTokens(model.getMaxTokens())
                .hasFreeTier(model.getHasFreeTier())
                .apiEndpoint(model.getApiEndpoint())
                .documentationUrl(model.getDocumentationUrl())
                .averageRating(model.getAverageRating())
                .reviewCount(model.getReviewCount())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}

