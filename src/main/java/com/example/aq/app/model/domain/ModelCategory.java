package com.example.aq.app.model.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelCategory {
    TEXT_GENERATION("텍스트 생성"),
    CODE_GENERATION("코드 생성"),
    TRANSLATION("번역"),
    SUMMARIZATION("요약"),
    QUESTION_ANSWERING("질문 답변"),
    CREATIVE_WRITING("창작"),
    ANALYSIS("분석"),
    MULTIMODAL("멀티모달");

    private final String description;
}
