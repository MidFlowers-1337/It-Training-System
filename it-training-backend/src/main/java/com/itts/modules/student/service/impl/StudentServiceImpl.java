package com.itts.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.exception.BusinessException;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.entity.StudyCheckin;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.learning.mapper.StudyCheckinMapper;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.modules.student.dto.StudentDashboardResponse;
import com.itts.modules.student.dto.StudentStatsResponse;
import com.itts.modules.student.entity.UserChapterProgress;
import com.itts.modules.student.entity.UserLearningStreak;
import com.itts.modules.student.entity.UserLevel;
import com.itts.modules.student.mapper.UserChapterProgressMapper;
import com.itts.modules.student.mapper.UserLearningStreakMapper;
import com.itts.modules.student.mapper.UserLevelMapper;
import com.itts.modules.student.service.StudentService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学生服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final SysUserMapper sysUserMapper;
    private final UserLevelMapper userLevelMapper;
    private final UserLearningStreakMapper userLearningStreakMapper;
    private final UserChapterProgressMapper userChapterProgressMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final ClassSessionMapper classSessionMapper;
    private final CourseMapper courseMapper;
    private final com.itts.modules.course.mapper.CourseChapterMapper courseChapterMapper;
    private final LearningProgressMapper learningProgressMapper;
    private final StudyCheckinMapper studyCheckinMapper;
    private final com.itts.modules.learning.service.AchievementService achievementService;
    private final com.itts.modules.learning.service.UserLearningStatsService userLearningStatsService;

    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");

    // 等级经验值配置
    private static final int[] LEVEL_EXP_REQUIREMENTS = {
        0, 100, 300, 600, 1000, 1500, 2100, 2800, 3600, 4500, 5500,
        6600, 7800, 9100, 10500, 12000, 13600, 15300, 17100, 19000, 21000
    };

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

    @Override
    public StudentStatsResponse getStats(Long userId) {
        log.info("获取学生学习统计, userId: {}", userId);

        StudentStatsResponse stats = new StudentStatsResponse();

        // 1. 获取学习时长统计
        com.itts.modules.learning.entity.UserLearningStats learningStats =
            userLearningStatsService.getOrCreateStats(userId);
        stats.setTotalStudyMinutes(learningStats.getTotalStudyMinutes());

        // 2. 获取打卡统计
        UserLearningStreak streak = getOrCreateStreak(userId);
        stats.setStreakDays(streak.getStreakDays());
        stats.setMaxStreakDays(streak.getMaxStreakDays());
        stats.setTotalCheckinDays(streak.getTotalCheckinDays());

        // 3. 获取课程统计（使用JOIN查询避免N+1问题）
        List<Enrollment> enrollmentsWithDetails = enrollmentMapper.selectUserEnrollmentsWithDetails(userId);

        int completedCourses = 0;
        int inProgressCourses = 0;

        // 收集所有课程ID一次性查询进度
        Set<Long> courseIds = enrollmentsWithDetails.stream()
                .filter(e -> e.getCourseId() != null && e.getStatus() != null && e.getStatus() == 0)
                .map(Enrollment::getCourseId)
                .collect(Collectors.toSet());

        if (!courseIds.isEmpty()) {
            // 一次查询所有学习进度
            List<LearningProgress> progressList = learningProgressMapper.selectList(
                    new LambdaQueryWrapper<LearningProgress>()
                            .eq(LearningProgress::getUserId, userId)
                            .in(LearningProgress::getCourseId, courseIds)
            );

            // 转换为Map便于查找
            Map<Long, LearningProgress> progressMap = progressList.stream()
                    .collect(Collectors.toMap(LearningProgress::getCourseId, p -> p, (a, b) -> a));

            for (Long courseId : courseIds) {
                LearningProgress progress = progressMap.get(courseId);
                if (progress != null) {
                    if (progress.getProgressPercent() >= 100) {
                        completedCourses++;
                    } else if (progress.getProgressPercent() > 0) {
                        inProgressCourses++;
                    }
                }
            }
        }

        stats.setCompletedCourses(completedCourses);
        stats.setInProgressCourses(inProgressCourses);

        // 4. 获取成就统计
        List<com.itts.modules.learning.dto.AchievementResponse> achievements =
            achievementService.getUserAchievements(userId);
        stats.setAchievementsEarned(achievements.size());

        // 5. 获取等级信息
        UserLevel userLevel = getOrCreateUserLevel(userId);
        stats.setLevel(userLevel.getLevel());
        stats.setExperience(userLevel.getExperience());
        stats.setNextLevelExp(getNextLevelExp(userLevel.getLevel()));

        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addExperience(Long userId, Integer exp) {
        log.info("添加经验值, userId: {}, exp: {}", userId, exp);

        if (exp <= 0) {
            return;
        }

        // 获取或创建用户等级记录
        UserLevel userLevel = getOrCreateUserLevel(userId);

        int currentExp = userLevel.getExperience();
        int currentLevel = userLevel.getLevel();
        int newExp = currentExp + exp;

        // 计算新等级
        int newLevel = calculateLevel(newExp);

        // 更新等级和经验
        userLevel.setExperience(newExp);
        userLevel.setLevel(newLevel);
        userLevel.setTotalExperience(userLevel.getTotalExperience() + exp);

        userLevelMapper.updateById(userLevel);

        // 如果升级了，记录日志
        if (newLevel > currentLevel) {
            log.info("用户升级, userId: {}, oldLevel: {}, newLevel: {}", userId, currentLevel, newLevel);
        }
    }

    @Override
    public void checkAndUnlockAchievements(Long userId) {
        log.info("检查并解锁成就, userId: {}", userId);

        // 调用 AchievementService 统一处理成就检查和授予
        achievementService.checkAndGrantAchievements(userId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取用户信息
     */
    private StudentDashboardResponse.UserInfo getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserLevel userLevel = getOrCreateUserLevel(userId);

        StudentDashboardResponse.UserInfo userInfo = new StudentDashboardResponse.UserInfo();
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setLevel(userLevel.getLevel());
        userInfo.setExperience(userLevel.getExperience());
        userInfo.setNextLevelExp(getNextLevelExp(userLevel.getLevel()));

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
        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getUserId, userId)
                .lt(LearningProgress::getProgressPercent, 100)
                .orderByDesc(LearningProgress::getUpdatedAt)
                .last("LIMIT 1");

        LearningProgress progress = learningProgressMapper.selectOne(wrapper);

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
        LambdaQueryWrapper<UserChapterProgress> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(UserChapterProgress::getUserId, userId)
                .eq(UserChapterProgress::getCourseId, course.getId())
                .eq(UserChapterProgress::getCompleted, false)
                .orderByDesc(UserChapterProgress::getUpdatedAt)
                .last("LIMIT 1");

        UserChapterProgress chapterProgress = userChapterProgressMapper.selectOne(chapterWrapper);

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
     * 获取本周学习统计
     */
    private StudentDashboardResponse.WeeklyStats getWeeklyStats(Long userId) {
        StudentDashboardResponse.WeeklyStats stats = new StudentDashboardResponse.WeeklyStats();

        List<Integer> dailyMinutes = new ArrayList<>();
        int totalMinutes = 0;

        // 获取最近7天的学习数据
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LearningProgress::getUserId, userId)
                    .ge(LearningProgress::getUpdatedAt, dayStart)
                    .lt(LearningProgress::getUpdatedAt, dayEnd);

            List<LearningProgress> dayProgress = learningProgressMapper.selectList(wrapper);
            int dayMinutes = dayProgress.stream()
                    .mapToInt(p -> p.getStudyDurationMinutes() != null ? p.getStudyDurationMinutes() : 0)
                    .sum();

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
        // 使用JOIN查询直接获取报名和课程信息
        List<Enrollment> enrollmentsWithDetails = enrollmentMapper.selectUserEnrollmentsWithDetails(userId);

        // 过滤已报名状态并限制数量
        List<Enrollment> filteredEnrollments = enrollmentsWithDetails.stream()
                .filter(e -> e.getStatus() != null && e.getStatus() == 0 && e.getCourseId() != null)
                .limit(limit)
                .collect(Collectors.toList());

        if (filteredEnrollments.isEmpty()) {
            return new ArrayList<>();
        }

        // 收集所有课程ID一次性查询进度
        Set<Long> courseIds = filteredEnrollments.stream()
                .map(Enrollment::getCourseId)
                .collect(Collectors.toSet());

        // 一次查询所有学习进度
        List<LearningProgress> progressList = learningProgressMapper.selectList(
                new LambdaQueryWrapper<LearningProgress>()
                        .eq(LearningProgress::getUserId, userId)
                        .in(LearningProgress::getCourseId, courseIds)
        );

        // 转换为Map便于查找
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
        // 调用 AchievementService 获取真实数据
        List<com.itts.modules.learning.dto.AchievementResponse> achievements =
            achievementService.getRecentAchievements(userId, limit);

        return achievements.stream().map(achievement -> {
            StudentDashboardResponse.AchievementItem item = new StudentDashboardResponse.AchievementItem();
            item.setAchievementId(achievement.getId());
            item.setName(achievement.getName());
            item.setIcon(achievement.getIcon() != null ? achievement.getIcon() : "🏆");
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

    /**
     * 计算等级
     */
    private int calculateLevel(int exp) {
        for (int level = LEVEL_EXP_REQUIREMENTS.length - 1; level >= 0; level--) {
            if (exp >= LEVEL_EXP_REQUIREMENTS[level]) {
                return level + 1;
            }
        }
        return 1;
    }

    /**
     * 获取下一级所需经验
     */
    private int getNextLevelExp(int currentLevel) {
        if (currentLevel >= LEVEL_EXP_REQUIREMENTS.length) {
            return LEVEL_EXP_REQUIREMENTS[LEVEL_EXP_REQUIREMENTS.length - 1];
        }
        return LEVEL_EXP_REQUIREMENTS[currentLevel];
    }
}
