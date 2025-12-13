package com.itts.enums;

import lombok.Getter;

/**
 * 课程分类枚举
 */
@Getter
public enum CourseCategory {

    BACKEND("BACKEND", "后端开发"),
    FRONTEND("FRONTEND", "前端开发"),
    DATABASE("DATABASE", "数据库"),
    CLOUD("CLOUD", "云计算"),
    AI("AI", "人工智能"),
    OTHER("OTHER", "其他");

    private final String code;
    private final String desc;

    CourseCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CourseCategory fromCode(String code) {
        for (CourseCategory category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return null;
    }
}
