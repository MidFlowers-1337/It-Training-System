package com.itts.modules.learning.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用户学习统计响应 DTO
 */
@Data
public class UserStatsResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    // ========== 总体统计 ==========

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
     * 课程完成率
     */
    private Double completionRate;

    // ========== 连续学习统计 ==========

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
     * 最后学习日期
     */
    private LocalDate lastStudyDate;

    // ========== 成就统计 ==========

    /**
     * 总成就积分
     */
    private Integer totalAchievementPoints;

    /**
     * 已获得成就数
     */
    private Integer achievementsEarned;

    /**
     * 总成就数
     */
    private Integer totalAchievements;

    // ========== 时间分布统计 ==========

    /**
     * 本周学习时长(分钟)
     */
    private Integer weeklyStudyMinutes;

    /**
     * 本月学习时长(分钟)
     */
    private Integer monthlyStudyMinutes;

    /**
     * 按类别学习时长分布
     */
    private Map<String, Integer> studyByCategory;

    /**
     * 最近30天学习趋势
     */
    private List<DailyStudyTrend> last30DaysTrend;

    /**
     * 每日学习趋势项
     */
    @Data
    public static class DailyStudyTrend {
        private LocalDate date;
        private Integer studyMinutes;
        private Boolean checkedIn;
    }

    // ========== 排名信息 ==========

    /**
     * 学习时长排名
     */
    private Integer studyTimeRank;

    /**
     * 连续打卡排名
     */
    private Integer streakRank;

    /**
     * 成就积分排名
     */
    private Integer achievementRank;
}