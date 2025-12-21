package com.itts.modules.student.dto;

import lombok.Data;

/**
 * 学生学习统计响应DTO
 */
@Data
public class StudentStatsResponse {

    /**
     * 总学习时长（分钟）
     */
    private Integer totalStudyMinutes;

    /**
     * 连续打卡天数
     */
    private Integer streakDays;

    /**
     * 最大连续打卡天数
     */
    private Integer maxStreakDays;

    /**
     * 总打卡天数
     */
    private Integer totalCheckinDays;

    /**
     * 已完成课程数
     */
    private Integer completedCourses;

    /**
     * 进行中课程数
     */
    private Integer inProgressCourses;

    /**
     * 已获得成就数
     */
    private Integer achievementsEarned;

    /**
     * 用户等级
     */
    private Integer level;

    /**
     * 当前经验值
     */
    private Integer experience;

    /**
     * 下一级所需经验值
     */
    private Integer nextLevelExp;
}
