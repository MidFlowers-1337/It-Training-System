package com.itts.enums;

import lombok.Getter;

/**
 * 报名状态枚举
 */
@Getter
public enum EnrollmentStatus {

    ENROLLED(0, "已报名"),
    CANCELED(1, "已取消");

    private final int code;
    private final String desc;

    EnrollmentStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnrollmentStatus fromCode(int code) {
        for (EnrollmentStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}
