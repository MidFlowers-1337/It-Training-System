package com.itts.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.common.util.LevelDifficultyUtils;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.checkin.entity.StudyCheckin;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.checkin.mapper.StudyCheckinMapper;
import com.itts.modules.student.dto.StudentDashboardResponse;
import com.itts.modules.student.entity.UserChapterProgress;
import com.itts.modules.student.entity.UserLearningStreak;
import com.itts.modules.student.entity.UserLevel;
import com.itts.modules.student.mapper.UserChapterProgressMapper;
import com.itts.modules.student.mapper.UserLearningStreakMapper;
import com.itts.modules.student.mapper.UserLevelMapper;
import com.itts.modules.student.service.StudentDashboardService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学生 Dashboard 数据聚合服务
 * <p>
 * [Phase 6 #7] 从 StudentServiceImpl 拆分而来，专门负责 Dashboard 数据组装，
 * 降低 StudentServiceImpl 的职责复杂度和依赖数量。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private final SysUserMapper sysUserMapper;
    private final UserLevelMapper userLevelMapper;
    private final UserLearningStreakMapper userLearningStreakMapper;
    private final UserChapterProgressMapper userChapterProgressMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;
    private final com.itts.modules.course.mapper.CourseChapterMapper courseChapterMapper;
    private final LearningProgressMapper learningProgressMapper;
    private final StudyCheckinMapper studyCheckinMapper;
    private final com.itts.modules.achievement.service.AchievementService achievementService;
    private final com.itts.modules.learning.service.UserLearningStatsService userLearningStatsService;

    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");

    @Override
    public StudentDashboardResponse getDashboard(Long userId) {
        log.info("获取学生Dashboard数据, userId: {}", userId);

        StudentDashboardResponse response = new StudentDashboardResponse();

        // 1. 用户信息
        response.setUserInfo(getUserInfo(userId));

        // 2. 今日学习统计
        response.setTodayStats(getTodayStats(userId));

        // 3. 继续学习的课程
        response.setContinueLearning(getContinueLearning(userId));

        // 4. 本周学习统计
        response.setWeeklyStats(getWeeklyStats(userId));

        // 5. 我的课程列表（最多4个）
        response.setMyCourses(getMyCourses(userId, 4));

        // 6. 最近解锁的成就（最多4个）
        response.setRecentAchievements(getRecentAchievements(userId, 4));

        return response;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取用户信息
     */
    private StudentDashboardResponse.UserInfo getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        UserLevel userLevel = getOrCreateUserLevel(userId);

        // 等级基于学习时长计算
        com.itts.modules.learning.entity.UserLearningStats learningStats =
            userLearningStatsService.getOrCreateStats(userId);
        int totalMinutes = learningStats.getTotalStudyMinutes();
        int level = LevelDifficultyUtils.calculateLevelByMinutes(totalMinutes);
        String levelName = LevelDifficultyUtils.getLevelName(level);
        int nextLevelMinutes = LevelDifficultyUtils.getNextLevelMinutes(level);

        StudentDashboardResponse.UserInfo userInfo = new StudentDashboardResponse.UserInfo();
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setLevel(level);
        userInfo.setLevelName(levelName);
        userInfo.setTotalStudyMinutes(totalMinutes);
        userInfo.setNextLevelMinutes(nextLevelMinutes);
        userInfo.setExperience(userLevel.getExperience());
        userInfo.setNextLevelExp(nextLevelMinutes); // backward compat

        return userInfo;
    }

    /**
     * 获取今日学习统计
     */
    private StudentDashboardResponse.TodayStats getTodayStats(Long userId) {
        StudentDashboardResponse.TodayStats stats = new StudentDashboardResponse.TodayStats();

        // 从 study_checkin 表获取今日学习时长
        LocalDate today = LocalDate.now(CHINA_ZONE);
        LambdaQueryWrapper<StudyCheckin> checkinWrapper = new LambdaQueryWrapper<>();
        checkinWrapper.eq(StudyCheckin::getUserId, userId)
                .eq(StudyCheckin::getCheckinDate, today);

        StudyCheckin todayCheckin = studyCheckinMapper.selectOne(checkinWrapper);
        int todayMinutes = todayCheckin != null && todayCheckin.getStudyMinutes() != null
                ? todayCheckin.getStudyMinutes()
                : 0;

        stats.setStudyMinutes(todayMinutes);

        // 获取连续天数
        UserLearningStreak streak = getOrCreateStreak(userId);
        stats.setStreakDays(streak.getStreakDays());

        // 检查今日是否已打卡
        boolean checkedIn = streak.getLastCheckinDate() != null &&
                           streak.getLastCheckinDate().equals(today);
        stats.setCheckedIn(checkedIn);

        return stats;
    }

    /**
     * 获取继续学习的课程
     */
    private StudentDashboardResponse.ContinueLearning getContinueLearning(Long userId) {
        // 查找最近学习的课程
        List<LearningProgress> progressList = learningProgressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .lt(LearningProgress::getProgressPercent, 100)
                .orderByDesc(LearningProgress::getUpdatedAt)
        );

        LearningProgress progress = progressList.stream().findFirst().orElse(null);

        if (progress == null) {
            return null;
        }

        Course course = courseMapper.selectById(progress.getCourseId());
        if (course == null) {
            return null;
        }

        StudentDashboardResponse.ContinueLearning continueLearning =
            new StudentDashboardResponse.ContinueLearning();
        continueLearning.setCourseId(course.getId());
        continueLearning.setCourseName(course.getName());
        continueLearning.setCoverImage(course.getCoverImage());
        continueLearning.setProgressPercent(progress.getProgressPercent());

        // 获取当前正在学习的章节
        List<UserChapterProgress> chapterList = userChapterProgressMapper.selectList(
            new LambdaQueryWrapper<UserChapterProgress>()
                .eq(UserChapterProgress::getUserId, userId)
                .eq(UserChapterProgress::getCourseId, course.getId())
                .eq(UserChapterProgress::getCompleted, false)
                .orderByDesc(UserChapterProgress::getUpdatedAt)
        );

        UserChapterProgress chapterProgress = chapterList.stream().findFirst().orElse(null);

        if (chapterProgress != null) {
            // 获取章节信息
            com.itts.modules.course.entity.CourseChapter chapter =
                courseChapterMapper.selectById(chapterProgress.getChapterId());
            if (chapter != null) {
                continueLearning.setCurrentChapter(chapter.getTitle());
                continueLearning.setCurrentChapterId(chapter.getId());
            } else {
                continueLearning.setCurrentChapter("继续学习");
                continueLearning.setCurrentChapterId(null);
            }
        } else {
            continueLearning.setCurrentChapter("开始学习");
            continueLearning.setCurrentChapterId(null);
        }

        return continueLearning;
    }

    /**
     * 获取本周学习统计（从 study_checkin 表获取每日增量学习时长）
     */
    private StudentDashboardResponse.WeeklyStats getWeeklyStats(Long userId) {
        StudentDashboardResponse.WeeklyStats stats = new StudentDashboardResponse.WeeklyStats();

        List<Integer> dailyMinutes = new ArrayList<>();
        int totalMinutes = 0;

        LocalDate today = LocalDate.now(CHINA_ZONE);
        LocalDate weekStartDate = today.minusDays(6);

        List<StudyCheckin> weekCheckins = studyCheckinMapper.selectList(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .ge(StudyCheckin::getCheckinDate, weekStartDate)
                .le(StudyCheckin::getCheckinDate, today)
        );

        Map<LocalDate, Integer> dailyMinutesMap = weekCheckins.stream()
            .collect(Collectors.groupingBy(
                StudyCheckin::getCheckinDate,
                Collectors.summingInt(c -> c.getStudyMinutes() != null ? c.getStudyMinutes() : 0)
            ));

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int dayMinutes = dailyMinutesMap.getOrDefault(date, 0);
            dailyMinutes.add(dayMinutes);
            totalMinutes += dayMinutes;
        }

        stats.setDailyMinutes(dailyMinutes);
        stats.setTotalMinutes(totalMinutes);

        return stats;
    }

    /**
     * 获取我的课程列表（使用JOIN查询避免N+1问题）
     */
    private List<StudentDashboardResponse.MyCourseItem> getMyCourses(Long userId, int limit) {
        List<Enrollment> enrollmentsWithDetails = enrollmentMapper.selectUserEnrollmentsWithDetails(userId);

        List<Enrollment> filteredEnrollments = enrollmentsWithDetails.stream()
                .filter(e -> e.getStatus() != null && e.getStatus() == 0 && e.getCourseId() != null)
                .limit(limit)
                .collect(Collectors.toList());

        if (filteredEnrollments.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> courseIds = filteredEnrollments.stream()
                .map(Enrollment::getCourseId)
                .collect(Collectors.toSet());

        List<LearningProgress> progressList = learningProgressMapper.selectList(
                new LambdaQueryWrapper<LearningProgress>()
                        .eq(LearningProgress::getUserId, userId)
                        .in(LearningProgress::getCourseId, courseIds)
        );

        Map<Long, LearningProgress> progressMap = progressList.stream()
                .collect(Collectors.toMap(LearningProgress::getCourseId, p -> p, (a, b) -> a));

        return filteredEnrollments.stream().map(enrollment -> {
            StudentDashboardResponse.MyCourseItem item = new StudentDashboardResponse.MyCourseItem();
            item.setCourseId(enrollment.getCourseId());
            item.setCourseName(enrollment.getCourseName());
            item.setCoverImage(enrollment.getCoverImage());

            LearningProgress progress = progressMap.get(enrollment.getCourseId());
            if (progress != null) {
                item.setProgressPercent(progress.getProgressPercent());
                if (progress.getProgressPercent() >= 100) {
                    item.setStatus("已完成");
                } else if (progress.getProgressPercent() > 0) {
                    item.setStatus("进行中");
                } else {
                    item.setStatus("未开始");
                }
            } else {
                item.setProgressPercent(0);
                item.setStatus("未开始");
            }

            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 获取最近解锁的成就
     */
    private List<StudentDashboardResponse.AchievementItem> getRecentAchievements(Long userId, int limit) {
        List<com.itts.modules.achievement.dto.AchievementResponse> achievements =
            achievementService.getRecentAchievements(userId, limit);

        return achievements.stream().map(achievement -> {
            StudentDashboardResponse.AchievementItem item = new StudentDashboardResponse.AchievementItem();
            item.setAchievementId(achievement.getId());
            item.setName(achievement.getName());
            item.setIcon(achievement.getIcon() != null ? achievement.getIcon() : "\uD83C\uDFC6");
            item.setUnlockedAt(formatEarnedTime(achievement.getEarnedAt()));
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 格式化获得时间为相对时间
     */
    private String formatEarnedTime(LocalDateTime earnedAt) {
        if (earnedAt == null) {
            return "刚刚";
        }

        long hours = ChronoUnit.HOURS.between(earnedAt, LocalDateTime.now());
        long days = ChronoUnit.DAYS.between(earnedAt, LocalDateTime.now());

        if (hours < 1) {
            long minutes = ChronoUnit.MINUTES.between(earnedAt, LocalDateTime.now());
            return minutes <= 0 ? "刚刚" : minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 30) {
            return days + "天前";
        } else {
            long months = ChronoUnit.MONTHS.between(earnedAt, LocalDateTime.now());
            return months + "个月前";
        }
    }

    /**
     * 获取或创建用户等级记录
     */
    private UserLevel getOrCreateUserLevel(Long userId) {
        LambdaQueryWrapper<UserLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLevel::getUserId, userId);

        UserLevel userLevel = userLevelMapper.selectOne(wrapper);

        if (userLevel == null) {
            userLevel = new UserLevel();
            userLevel.setUserId(userId);
            userLevel.setLevel(1);
            userLevel.setExperience(0);
            userLevel.setTotalExperience(0);
            userLevel.setCreatedAt(LocalDateTime.now());
            userLevel.setUpdatedAt(LocalDateTime.now());
            userLevelMapper.insert(userLevel);
        }

        return userLevel;
    }

    /**
     * 获取或创建打卡记录
     */
    private UserLearningStreak getOrCreateStreak(Long userId) {
        LambdaQueryWrapper<UserLearningStreak> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLearningStreak::getUserId, userId);

        UserLearningStreak streak = userLearningStreakMapper.selectOne(wrapper);

        if (streak == null) {
            streak = new UserLearningStreak();
            streak.setUserId(userId);
            streak.setStreakDays(0);
            streak.setMaxStreakDays(0);
            streak.setTotalCheckinDays(0);
            streak.setCreatedAt(LocalDateTime.now());
            streak.setUpdatedAt(LocalDateTime.now());
            userLearningStreakMapper.insert(streak);
        }

        return streak;
    }
}
