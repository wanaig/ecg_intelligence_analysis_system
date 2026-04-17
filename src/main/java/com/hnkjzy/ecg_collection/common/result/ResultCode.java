package com.hnkjzy.ecg_collection.common.result;

import lombok.Getter;

/**
 * 统一状态码定义。
 */
@Getter
public enum ResultCode {
    SUCCESS(0, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证或登录已失效"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    VALIDATION_ERROR(422, "参数校验失败"),
    BUSINESS_ERROR(1000, "业务异常"),
    TOKEN_INVALID(1001, "Token 无效"),
    TOKEN_EXPIRED(1002, "Token 已过期"),
    INTERNAL_ERROR(5000, "系统繁忙，请稍后重试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
