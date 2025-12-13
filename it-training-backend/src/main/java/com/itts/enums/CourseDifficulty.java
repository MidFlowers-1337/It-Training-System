package com.itts.enums;

import lombok.Getter;

/**
 * 课程难度枚举
 */
@Getter
public enum CourseDifficulty {

    ENTRY(1, "入门"),
    BEGINNER(2, "初级"),
    INTERMEDIATE(3, "中级"),
    ADVANCED(4, "高级");

    private final int code;
    private final String desc;

    CourseDifficulty(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CourseDifficulty fromCode(int code) {
        for (CourseDifficulty difficulty : values()) {
            if (difficulty.getCode() == code) {
                return difficulty;
            }
        }
        return null;
    }
}
