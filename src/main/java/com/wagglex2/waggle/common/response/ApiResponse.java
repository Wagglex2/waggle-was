package com.wagglex2.waggle.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final String code;
    private final String message;
    private final T data;

    // ----- 성공 ----- //
    public static <T> ApiResponse<T> ok() { return of ("SUCCESS", "성공", null); }

    public static <T> ApiResponse<T> ok(String message) { return of ("SUCCESS", message, null); }

    public static <T> ApiResponse<T> ok(T data) {
        return of("SUCCESS", "성공", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return of("SUCCESS", message, data);
    }

    // ----- 실패 (단일 메시지) ----- //
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return of(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return of(code, message, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return of(errorCode.getCode(), errorCode.getMessage(), data);
    }
}
