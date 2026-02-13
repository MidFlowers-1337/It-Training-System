package com.itts.modules.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户画像响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 学习等级
     */
    private Integer learningLevel;

    /**
     * 学习等级名称
     */
    private String levelName;

    /**
     * 总学习时长（分钟）
     */
    private Integer totalStudyMinutes;

    /**
     * 完成课程数
     */
    private Integer completedCourses;

    /**
     * 进行中课程数
     */
    private Integer inProgressCourses;

    /**
     * 获得成就数
     */
    private Integer achievementCount;

    /**
     * 成就积分
     */
    private Integer achievementPoints;

    /**
     * 连续打卡天数
     */
    private Integer currentStreak;

    /**
     * 最长连续打卡天数
     */
    private Integer maxStreak;

    /**
     * 技能标签
     */
    private List<SkillTagItem> skillTags;

    /**
     * 学习偏好
     */
    private LearningPreference preference;

    /**
     * 学习能力雷达图数据
     */
    private AbilityRadar abilityRadar;

    /**
     * 学习时间分布
     */
    private List<TimeDistributionItem> timeDistribution;

    /**
     * 类别学习分布
     */
    private List<CategoryDistributionItem> categoryDistribution;

    /**
     * 学习里程碑
     */
    private List<MilestoneItem> milestones;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastStudyTime;

    /**
     * 技能标签项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillTagItem {
        private String tag;
        private Integer level;  // 1-5 熟练度
        private Integer courseCount;  // 相关课程数
    }

    /**
     * 学习偏好
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningPreference {
        private List<String> preferredCategories;
        private String preferredDifficulty;
        private Integer dailyStudyGoal;  // 每日学习目标（分钟）
        private String preferredStudyTime;  // 偏好学习时间段
    }

    /**
     * 能力雷达图数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AbilityRadar {
        private Integer learningSpeed;      // 学习速度 0-100
        private Integer persistence;        // 坚持度 0-100
        private Integer comprehension;      // 理解力 0-100
        private Integer practiceAbility;    // 实践能力 0-100
        private Integer breadth;            // 知识广度 0-100
        private Integer depth;              // 知识深度 0-100
    }

    /**
     * 时间分布项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeDistributionItem {
        private String timeSlot;  // 时间段，如 "06:00-08:00"
        private Integer minutes;  // 学习时长
        private Double percentage;  // 占比
    }

    /**
     * 类别分布项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDistributionItem {
        private String category;
        private String categoryName;
        private Integer courseCount;
        private Integer studyMinutes;
        private Double percentage;
    }

    /**
     * 里程碑项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MilestoneItem {
        private String title;
        private String description;
        private String icon;
        private LocalDateTime achievedAt;
    }
}