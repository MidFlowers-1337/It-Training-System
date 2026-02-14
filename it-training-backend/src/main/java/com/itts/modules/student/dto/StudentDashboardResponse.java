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
        private Integer level;            // 等级 (1-8, 基于学习时长)
        private String levelName;         // 等级名称 (学习新手/初级学员/.../学习宗师)
        private Integer totalStudyMinutes; // 总学习分钟数
        private Integer nextLevelMinutes;  // 下一级所需分钟数
        private Integer experience;       // 累计经验值/积分 (保留作为积分系统)
        private Integer nextLevelExp;     // 保留兼容旧前端, 值=nextLevelMinutes
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
