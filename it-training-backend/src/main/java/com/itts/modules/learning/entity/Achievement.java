package com.itts.modules.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成就定义实体
 */
@Data
@TableName("achievement")
public class Achievement {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 成就编码
     */
    private String code;

    /**
     * 成就名称
     */
    private String name;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 图标URL
     */
    private String iconUrl;

    /**
     * 分类: general-通用, streak-连续, course-课程, skill-技能
     */
    private String category;

    /**
     * 条件类型: streak_days-连续天数, courses_completed-完成课程数, hours_studied-学习时长
     */
    private String conditionType;

    /**
     * 条件值
     */
    private Integer conditionValue;

    /**
     * 成就积分
     */
    private Integer points;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}