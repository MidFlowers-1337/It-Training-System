package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itts.common.util.TimeFormatUtils;
import com.itts.modules.learning.dto.UserStatsResponse;
import com.itts.modules.checkin.entity.StudyCheckin;
import com.itts.modules.achievement.entity.UserAchievement;
import com.itts.modules.learning.entity.UserLearningStats;
import com.itts.modules.achievement.event.LearningActivityEvent;
import com.itts.modules.checkin.mapper.StudyCheckinMapper;
import com.itts.modules.achievement.mapper.UserAchievementMapper;
import com.itts.modules.learning.mapper.UserLearningStatsMapper;
import com.itts.modules.learning.service.UserLearningStatsService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户学习统计服务实现
 * <p>
 * [Phase 5 #30] 移除 @Lazy AchievementService 循环依赖，
 * 改用 ApplicationEventPublisher 发布 LearningActivityEvent 触发成就检查，
 * 成就数量统计改为直接查询 UserAchievementMapper（避免循环引用）。
 */
@Slf4j
@Service
public class UserLearningStatsServiceImpl extends ServiceImpl<UserLearningStatsMapper, UserLearningStats>
        implements UserLearningStatsService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private StudyCheckinMapper checkinMapper;

    @Autowired
    private UserAchievementMapper userAchievementMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public UserStatsResponse getUserStats(Long userId) {
        UserLearningStats stats = getOrCreateStats(userId);
        SysUser user = userMapper.selectById(userId);

        UserStatsResponse response = new UserStatsResponse();
        response.setUserId(userId);
        response.setUsername(user != null ? user.getUsername() : "未知用户");

        // 总体统计
        response.setTotalStudyMinutes(stats.getTotalStudyMinutes());
        response.setTotalStudyFormatted(TimeFormatUtils.formatStudyTime(stats.getTotalStudyMinutes()));
        response.setTotalCoursesEnrolled(stats.getTotalCoursesEnrolled());
        response.setTotalCoursesCompleted(stats.getTotalCoursesCompleted());

        // 计算完成率
        if (stats.getTotalCoursesEnrolled() > 0) {
            response.setCompletionRate(
                (double) stats.getTotalCoursesCompleted() / stats.getTotalCoursesEnrolled() * 100
            );
        } else {
            response.setCompletionRate(0.0);
        }

        // 连续学习统计
        response.setCurrentStreakDays(stats.getCurrentStreakDays());
        response.setMaxStreakDays(stats.getMaxStreakDays());
        response.setTotalCheckinDays(stats.getTotalCheckinDays());
        response.setLastStudyDate(stats.getLastStudyDate());

        // 成就统计
        response.setTotalAchievementPoints(stats.getTotalAchievementPoints());

        // [Phase 5 #30] 直接查询 Mapper 获取成就数量，避免循环依赖 AchievementService
        long achievementsEarned = userAchievementMapper.selectCount(
            new LambdaQueryWrapper<UserAchievement>().eq(UserAchievement::getUserId, userId)
        );
        response.setAchievementsEarned((int) achievementsEarned);

        // 时间分布统计
        // [Phase 4 #P11] 合并查询：一次查询过去30天打卡记录，在内存中计算周/月统计
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate thirtyDaysAgo = today.minusDays(29);

        // 选取最早的日期作为查询起点（monthStart 或 thirtyDaysAgo）
        LocalDate queryStart = monthStart.isBefore(thirtyDaysAgo) ? monthStart : thirtyDaysAgo;
        if (weekStart.isBefore(queryStart)) {
            queryStart = weekStart;
        }

        List<StudyCheckin> recentCheckins = checkinMapper.selectList(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .ge(StudyCheckin::getCheckinDate, queryStart)
                .le(StudyCheckin::getCheckinDate, today)
        );

        // 从同一批数据计算周学习时长
        final LocalDate finalWeekStart = weekStart;
        int weeklyMinutes = recentCheckins.stream()
            .filter(c -> !c.getCheckinDate().isBefore(finalWeekStart) && !c.getCheckinDate().isAfter(today))
            .mapToInt(StudyCheckin::getStudyMinutes)
            .sum();
        response.setWeeklyStudyMinutes(weeklyMinutes);

        // 从同一批数据计算月学习时长
        final LocalDate finalMonthStart = monthStart;
        int monthlyMinutes = recentCheckins.stream()
            .filter(c -> !c.getCheckinDate().isBefore(finalMonthStart) && !c.getCheckinDate().isAfter(today))
            .mapToInt(StudyCheckin::getStudyMinutes)
            .sum();
        response.setMonthlyStudyMinutes(monthlyMinutes);

        // 最近30天学习趋势
        response.setLast30DaysTrend(getLast30DaysTrend(userId));

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLearningStats initUserStats(Long userId) {
        // 检查是否已存在
        UserLearningStats existing = getOne(
            new LambdaQueryWrapper<UserLearningStats>().eq(UserLearningStats::getUserId, userId)
        );
        if (existing != null) {
            return existing;
        }

        UserLearningStats stats = new UserLearningStats();
        stats.setUserId(userId);
        stats.setTotalStudyMinutes(0);
        stats.setTotalCoursesEnrolled(0);
        stats.setTotalCoursesCompleted(0);
        stats.setCurrentStreakDays(0);
        stats.setMaxStreakDays(0);
        stats.setTotalCheckinDays(0);
        stats.setTotalAchievementPoints(0);
        save(stats);
        return stats;
    }

    @Override
    @CacheEvict(value = "stats:user", key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public void addStudyTime(Long userId, int studyMinutes) {
        // 确保统计记录存在
        getOrCreateStats(userId);
        // 使用原子 SQL 增加学习时长，避免并发读-改-写竞态
        baseMapper.atomicAddStudyMinutes(userId, studyMinutes, LocalDate.now());

        // [Phase 5 #30] 发布学习活动事件，由 AchievementService 监听并检查成就
        eventPublisher.publishEvent(new LearningActivityEvent(this, userId, "study_time"));
    }

    @Override
    @CacheEvict(value = "stats:user", key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public void updateStreakDays(Long userId) {
        UserLearningStats stats = getOrCreateStats(userId);
        LocalDate today = LocalDate.now();
        LocalDate lastStudyDate = stats.getLastStudyDate();

        if (lastStudyDate == null) {
            // 首次学习：原子初始化打卡
            baseMapper.atomicInitStreak(userId, today);
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastStudyDate, today);

            if (daysBetween == 0) {
                // 今天已经打过卡，不更新
                return;
            } else if (daysBetween == 1) {
                // 连续打卡：原子递增
                baseMapper.atomicIncrementStreak(userId, today);
            } else {
                // 断签：原子重置为1
                baseMapper.atomicResetStreak(userId, today);
            }
        }
    }

    @Override
    @CacheEvict(value = "stats:user", key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public void incrementCompletedCourses(Long userId) {
        // 确保统计记录存在
        getOrCreateStats(userId);
        // 原子递增已完成课程数
        baseMapper.atomicIncrementCompletedCourses(userId);

        // [Phase 5 #30] 完成课程也可能触发成就
        eventPublisher.publishEvent(new LearningActivityEvent(this, userId, "progress_update"));
    }

    @Override
    @CacheEvict(value = "stats:user", key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public void incrementEnrolledCourses(Long userId) {
        // 确保统计记录存在
        getOrCreateStats(userId);
        // 原子递增已报名课程数
        baseMapper.atomicIncrementEnrolledCourses(userId);
    }

    @Override
    @CacheEvict(value = "stats:user", key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public void addAchievementPoints(Long userId, int points) {
        // 确保统计记录存在
        getOrCreateStats(userId);
        // 原子增加成就积分
        baseMapper.atomicAddAchievementPoints(userId, points);
    }

    @Override
    @Cacheable(value = "stats:user", key = "#userId")
    public UserLearningStats getOrCreateStats(Long userId) {
        UserLearningStats stats = getOne(
            new LambdaQueryWrapper<UserLearningStats>().eq(UserLearningStats::getUserId, userId)
        );
        if (stats == null) {
            stats = initUserStats(userId);
        }
        return stats;
    }

    /**
     * 获取最近30天学习趋势
     */
    private List<UserStatsResponse.DailyStudyTrend> getLast30DaysTrend(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(29);

        List<StudyCheckin> checkins = checkinMapper.selectList(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .ge(StudyCheckin::getCheckinDate, startDate)
                .le(StudyCheckin::getCheckinDate, today)
        );

        List<UserStatsResponse.DailyStudyTrend> trends = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LocalDate date = startDate.plusDays(i);
            UserStatsResponse.DailyStudyTrend trend = new UserStatsResponse.DailyStudyTrend();
            trend.setDate(date);

            // 查找该日期的打卡记录
            final LocalDate checkDate = date;
            StudyCheckin checkin = checkins.stream()
                .filter(c -> c.getCheckinDate().equals(checkDate))
                .findFirst()
                .orElse(null);

            if (checkin != null) {
                trend.setStudyMinutes(checkin.getStudyMinutes());
                trend.setCheckedIn(true);
            } else {
                trend.setStudyMinutes(0);
                trend.setCheckedIn(false);
            }

            trends.add(trend);
        }

        return trends;
    }
}
