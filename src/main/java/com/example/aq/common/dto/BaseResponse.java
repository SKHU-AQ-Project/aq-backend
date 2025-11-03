package com.example.aq.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;

    @Builder
    public BaseResponse(boolean success, String message, T data, String error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> error(String message) {
        return BaseResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> BaseResponse<T> error(String message, String error) {
        return BaseResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .build();
    }
}
