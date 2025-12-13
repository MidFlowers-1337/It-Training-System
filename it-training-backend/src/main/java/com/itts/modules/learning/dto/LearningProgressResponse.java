package com.itts.modules.learning.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习进度响应 DTO
 */
@Data
public class LearningProgressResponse {

    /**
     * 进度ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 报名记录ID
     */
    private Long enrollmentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程分类
     */
    private String courseCategory;

    /**
     * 课程难度
     */
    private Integer courseDifficulty;

    /**
     * 课程封面
     */
    private String courseCoverImage;

    /**
     * 进度百分比(0-100)
     */
    private Integer progressPercent;

    /**
     * 累计学习时长(分钟)
     */
    private Integer studyDurationMinutes;

    /**
     * 累计学习时长（格式化）
     */
    private String studyDurationFormatted;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastStudyAt;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}