package com.itts.modules.student.dto;

import lombok.Data;

/**
 * 学习打卡响应DTO
 */
@Data
public class CheckinResponse {

    /**
     * 是否打卡成功
     */
    private Boolean success;

    /**
     * 连续学习天数
     */
    private Integer streakDays;

    /**
     * 最大连续天数
     */
    private Integer maxStreakDays;

    /**
     * 总打卡天数
     */
    private Integer totalDays;

    /**
     * 是否获得新成就
     */
    private Boolean newAchievement;

    /**
     * 新成就名称
     */
    private String achievementName;

    /**
     * 奖励经验值
     */
    private Integer rewardExp;
}
