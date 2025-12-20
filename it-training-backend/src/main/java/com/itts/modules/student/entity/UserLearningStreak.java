package com.itts.modules.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习打卡实体
 */
@Data
@TableName("user_learning_streak")
public class UserLearningStreak implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 连续学习天数
     */
    private Integer streakDays;

    /**
     * 最大连续天数
     */
    private Integer maxStreakDays;

    /**
     * 最后打卡日期
     */
    private LocalDate lastCheckinDate;

    /**
     * 总打卡天数
     */
    private Integer totalCheckinDays;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
