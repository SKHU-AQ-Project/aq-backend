package com.example.aq.app.model.dto;

import com.example.aq.app.model.domain.ModelCategory;
import com.example.aq.app.model.domain.UpdateRequestStatus;
import com.example.aq.app.model.domain.ModelUpdateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ModelUpdateRequestResponse {
    private Long id;
    private Long userId;
    private String userNickname;
    private Long modelId;
    private String modelName;
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
    private String reason;
    private UpdateRequestStatus status;
    private Long processedBy;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ModelUpdateRequestResponse(Long id, Long userId, String userNickname, Long modelId, String modelName,
                                     String name, String provider, String description, ModelCategory category,
                                     List<String> capabilities, BigDecimal inputPricePerToken,
                                     BigDecimal outputPricePerToken, Integer maxTokens, Boolean hasFreeTier,
                                     String apiEndpoint, String documentationUrl, String reason,
                                     UpdateRequestStatus status, Long processedBy, LocalDateTime processedAt,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
        this.modelId = modelId;
        this.modelName = modelName;
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
        this.reason = reason;
        this.status = status;
        this.processedBy = processedBy;
        this.processedAt = processedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ModelUpdateRequestResponse of(ModelUpdateRequest request) {
        return ModelUpdateRequestResponse.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .userNickname(request.getUser().getNickname())
                .modelId(request.getModel().getId())
                .modelName(request.getModel().getName())
                .name(request.getName())
                .provider(request.getProvider())
                .description(request.getDescription())
                .category(request.getCategory())
                .capabilities(request.getCapabilities())
                .inputPricePerToken(request.getInputPricePerToken())
                .outputPricePerToken(request.getOutputPricePerToken())
                .maxTokens(request.getMaxTokens())
                .hasFreeTier(request.getHasFreeTier())
                .apiEndpoint(request.getApiEndpoint())
                .documentationUrl(request.getDocumentationUrl())
                .reason(request.getReason())
                .status(request.getStatus())
                .processedBy(request.getProcessedBy())
                .processedAt(request.getProcessedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}

