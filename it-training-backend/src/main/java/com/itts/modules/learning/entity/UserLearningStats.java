package com.itts.modules.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户学习统计实体
 */
@Data
@TableName("user_learning_stats")
public class UserLearningStats {

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
     * 总学习时长(分钟)
     */
    private Integer totalStudyMinutes;

    /**
     * 报名课程总数
     */
    private Integer totalCoursesEnrolled;

    /**
     * 完成课程总数
     */
    private Integer totalCoursesCompleted;

    /**
     * 当前连续学习天数
     */
    private Integer currentStreakDays;

    /**
     * 最长连续学习天数
     */
    private Integer maxStreakDays;

    /**
     * 总打卡天数
     */
    private Integer totalCheckinDays;

    /**
     * 总成就积分
     */
    private Integer totalAchievementPoints;

    /**
     * 最后学习日期
     */
    private LocalDate lastStudyDate;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}