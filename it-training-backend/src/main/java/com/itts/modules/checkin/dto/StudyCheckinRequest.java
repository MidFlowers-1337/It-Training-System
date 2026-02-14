package com.itts.modules.checkin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "学习时长不能为空")
    @Min(value = 1, message = "学习时长至少1分钟")
    private Integer studyMinutes;

    /**
     * 学习内容/笔记
     */
    @Size(max = 1000, message = "学习内容不能超过1000个字符")
    private String studyContent;

    /**
     * 心情: happy, normal, tired, frustrated
     */
    @Pattern(regexp = "^(happy|normal|tired|frustrated)$", message = "心情值必须是 happy、normal、tired 或 frustrated")
    private String mood;
}