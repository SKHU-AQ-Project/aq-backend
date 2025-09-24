package com.example.aq.model.dto;

import com.example.aq.domain.model.ModelCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double averageRating;
    private Long reviewCount;
    private Boolean isBookmarked;
}
