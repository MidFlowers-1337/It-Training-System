package com.itts.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.exception.BusinessException;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.modules.student.dto.CheckinResponse;
import com.itts.modules.student.dto.StudentDashboardResponse;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    private final LearningProgressMapper learningProgressMapper;

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
    @Transactional(rollbackFor = Exception.class)
    public CheckinResponse checkin(Long userId) {
        log.info("用户打卡, userId: {}", userId);

        CheckinResponse response = new CheckinResponse();

        // 获取或创建打卡记录
        UserLearningStreak streak = getOrCreateStreak(userId);

        LocalDate today = LocalDate.now();
        LocalDate lastCheckin = streak.getLastCheckinDate();

        // 检查是否已打卡
        if (lastCheckin != null && lastCheckin.equals(today)) {
            response.setSuccess(false);
            response.setStreakDays(streak.getStreakDays());
            response.setMaxStreakDays(streak.getMaxStreakDays());
            response.setTotalDays(streak.getTotalCheckinDays());
            response.setNewAchievement(false);
            throw new BusinessException("今日已打卡，请勿重复打卡");
        }

        // 计算连续天数
        int newStreakDays;
        if (lastCheckin == null) {
            // 首次打卡
            newStreakDays = 1;
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastCheckin, today);
            if (daysBetween == 1) {
                // 连续打卡
                newStreakDays = streak.getStreakDays() + 1;
            } else {
                // 中断了，重新开始
                newStreakDays = 1;
            }
        }

        // 更新打卡记录
        streak.setStreakDays(newStreakDays);
        streak.setLastCheckinDate(today);
        streak.setTotalCheckinDays(streak.getTotalCheckinDays() + 1);

        // 更新最大连续天数
        if (newStreakDays > streak.getMaxStreakDays()) {
            streak.setMaxStreakDays(newStreakDays);
        }

        userLearningStreakMapper.updateById(streak);

        // 奖励经验值
        int rewardExp = 20; // 每次打卡奖励20经验
        addExperience(userId, rewardExp);

        // 检查成就解锁
        boolean newAchievement = checkStreakAchievements(userId, newStreakDays);

        // 构建响应
        response.setSuccess(true);
        response.setStreakDays(newStreakDays);
        response.setMaxStreakDays(streak.getMaxStreakDays());
        response.setTotalDays(streak.getTotalCheckinDays());
        response.setRewardExp(rewardExp);
        response.setNewAchievement(newAchievement);

        if (newAchievement) {
            response.setAchievementName(getStreakAchievementName(newStreakDays));
        }

        log.info("打卡成功, userId: {}, streakDays: {}", userId, newStreakDays);

        return response;
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

        // 获取用户数据
        UserLearningStreak streak = getOrCreateStreak(userId);
        UserLevel userLevel = getOrCreateUserLevel(userId);

        // 检查连续学习成就
        checkStreakAchievements(userId, streak.getStreakDays());

        // 检查学习时长成就
        int totalHours = userLevel.getTotalExperience() / 60; // 假设1分钟=1经验
        checkDurationAchievements(userId, totalHours);

        // 检查课程完成成就
        int completedCourses = getCompletedCoursesCount(userId);
        checkCourseAchievements(userId, completedCourses);
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

        // 获取今日学习时长（从learning_progress表）
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getUserId, userId)
                .ge(LearningProgress::getUpdatedAt, todayStart);

        List<LearningProgress> todayProgress = learningProgressMapper.selectList(wrapper);
        int todayMinutes = todayProgress.stream()
                .mapToInt(p -> p.getStudyDurationMinutes() != null ? p.getStudyDurationMinutes() : 0)
                .sum();

        stats.setStudyMinutes(todayMinutes);

        // 获取连续天数
        UserLearningStreak streak = getOrCreateStreak(userId);
        stats.setStreakDays(streak.getStreakDays());

        // 检查今日是否已打卡
        LocalDate today = LocalDate.now();
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
        continueLearning.setCurrentChapter("继续学习"); // TODO: 从章节进度获取
        continueLearning.setCurrentChapterId(null);

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
     * 获取我的课程列表
     */
    private List<StudentDashboardResponse.MyCourseItem> getMyCourses(Long userId, int limit) {
        // 查询用户报名的课程
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.eq(Enrollment::getUserId, userId)
                .eq(Enrollment::getStatus, 0) // 0-已报名
                .orderByDesc(Enrollment::getCreatedAt)
                .last("LIMIT " + limit);

        List<Enrollment> enrollments = enrollmentMapper.selectList(enrollmentWrapper);

        return enrollments.stream().map(enrollment -> {
            // 通过 sessionId 获取班期信息
            ClassSession session = classSessionMapper.selectById(enrollment.getSessionId());
            if (session == null) {
                return null;
            }

            // 通过班期获取课程信息
            Course course = courseMapper.selectById(session.getCourseId());
            if (course == null) {
                return null;
            }

            // 获取学习进度
            LambdaQueryWrapper<LearningProgress> progressWrapper = new LambdaQueryWrapper<>();
            progressWrapper.eq(LearningProgress::getUserId, userId)
                    .eq(LearningProgress::getCourseId, course.getId());

            LearningProgress progress = learningProgressMapper.selectOne(progressWrapper);

            StudentDashboardResponse.MyCourseItem item = new StudentDashboardResponse.MyCourseItem();
            item.setCourseId(course.getId());
            item.setCourseName(course.getName());
            item.setCoverImage(course.getCoverImage());

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
        }).filter(item -> item != null).collect(Collectors.toList());
    }

    /**
     * 获取最近解锁的成就
     */
    private List<StudentDashboardResponse.AchievementItem> getRecentAchievements(Long userId, int limit) {
        // TODO: 从user_achievement表查询
        // 这里返回模拟数据
        List<StudentDashboardResponse.AchievementItem> achievements = new ArrayList<>();

        StudentDashboardResponse.AchievementItem item1 = new StudentDashboardResponse.AchievementItem();
        item1.setAchievementId(1L);
        item1.setName("连续7天学习");
        item1.setIcon("🔥🔥");
        item1.setUnlockedAt("2小时前");
        achievements.add(item1);

        return achievements;
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

    /**
     * 检查连续学习成就
     */
    private boolean checkStreakAchievements(Long userId, int streakDays) {
        // 成就里程碑：3天、7天、30天、100天
        int[] milestones = {3, 7, 30, 100};

        for (int milestone : milestones) {
            if (streakDays == milestone) {
                log.info("解锁连续学习成就, userId: {}, days: {}", userId, milestone);
                // TODO: 记录到user_achievement表
                return true;
            }
        }

        return false;
    }

    /**
     * 检查学习时长成就
     */
    private void checkDurationAchievements(Long userId, int totalHours) {
        // 成就里程碑：10h、50h、100h、500h
        int[] milestones = {10, 50, 100, 500};

        for (int milestone : milestones) {
            if (totalHours >= milestone) {
                log.info("解锁学习时长成就, userId: {}, hours: {}", userId, milestone);
                // TODO: 记录到user_achievement表
            }
        }
    }

    /**
     * 检查课程完成成就
     */
    private void checkCourseAchievements(Long userId, int completedCount) {
        // 成就里程碑：1门、5门、10门、50门
        int[] milestones = {1, 5, 10, 50};

        for (int milestone : milestones) {
            if (completedCount >= milestone) {
                log.info("解锁课程完成成就, userId: {}, count: {}", userId, milestone);
                // TODO: 记录到user_achievement表
            }
        }
    }

    /**
     * 获取已完成课程数量
     */
    private int getCompletedCoursesCount(Long userId) {
        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getUserId, userId)
                .ge(LearningProgress::getProgressPercent, 100);

        return learningProgressMapper.selectCount(wrapper).intValue();
    }

    /**
     * 获取连续学习成就名称
     */
    private String getStreakAchievementName(int days) {
        if (days >= 100) return "学习狂魔";
        if (days >= 30) return "学习达人";
        if (days >= 7) return "坚持不懈";
        if (days >= 3) return "初露锋芒";
        return "学习新手";
    }
}
