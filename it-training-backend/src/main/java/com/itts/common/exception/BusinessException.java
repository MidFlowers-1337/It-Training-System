package com.itts.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务异常
 * 支持业务错误码和 HTTP 状态码映射
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * HTTP 状态码（用于响应头）
     */
    private final HttpStatus httpStatus;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        // 根据 code 范围推断 HTTP 状态码
        this.httpStatus = inferHttpStatus(code);
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getHttpStatus();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
        this.httpStatus = errorCode.getHttpStatus();
    }

    /**
     * 根据业务错误码推断 HTTP 状态码
     * 用于兼容直接传 code 的构造方法
     */
    private static HttpStatus inferHttpStatus(int code) {
        if (code >= 200 && code < 300) {
            return HttpStatus.OK;
        } else if (code == 400 || (code >= 4000 && code < 4010)) {
            return HttpStatus.BAD_REQUEST;
        } else if (code == 401 || code == 4003 || code == 4005 || code == 4006) {
            return HttpStatus.UNAUTHORIZED;
        } else if (code == 403 || code == 4004) {
            return HttpStatus.FORBIDDEN;
        } else if (code == 404 || code == 4001 || code == 4022 || code == 4032 || code == 4042) {
            return HttpStatus.NOT_FOUND;
        } else if (code == 4002 || code == 4020 || code == 4031 || code == 4040 || code == 4041) {
            return HttpStatus.CONFLICT;
        } else if (code == 4050) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (code >= 4020 && code < 4050) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
