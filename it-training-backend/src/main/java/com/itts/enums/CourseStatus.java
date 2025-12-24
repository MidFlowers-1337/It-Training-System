package com.itts.enums;

import lombok.Getter;

/**
 * 课程状态枚举
 */
@Getter
public enum CourseStatus {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布");

    private final int code;
    private final String desc;

    CourseStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CourseStatus fromCode(int code) {
        for (CourseStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断课程是否已发布
     */
    public boolean isPublished() {
        return this == PUBLISHED;
    }
}
