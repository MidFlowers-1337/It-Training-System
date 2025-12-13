package com.itts.modules.learning.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习仪表盘响应 DTO
 */
@Data
public class LearningDashboardResponse {

    /**
     * 总学习时长(分钟)
     */
    private Integer totalStudyMinutes;

    /**
     * 总学习时长（格式化）
     */
    private String totalStudyFormatted;

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
     * 今日是否已打卡
     */
    private Boolean todayCheckedIn;

    /**
     * 进行中的课程列表
     */
    private List<LearningProgressResponse> inProgressCourses;

    /**
     * 最近获得的成就
     */
    private List<AchievementResponse> recentAchievements;

    /**
     * 本周学习时长（按天）
     */
    private List<DailyStudyItem> weeklyStudyData;

    /**
     * 每日学习数据项
     */
    @Data
    public static class DailyStudyItem {
        private LocalDate date;
        private String dayOfWeek;
        private Integer studyMinutes;
    }
}