package com.itts.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatus {

    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final int code;
    private final String desc;

    UserStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserStatus fromCode(int code) {
        for (UserStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断用户是否启用
     */
    public boolean isEnabled() {
        return this == ENABLED;
    }
}
