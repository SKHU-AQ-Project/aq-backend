package com.example.aq.app.model.dto;

import com.example.aq.app.model.domain.ModelCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class ModelUpdateRequestDto {
    
    @NotNull(message = "모델 ID는 필수입니다")
    private Long modelId;

    @Size(max = 100, message = "모델 이름은 100자 이하여야 합니다")
    private String name;

    @Size(max = 50, message = "제공업체는 50자 이하여야 합니다")
    private String provider;

    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
    private String description;

    private ModelCategory category;

    private List<String> capabilities;

    private BigDecimal inputPricePerToken;

    private BigDecimal outputPricePerToken;

    private Integer maxTokens;

    private Boolean hasFreeTier;

    private String apiEndpoint;

    private String documentationUrl;

    @NotBlank(message = "수정 요청 사유는 필수입니다")
    @Size(max = 500, message = "수정 요청 사유는 500자 이하여야 합니다")
    private String reason;
}

