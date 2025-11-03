package com.example.aq.app.interaction.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LikeType {
    REVIEW("리뷰"),
    RECIPE("레시피"),
    COMMENT("댓글");

    private final String description;
}
