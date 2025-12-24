package com.itts.enums;

import lombok.Getter;

/**
 * 学习进度状态枚举
 */
@Getter
public enum LearningStatus {

    NOT_STARTED("not_started", "未开始"),
    IN_PROGRESS("in_progress", "进行中"),
    COMPLETED("completed", "已完成");

    private final String code;
    private final String desc;

    LearningStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static LearningStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (LearningStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否已完成
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * 判断是否进行中
     */
    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }
}
