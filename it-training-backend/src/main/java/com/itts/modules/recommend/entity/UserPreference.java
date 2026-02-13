package com.itts.modules.recommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户学习偏好实体
 */
@Data
@TableName("user_preference")
public class UserPreference {

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
     * 偏好分类JSON数组
     */
    private String preferredCategories;

    /**
     * 偏好难度(1-4)
     */
    private Integer preferredDifficulty;

    /**
     * 学习目标
     */
    private String learningGoal;

    /**
     * 每周学习时长目标(小时)
     */
    private Integer weeklyHours;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}