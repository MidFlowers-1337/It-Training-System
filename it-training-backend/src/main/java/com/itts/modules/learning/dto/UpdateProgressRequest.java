package com.itts.modules.learning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新学习进度请求 DTO
 */
@Data
public class UpdateProgressRequest {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 进度百分比 (0-100)
     */
    @Min(value = 0, message = "进度不能小于0")
    @Max(value = 100, message = "进度不能大于100")
    private Integer progressPercent;

    /**
     * 本次学习时长(分钟)
     */
    @Min(value = 0, message = "学习时长不能为负数")
    private Integer studyMinutes;

    /**
     * 最后学习章节/位置
     */
    private String lastPosition;

    /**
     * 学习笔记
     */
    private String notes;
}