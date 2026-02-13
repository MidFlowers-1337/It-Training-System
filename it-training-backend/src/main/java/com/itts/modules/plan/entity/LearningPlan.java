package com.itts.modules.plan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习计划实体
 */
@Data
@TableName("learning_plan")
public class LearningPlan {

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
     * 计划名称
     */
    private String planName;

    /**
     * 计划描述
     */
    private String description;

    /**
     * 目标课程ID列表JSON
     */
    private String targetCourses;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 每周目标学习时长(小时)
     */
    private Integer weeklyGoalHours;

    /**
     * 状态: active-进行中, completed-已完成, paused-已暂停, canceled-已取消
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}