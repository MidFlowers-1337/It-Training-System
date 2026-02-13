package com.itts.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.util.LevelDifficultyUtils;
import com.itts.enums.EnrollmentStatus;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.student.dto.StudentStatsResponse;
import com.itts.modules.student.entity.UserLearningStreak;
import com.itts.modules.student.entity.UserLevel;
import com.itts.modules.student.mapper.UserLearningStreakMapper;
import com.itts.modules.student.mapper.UserLevelMapper;
import com.itts.modules.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学生服务实现
 * <p>
 * [Phase 6 #7] 拆分后仅保留核心学生业务逻辑（统计、经验值、成就检查），
 * Dashboard 数据聚合已移至 StudentDashboardServiceImpl。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserLevelMapper userLevelMapper;
    private final UserLearningStreakMapper userLearningStreakMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final LearningProgressMapper learningProgressMapper;
    private final com.itts.modules.achievement.service.AchievementService achievementService;
    private final com.itts.modules.learning.service.UserLearningStatsService userLearningStatsService;

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

        Set<Long> courseIds = enrollmentsWithDetails.stream()
                .filter(e -> e.getCourseId() != null && e.getStatus() != null
                        && e.getStatus().equals(EnrollmentStatus.ENROLLED.getCode()))
                .map(Enrollment::getCourseId)
                .collect(Collectors.toSet());

        if (!courseIds.isEmpty()) {
            List<LearningProgress> progressList = learningProgressMapper.selectList(
                    new LambdaQueryWrapper<LearningProgress>()
                            .eq(LearningProgress::getUserId, userId)
                            .in(LearningProgress::getCourseId, courseIds)
            );

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
        List<com.itts.modules.achievement.dto.AchievementResponse> achievements =
            achievementService.getUserAchievements(userId);
        stats.setAchievementsEarned(achievements.size());

        // 5. 获取等级信息
        int totalMinutes = learningStats.getTotalStudyMinutes();
        int level = LevelDifficultyUtils.calculateLevelByMinutes(totalMinutes);
        stats.setLevel(level);
        stats.setLevelName(LevelDifficultyUtils.getLevelName(level));
        stats.setNextLevelMinutes(LevelDifficultyUtils.getNextLevelMinutes(level));

        UserLevel userLevel = getOrCreateUserLevel(userId);
        stats.setExperience(userLevel.getExperience());
        stats.setNextLevelExp(LevelDifficultyUtils.getNextLevelMinutes(level));

        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addExperience(Long userId, Integer exp) {
        log.info("添加经验值, userId: {}, exp: {}", userId, exp);

        if (exp <= 0) {
            return;
        }

        UserLevel userLevel = getOrCreateUserLevel(userId);

        int newExp = userLevel.getExperience() + exp;

        com.itts.modules.learning.entity.UserLearningStats learningStats =
            userLearningStatsService.getOrCreateStats(userId);
        int newLevel = LevelDifficultyUtils.calculateLevelByMinutes(learningStats.getTotalStudyMinutes());
        int oldLevel = userLevel.getLevel();

        userLevel.setExperience(newExp);
        userLevel.setLevel(newLevel);
        userLevel.setTotalExperience(userLevel.getTotalExperience() + exp);

        userLevelMapper.updateById(userLevel);

        if (newLevel > oldLevel) {
            log.info("用户升级, userId: {}, oldLevel: {}, newLevel: {}, levelName: {}",
                userId, oldLevel, newLevel, LevelDifficultyUtils.getLevelName(newLevel));
        }
    }

    @Override
    public void checkAndUnlockAchievements(Long userId) {
        log.info("检查并解锁成就, userId: {}", userId);
        achievementService.checkAndGrantAchievements(userId);
    }

    // ==================== 私有辅助方法 ====================

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
