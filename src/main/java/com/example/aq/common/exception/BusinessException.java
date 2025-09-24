package com.example.aq.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final Object[] args;

    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
        this.args = new Object[0];
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.args = new Object[0];
    }

    public BusinessException(String code, String message, Object... args) {
        super(message);
        this.code = code;
        this.args = args;
    }
}
