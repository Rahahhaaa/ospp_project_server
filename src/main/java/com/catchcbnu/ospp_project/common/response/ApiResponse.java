package com.catchcbnu.ospp_project.common.response;

import org.springframework.http.HttpStatus;

public class ApiResponse<T> {

    private final String status;
    private final String message;
    private final T data;

    private ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus.name(), message, data);
    }

    public static ApiResponse<Void> error(HttpStatus httpStatus, String message) {
        return new ApiResponse<>(httpStatus.name(), message, null);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
