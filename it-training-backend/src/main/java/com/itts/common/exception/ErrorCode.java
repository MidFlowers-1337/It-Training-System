package com.itts.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 错误码枚举
 * 每个业务错误码都映射到对应的 HTTP 状态码，使 API 语义更清晰
 */
@Getter
public enum ErrorCode {

    // 通用错误 - 直接映射 HTTP 状态码
    SUCCESS(200, "操作成功", HttpStatus.OK),
    FAIL(500, "操作失败", HttpStatus.INTERNAL_SERVER_ERROR),
    PARAM_ERROR(400, "参数错误", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "未授权", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "禁止访问", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "资源不存在", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(405, "方法不允许", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_ERROR(500, "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR),

    // 认证错误 40xx - 认证相关统一用 401
    USER_NOT_FOUND(4001, "用户不存在", HttpStatus.NOT_FOUND),
    USERNAME_EXISTS(4002, "用户名已存在", HttpStatus.CONFLICT),
    PASSWORD_ERROR(4003, "用户名或密码错误", HttpStatus.UNAUTHORIZED),
    PASSWORD_NOT_MATCH(4007, "两次输入的密码不一致", HttpStatus.BAD_REQUEST),
    ACCOUNT_DISABLED(4004, "账户已被禁用，请联系管理员", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED(4005, "登录已过期，请重新登录", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(4006, "无效的Token", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED(4008, "账号已锁定，请稍后重试", HttpStatus.TOO_MANY_REQUESTS),

    // 课程错误 402x - 资源冲突用 409，不存在用 404，业务规则用 422
    COURSE_CODE_EXISTS(4020, "课程编码已存在", HttpStatus.CONFLICT),
    COURSE_HAS_SESSION(4021, "该课程下存在班期，无法删除", HttpStatus.UNPROCESSABLE_ENTITY),
    COURSE_NOT_FOUND(4022, "课程不存在", HttpStatus.NOT_FOUND),
    CHAPTER_NOT_FOUND(4023, "章节不存在", HttpStatus.NOT_FOUND),
    CHAPTER_NOT_BELONG_TO_COURSE(4024, "章节不属于指定课程", HttpStatus.BAD_REQUEST),

    // 班期错误 403x
    SESSION_NOT_ENROLLABLE(4030, "该班期暂不可报名", HttpStatus.UNPROCESSABLE_ENTITY),
    SESSION_CODE_EXISTS(4031, "班期编码已存在", HttpStatus.CONFLICT),
    SESSION_NOT_FOUND(4032, "班期不存在", HttpStatus.NOT_FOUND),
    SESSION_HAS_ENROLLMENT(4033, "该班期已有学员报名，无法删除", HttpStatus.UNPROCESSABLE_ENTITY),

    // 报名错误 404x - 名额满用 409 冲突，重复用 409，不存在用 404，业务规则用 422
    ENROLLMENT_QUOTA_FULL(4040, "报名失败，名额已满", HttpStatus.CONFLICT),
    ENROLLMENT_DUPLICATE(4041, "您已报名该班期", HttpStatus.CONFLICT),
    ENROLLMENT_NOT_FOUND(4042, "报名记录不存在", HttpStatus.NOT_FOUND),
    ENROLLMENT_CANNOT_CANCEL(4044, "班期已开始，无法取消", HttpStatus.UNPROCESSABLE_ENTITY),

    // AI 错误 405x - 外部服务不可用用 503
    AI_SERVICE_ERROR(4050, "AI服务暂时不可用", HttpStatus.SERVICE_UNAVAILABLE),

    // 通知错误 406x
    NOTIFICATION_NOT_FOUND(4060, "通知不存在", HttpStatus.NOT_FOUND),

    // 成就错误 407x
    ACHIEVEMENT_NOT_FOUND(4070, "成就不存在", HttpStatus.NOT_FOUND),
    ACHIEVEMENT_CODE_EXISTS(4071, "成就编码已存在", HttpStatus.CONFLICT),

    // 限流错误 429 - Too Many Requests
    RATE_LIMIT_EXCEEDED(429, "请求过于频繁，请稍后再试", HttpStatus.TOO_MANY_REQUESTS);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
