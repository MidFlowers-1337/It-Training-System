package com.itts.modules.learning.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成就响应 DTO
 */
@Data
public class AchievementResponse {

    /**
     * 成就ID
     */
    private Long id;

    /**
     * 成就名称
     */
    private String name;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 成就图标
     */
    private String icon;

    /**
     * 成就积分
     */
    private Integer points;

    /**
     * 成就类别
     */
    private String category;

    /**
     * 条件类型: streak_days, courses_completed, hours_studied
     */
    private String conditionType;

    /**
     * 条件值
     */
    private Integer conditionValue;

    /**
     * 是否已获得
     */
    private Boolean earned;

    /**
     * 获得时间
     */
    private LocalDateTime earnedAt;

    /**
     * 当前进度值
     */
    private Integer currentProgress;

    /**
     * 进度百分比 (0-100)
     */
    private Integer progressPercent;
}