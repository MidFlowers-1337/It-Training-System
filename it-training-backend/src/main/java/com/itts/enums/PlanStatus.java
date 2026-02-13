package com.itts.enums;

import lombok.Getter;

/**
 * 学习计划状态枚举
 * 替代 LearningPlanServiceImpl / LearningReportServiceImpl 中的魔法字符串
 *
 * [Phase 1.4] 消除 "active"、"paused"、"completed"、"canceled" 等硬编码字符串
 */
@Getter
public enum PlanStatus {

    ACTIVE("active", "进行中"),
    PAUSED("paused", "已暂停"),
    COMPLETED("completed", "已完成"),
    CANCELED("canceled", "已取消");

    private final String code;
    private final String desc;

    PlanStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 从字符串 code 转换为枚举
     *
     * @param code 状态代码
     * @return 对应枚举值，不存在则返回 null
     */
    public static PlanStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (PlanStatus status : values()) {
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
    public boolean isActive() {
        return this == ACTIVE;
    }
}
