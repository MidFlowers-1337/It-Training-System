package com.itts.common.exception;

import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode {

    // 通用错误 1xxx
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 认证错误 40xx
    USER_NOT_FOUND(4001, "用户不存在"),
    USERNAME_EXISTS(4002, "用户名已存在"),
    PASSWORD_ERROR(4003, "用户名或密码错误"),
    PASSWORD_NOT_MATCH(4007, "两次输入的密码不一致"),
    ACCOUNT_DISABLED(4004, "账户已被禁用，请联系管理员"),
    TOKEN_EXPIRED(4005, "登录已过期，请重新登录"),
    TOKEN_INVALID(4006, "无效的Token"),

    // 课程错误 402x
    COURSE_CODE_EXISTS(4020, "课程编码已存在"),
    COURSE_HAS_SESSION(4021, "该课程下存在班期，无法删除"),
    COURSE_NOT_FOUND(4022, "课程不存在"),

    // 班期错误 403x
    SESSION_NOT_ENROLLABLE(4030, "该班期暂不可报名"),
    SESSION_CODE_EXISTS(4031, "班期编码已存在"),
    SESSION_NOT_FOUND(4032, "班期不存在"),
    SESSION_HAS_ENROLLMENT(4033, "该班期已有学员报名，无法删除"),

    // 报名错误 404x
    ENROLLMENT_QUOTA_FULL(4040, "报名失败，名额已满"),
    ENROLLMENT_DUPLICATE(4041, "您已报名该班期"),
    ENROLLMENT_NOT_FOUND(4042, "报名记录不存在"),
    ENROLLMENT_CANNOT_CANCEL(4044, "班期已开始，无法取消"),

    // AI 错误 405x
    AI_SERVICE_ERROR(4050, "AI服务暂时不可用");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
