package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itts.modules.learning.dto.UserStatsResponse;
import com.itts.modules.learning.entity.StudyCheckin;
import com.itts.modules.learning.entity.UserLearningStats;
import com.itts.modules.learning.mapper.StudyCheckinMapper;
import com.itts.modules.learning.mapper.UserLearningStatsMapper;
import com.itts.modules.learning.service.UserLearningStatsService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户学习统计服务实现
 */
@Service
public class UserLearningStatsServiceImpl extends ServiceImpl<UserLearningStatsMapper, UserLearningStats>
        implements UserLearningStatsService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private StudyCheckinMapper checkinMapper;

    @Lazy
    @Autowired
    private com.itts.modules.learning.service.AchievementService achievementService;

    @Override
    public UserStatsResponse getUserStats(Long userId) {
        UserLearningStats stats = getOrCreateStats(userId);
        SysUser user = userMapper.selectById(userId);

        UserStatsResponse response = new UserStatsResponse();
        response.setUserId(userId);
        response.setUsername(user != null ? user.getUsername() : "未知用户");

        // 总体统计
        response.setTotalStudyMinutes(stats.getTotalStudyMinutes());
        response.setTotalStudyFormatted(formatStudyTime(stats.getTotalStudyMinutes()));
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

        // 获取成就数量
        List<com.itts.modules.learning.dto.AchievementResponse> achievements =
            achievementService.getUserAchievements(userId);
        response.setAchievementsEarned(achievements.size());

        // 时间分布统计
        response.setWeeklyStudyMinutes(calculateWeeklyStudyMinutes(userId));
        response.setMonthlyStudyMinutes(calculateMonthlyStudyMinutes(userId));

        // 最近30天学习趋势
        response.setLast30DaysTrend(getLast30DaysTrend(userId));

        return response;
    }

    @Override
    @Transactional
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
    @Transactional
    public void addStudyTime(Long userId, int studyMinutes) {
        UserLearningStats stats = getOrCreateStats(userId);
        stats.setTotalStudyMinutes(stats.getTotalStudyMinutes() + studyMinutes);
        stats.setLastStudyDate(LocalDate.now());
        updateById(stats);
    }

    @Override
    @Transactional
    public void updateStreakDays(Long userId) {
        UserLearningStats stats = getOrCreateStats(userId);
        LocalDate today = LocalDate.now();
        LocalDate lastStudyDate = stats.getLastStudyDate();

        if (lastStudyDate == null) {
            // 首次学习
            stats.setCurrentStreakDays(1);
            stats.setMaxStreakDays(1);
            stats.setTotalCheckinDays(1);
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastStudyDate, today);
            
            if (daysBetween == 0) {
                // 今天已经打过卡，不更新
                return;
            } else if (daysBetween == 1) {
                // 连续打卡
                stats.setCurrentStreakDays(stats.getCurrentStreakDays() + 1);
                stats.setTotalCheckinDays(stats.getTotalCheckinDays() + 1);
                if (stats.getCurrentStreakDays() > stats.getMaxStreakDays()) {
                    stats.setMaxStreakDays(stats.getCurrentStreakDays());
                }
            } else {
                // 断签，重新开始
                stats.setCurrentStreakDays(1);
                stats.setTotalCheckinDays(stats.getTotalCheckinDays() + 1);
            }
        }

        stats.setLastStudyDate(today);
        updateById(stats);
    }

    @Override
    @Transactional
    public void incrementCompletedCourses(Long userId) {
        UserLearningStats stats = getOrCreateStats(userId);
        stats.setTotalCoursesCompleted(stats.getTotalCoursesCompleted() + 1);
        updateById(stats);
    }

    @Override
    @Transactional
    public void incrementEnrolledCourses(Long userId) {
        UserLearningStats stats = getOrCreateStats(userId);
        stats.setTotalCoursesEnrolled(stats.getTotalCoursesEnrolled() + 1);
        updateById(stats);
    }

    @Override
    @Transactional
    public void addAchievementPoints(Long userId, int points) {
        UserLearningStats stats = getOrCreateStats(userId);
        stats.setTotalAchievementPoints(stats.getTotalAchievementPoints() + points);
        updateById(stats);
    }

    @Override
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
     * 格式化学习时长
     */
    private String formatStudyTime(int minutes) {
        if (minutes < 60) {
            return minutes + "分钟";
        }
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        if (remainingMinutes == 0) {
            return hours + "小时";
        }
        return hours + "小时" + remainingMinutes + "分钟";
    }

    /**
     * 计算本周学习时长
     */
    private Integer calculateWeeklyStudyMinutes(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        
        List<StudyCheckin> checkins = checkinMapper.selectList(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .ge(StudyCheckin::getCheckinDate, weekStart)
                .le(StudyCheckin::getCheckinDate, today)
        );
        
        return checkins.stream()
            .mapToInt(StudyCheckin::getStudyMinutes)
            .sum();
    }

    /**
     * 计算本月学习时长
     */
    private Integer calculateMonthlyStudyMinutes(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        
        List<StudyCheckin> checkins = checkinMapper.selectList(
            new LambdaQueryWrapper<StudyCheckin>()
                .eq(StudyCheckin::getUserId, userId)
                .ge(StudyCheckin::getCheckinDate, monthStart)
                .le(StudyCheckin::getCheckinDate, today)
        );
        
        return checkins.stream()
            .mapToInt(StudyCheckin::getStudyMinutes)
            .sum();
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