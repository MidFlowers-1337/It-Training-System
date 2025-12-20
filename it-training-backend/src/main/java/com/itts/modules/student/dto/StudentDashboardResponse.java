package com.itts.modules.student.dto;

import lombok.Data;

import java.util.List;

/**
 * 学生Dashboard响应DTO
 */
@Data
public class StudentDashboardResponse {

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 今日学习统计
     */
    private TodayStats todayStats;

    /**
     * 继续学习的课程
     */
    private ContinueLearning continueLearning;

    /**
     * 本周学习统计
     */
    private WeeklyStats weeklyStats;

    /**
     * 我的课程列表（最多显示4个）
     */
    private List<MyCourseItem> myCourses;

    /**
     * 最近解锁的成就（最多显示4个）
     */
    private List<AchievementItem> recentAchievements;

    @Data
    public static class UserInfo {
        private String username;
        private String realName;
        private Integer level;
        private Integer experience;
        private Integer nextLevelExp;
    }

    @Data
    public static class TodayStats {
        private Integer studyMinutes;
        private Integer streakDays;
        private Boolean checkedIn;
    }

    @Data
    public static class ContinueLearning {
        private Long courseId;
        private String courseName;
        private String coverImage;
        private Integer progressPercent;
        private String currentChapter;
        private Long currentChapterId;
    }

    @Data
    public static class WeeklyStats {
        private List<Integer> dailyMinutes; // 7天的学习分钟数
        private Integer totalMinutes;
    }

    @Data
    public static class MyCourseItem {
        private Long courseId;
        private String courseName;
        private String coverImage;
        private Integer progressPercent;
        private String status; // 进行中/已完成/未开始
    }

    @Data
    public static class AchievementItem {
        private Long achievementId;
        private String name;
        private String icon;
        private String unlockedAt;
    }
}
