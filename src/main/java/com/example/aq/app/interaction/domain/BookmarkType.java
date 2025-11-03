package com.example.aq.app.interaction.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookmarkType {
    REVIEW("리뷰"),
    RECIPE("레시피"),
    MODEL("모델");

    private final String description;
}
