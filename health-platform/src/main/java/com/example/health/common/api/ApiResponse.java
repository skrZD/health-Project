package com.example.health.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;           // 0 成功；其他为错误码
    private String message;     // 提示信息
    private T data;             // 业务数据
    private long timestamp;     // 服务器时间戳（毫秒）

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .code(0)
                .message("ok")
                .data(data)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        if (code == 0) {
            code = 5000; // 防止误传 0
        }
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    // 添加 success 方法（与 ok 方法功能相同）
    public static <T> ApiResponse<T> success(T data) {
        return ok(data);
    }

    public static <T> ApiResponse<T> success() {
        return ok();
    }

    // 添加 error 方法（与 fail 方法功能相同）
    public static <T> ApiResponse<T> error(String message) {
        return fail(5000, message);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return fail(code, message);
    }
}




