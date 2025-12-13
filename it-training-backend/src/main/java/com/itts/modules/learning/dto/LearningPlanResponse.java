package com.itts.modules.learning.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习计划响应 DTO
 */
@Data
public class LearningPlanResponse {

    /**
     * 计划ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 计划描述
     */
    private String description;

    /**
     * 目标课程列表
     */
    private List<PlanCourseItem> targetCourses;

    /**
     * 计划开始日期
     */
    private LocalDate startDate;

    /**
     * 计划结束日期
     */
    private LocalDate endDate;

    /**
     * 每日目标学习时长(分钟)
     */
    private Integer dailyTargetMinutes;

    /**
     * 每周目标学习天数
     */
    private Integer weeklyTargetDays;

    /**
     * 计划状态: active, completed, paused, cancelled
     */
    private String status;

    /**
     * 完成进度百分比 (0-100)
     */
    private Integer progressPercent;

    /**
     * 已完成课程数
     */
    private Integer completedCourses;

    /**
     * 总课程数
     */
    private Integer totalCourses;

    /**
     * 剩余天数
     */
    private Integer remainingDays;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 计划课程项
     */
    @Data
    public static class PlanCourseItem {
        private Long courseId;
        private String courseName;
        private String category;
        private Integer progressPercent;
        private Boolean completed;
    }
}