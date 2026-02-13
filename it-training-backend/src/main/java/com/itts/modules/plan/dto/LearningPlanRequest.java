package com.itts.modules.plan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 100, message = "计划名称不能超过100个字符")
    private String planName;

    /**
     * 计划描述
     */
    @Size(max = 500, message = "计划描述不能超过500个字符")
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
    @Min(value = 1, message = "每日目标学习时长至少1分钟")
    @Max(value = 1440, message = "每日目标学习时长不能超过1440分钟")
    private Integer dailyTargetMinutes;

    /**
     * 每周目标学习天数
     */
    @Min(value = 1, message = "每周目标学习天数至少1天")
    @Max(value = 7, message = "每周目标学习天数不能超过7天")
    private Integer weeklyTargetDays;
}