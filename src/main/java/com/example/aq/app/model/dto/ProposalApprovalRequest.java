package com.example.aq.app.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProposalApprovalRequest {
    
    @NotNull(message = "승인 여부는 필수입니다")
    private Boolean approved;

    private String rejectionReason; // 거절 시 사유
}

