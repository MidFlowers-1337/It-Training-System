package com.itts.modules.learning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习计划创建/更新请求 DTO
 */
@Data
public class LearningPlanRequest {

    /**
     * 计划名称
     */
    @NotBlank(message = "计划名称不能为空")
    private String planName;

    /**
     * 计划描述
     */
    private String description;

    /**
     * 目标课程ID列表
     */
    private List<Long> targetCourseIds;

    /**
     * 计划开始日期
     */
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    /**
     * 计划结束日期
     */
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    /**
     * 每日目标学习时长(分钟)
     */
    private Integer dailyTargetMinutes;

    /**
     * 每周目标学习天数
     */
    private Integer weeklyTargetDays;
}