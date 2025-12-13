package com.itts.modules.learning.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 学习打卡请求 DTO
 */
@Data
public class StudyCheckinRequest {

    /**
     * 课程ID（可选，如果指定则记录到该课程）
     */
    private Long courseId;

    /**
     * 学习时长(分钟)
     */
    @Min(value = 1, message = "学习时长至少1分钟")
    private Integer studyMinutes;

    /**
     * 学习内容/笔记
     */
    private String studyContent;

    /**
     * 心情: happy, normal, tired, frustrated
     */
    private String mood;
}