package com.example.aq.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User related errors
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다"),
    USER_ALREADY_EXISTS("USER_002", "이미 존재하는 사용자입니다"),
    INVALID_CREDENTIALS("USER_003", "잘못된 인증 정보입니다"),
    USER_DEACTIVATED("USER_004", "비활성화된 사용자입니다"),

    // Model related errors
    MODEL_NOT_FOUND("MODEL_001", "모델을 찾을 수 없습니다"),
    MODEL_INACTIVE("MODEL_002", "비활성화된 모델입니다"),

    // Review related errors
    REVIEW_NOT_FOUND("REVIEW_001", "리뷰를 찾을 수 없습니다"),
    REVIEW_ACCESS_DENIED("REVIEW_002", "리뷰에 접근할 권한이 없습니다"),
    REVIEW_ALREADY_EXISTS("REVIEW_003", "이미 작성된 리뷰가 있습니다"),

    // Recipe related errors
    RECIPE_NOT_FOUND("RECIPE_001", "레시피를 찾을 수 없습니다"),
    RECIPE_ACCESS_DENIED("RECIPE_002", "레시피에 접근할 권한이 없습니다"),

    // Comment related errors
    COMMENT_NOT_FOUND("COMMENT_001", "댓글을 찾을 수 없습니다"),
    COMMENT_ACCESS_DENIED("COMMENT_002", "댓글에 접근할 권한이 없습니다"),

    // Validation errors
    INVALID_INPUT("VALIDATION_001", "잘못된 입력입니다"),
    REQUIRED_FIELD_MISSING("VALIDATION_002", "필수 필드가 누락되었습니다"),

    // System errors
    INTERNAL_SERVER_ERROR("SYSTEM_001", "서버 내부 오류가 발생했습니다"),
    EXTERNAL_API_ERROR("SYSTEM_002", "외부 API 호출 중 오류가 발생했습니다"),
    DATABASE_ERROR("SYSTEM_003", "데이터베이스 오류가 발생했습니다");

    private final String code;
    private final String message;
}
