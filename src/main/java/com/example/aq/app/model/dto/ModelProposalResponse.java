package com.example.aq.app.model.dto;

import com.example.aq.app.model.domain.ModelCategory;
import com.example.aq.app.model.domain.ModelProposal;
import com.example.aq.app.model.domain.ModelProposalStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ModelProposalResponse {
    private Long id;
    private Long userId;
    private String userNickname;
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
    private ModelProposalStatus status;
    private Integer likeCount;
    private String rejectionReason;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private Long modelId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isLiked; // 현재 사용자가 좋아요를 눌렀는지 여부

    @Builder
    public ModelProposalResponse(Long id, Long userId, String userNickname, String name, String provider,
                                String description, ModelCategory category, List<String> capabilities,
                                BigDecimal inputPricePerToken, BigDecimal outputPricePerToken, Integer maxTokens,
                                Boolean hasFreeTier, String apiEndpoint, String documentationUrl,
                                ModelProposalStatus status, Integer likeCount, String rejectionReason,
                                Long approvedBy, LocalDateTime approvedAt, Long modelId,
                                LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isLiked) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
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
        this.status = status;
        this.likeCount = likeCount;
        this.rejectionReason = rejectionReason;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.modelId = modelId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isLiked = isLiked;
    }

    public static ModelProposalResponse of(ModelProposal proposal, Boolean isLiked) {
        return ModelProposalResponse.builder()
                .id(proposal.getId())
                .userId(proposal.getUser().getId())
                .userNickname(proposal.getUser().getNickname())
                .name(proposal.getName())
                .provider(proposal.getProvider())
                .description(proposal.getDescription())
                .category(proposal.getCategory())
                .capabilities(proposal.getCapabilities())
                .inputPricePerToken(proposal.getInputPricePerToken())
                .outputPricePerToken(proposal.getOutputPricePerToken())
                .maxTokens(proposal.getMaxTokens())
                .hasFreeTier(proposal.getHasFreeTier())
                .apiEndpoint(proposal.getApiEndpoint())
                .documentationUrl(proposal.getDocumentationUrl())
                .status(proposal.getStatus())
                .likeCount(proposal.getLikeCount())
                .rejectionReason(proposal.getRejectionReason())
                .approvedBy(proposal.getApprovedBy())
                .approvedAt(proposal.getApprovedAt())
                .modelId(proposal.getModelId())
                .createdAt(proposal.getCreatedAt())
                .updatedAt(proposal.getUpdatedAt())
                .isLiked(isLiked)
                .build();
    }
}

