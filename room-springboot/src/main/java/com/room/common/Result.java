package com.room.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T> 数据类型
 */
@Setter
@Getter
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // 状态码
    private int code;
    // 提示信息
    private String message;
    // 返回数据
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功返回结果，带数据
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    // 成功返回结果，不带数据
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    // 失败返回结果，带自定义错误信息
    public static <T> Result<T> failed(String message) {
        return new Result<>(ResultCode.FAILED.getCode(), message, null);
    }

    // 失败返回结果，使用默认错误信息
    public static <T> Result<T> failed() {
        return new Result<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), null);
    }

    // 参数验证失败返回结果，带自定义错误信息
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    // 参数验证失败返回结果，使用默认错误信息
    public static <T> Result<T> validateFailed() {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), ResultCode.VALIDATE_FAILED.getMessage(), null);
    }

    // 未登录返回结果
    public static <T> Result<T> unauthorized(T data) {
        return new Result<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    // 未授权返回结果
    public static <T> Result<T> forbidden(T data) {
        return new Result<>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    // 状态码枚举类
    @Getter
    public enum ResultCode {
        SUCCESS(200, "操作成功"),
        FAILED(500, "操作失败"),
        VALIDATE_FAILED(400, "参数检验失败"),
        UNAUTHORIZED(401, "暂未登录或 token 已经过期"),
        FORBIDDEN(403, "没有相关权限");

        private final int code;
        private final String message;

        ResultCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

    }
}