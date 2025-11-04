package com.example.aq.app.model.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelProposalStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거절됨");

    private final String description;
}

