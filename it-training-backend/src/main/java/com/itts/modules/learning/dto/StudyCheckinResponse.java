package com.itts.modules.learning.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习打卡响应 DTO
 */
@Data
public class StudyCheckinResponse {

    /**
     * 打卡ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 打卡日期
     */
    private LocalDate checkinDate;

    /**
     * 学习时长(分钟)
     */
    private Integer studyMinutes;

    /**
     * 学习时长（格式化）
     */
    private String studyDurationFormatted;

    /**
     * 学习内容/笔记
     */
    private String studyContent;

    /**
     * 心情: happy, normal, tired, frustrated
     */
    private String mood;

    /**
     * 心情图标
     */
    private String moodIcon;

    /**
     * 关联课程ID
     */
    private Long courseId;

    /**
     * 关联课程名称
     */
    private String courseName;

    /**
     * 当前连续打卡天数
     */
    private Integer currentStreak;

    /**
     * 是否获得新成就
     */
    private Boolean newAchievementEarned;

    /**
     * 新获得的成就
     */
    private AchievementResponse newAchievement;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}