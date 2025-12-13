package com.itts.enums;

import lombok.Getter;

/**
 * 班期状态枚举
 */
@Getter
public enum SessionStatus {

    NOT_OPEN(0, "未开放", false),
    ENROLLING(1, "报名中", true),
    FULL(2, "已满员", false),
    IN_PROGRESS(3, "进行中", false),
    ENDED(4, "已结束", false);

    private final int code;
    private final String desc;
    private final boolean enrollable;

    SessionStatus(int code, String desc, boolean enrollable) {
        this.code = code;
        this.desc = desc;
        this.enrollable = enrollable;
    }

    public static SessionStatus fromCode(int code) {
        for (SessionStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}
