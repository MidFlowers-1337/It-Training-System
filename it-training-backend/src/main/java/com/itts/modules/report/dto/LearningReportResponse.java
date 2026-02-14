package com.itts.modules.report.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 学习报告响应 DTO
 */
@Data
public class LearningReportResponse {

    /**
     * 报告周期类型: weekly, monthly, yearly
     */
    private String periodType;

    /**
     * 报告开始日期
     */
    private LocalDate startDate;

    /**
     * 报告结束日期
     */
    private LocalDate endDate;

    // ==================== 学习概览 ====================

    /**
     * 总学习时长(分钟)
     */
    private Integer totalStudyMinutes;

    /**
     * 学习天数
     */
    private Integer studyDays;

    /**
     * 完成课程数
     */
    private Integer completedCourses;

    /**
     * 获得成就数
     */
    private Integer earnedAchievements;

    /**
     * 连续打卡天数
     */
    private Integer streakDays;

    /**
     * 平均每日学习时长(分钟)
     */
    private Integer avgDailyMinutes;

    // ==================== 对比数据 ====================

    /**
     * 与上期对比 - 学习时长变化百分比
     */
    private Integer studyTimeChangePercent;

    /**
     * 与上期对比 - 学习天数变化
     */
    private Integer studyDaysChange;

    /**
     * 与上期对比 - 完成课程变化
     */
    private Integer completedCoursesChange;

    // ==================== 学习趋势 ====================

    /**
     * 每日学习时长趋势 (日期 -> 分钟)
     */
    private List<DailyStudyItem> dailyStudyTrend;

    /**
     * 学习时段分布 (小时 -> 分钟)
     */
    private Map<Integer, Integer> hourlyDistribution;

    /**
     * 课程类别学习分布
     */
    private List<CategoryStudyItem> categoryDistribution;

    // ==================== 课程详情 ====================

    /**
     * 学习中的课程
     */
    private List<CourseStudyItem> inProgressCourses;

    /**
     * 已完成的课程
     */
    private List<CourseStudyItem> completedCourseList;

    // ==================== 成就与里程碑 ====================

    /**
     * 本期获得的成就
     */
    private List<AchievementItem> newAchievements;

    /**
     * 学习里程碑
     */
    private List<MilestoneItem> milestones;

    // ==================== 建议 ====================

    /**
     * 学习建议
     */
    private List<String> suggestions;

    /**
     * 每日学习项
     */
    @Data
    public static class DailyStudyItem {
        private LocalDate date;
        private Integer minutes;
        private Boolean checkedIn;
    }

    /**
     * 类别学习项
     */
    @Data
    public static class CategoryStudyItem {
        private String category;
        private String categoryName;
        private Integer minutes;
        private Integer percent;
        private Integer courseCount;
    }

    /**
     * 课程学习项
     */
    @Data
    public static class CourseStudyItem {
        private Long courseId;
        private String courseName;
        private String category;
        private Integer progressPercent;
        private Integer studyMinutes;
        private LocalDate lastStudyDate;
    }

    /**
     * 成就项
     */
    @Data
    public static class AchievementItem {
        private Long id;
        private String name;
        private String description;
        private String iconUrl;
        private Integer points;
        private LocalDate earnedDate;
    }

    /**
     * 里程碑项
     */
    @Data
    public static class MilestoneItem {
        private String type;
        private String title;
        private String description;
        private LocalDate achievedDate;
    }
}