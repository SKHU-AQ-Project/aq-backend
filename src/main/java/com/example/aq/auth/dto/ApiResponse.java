package com.example.aq.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String code;

    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(true, "요청이 성공했습니다.", data, "SUCCESS");
    }

    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, message, data, code);
    }
}
