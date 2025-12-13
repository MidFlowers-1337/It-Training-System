package com.itts.modules.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习进度实体
 */
@Data
@TableName("learning_progress")
public class LearningProgress {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 进度百分比(0-100)
     */
    private Integer progressPercent;

    /**
     * 累计学习时长(分钟)
     */
    private Integer studyDurationMinutes;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastStudyAt;

    /**
     * 状态: not_started-未开始, in_progress-进行中, completed-已完成
     */
    private String status;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}