package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itts.modules.learning.dto.AchievementResponse;
import com.itts.modules.learning.entity.Achievement;
import com.itts.modules.learning.entity.UserAchievement;
import com.itts.modules.learning.entity.UserLearningStats;
import com.itts.modules.learning.mapper.AchievementMapper;
import com.itts.modules.learning.mapper.UserAchievementMapper;
import com.itts.modules.learning.service.AchievementService;
import com.itts.modules.learning.service.UserLearningStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成就服务实现
 */
@Service
@RequiredArgsConstructor
public class AchievementServiceImpl extends ServiceImpl<AchievementMapper, Achievement>
        implements AchievementService {

    private final UserAchievementMapper userAchievementMapper;
    private final UserLearningStatsService userLearningStatsService;

    @Override
    public List<AchievementResponse> getAllAchievements(Long userId) {
        // 获取所有成就
        List<Achievement> achievements = list(
            new LambdaQueryWrapper<Achievement>()
                .eq(Achievement::getStatus, 1)
                .orderByAsc(Achievement::getCategory)
                .orderByAsc(Achievement::getConditionValue)
        );

        // 获取用户已获得的成就
        Map<Long, UserAchievement> userAchievementMap = getUserAchievementMap(userId);

        // 获取用户当前统计数据
        UserLearningStats stats = userLearningStatsService.getOrCreateStats(userId);

        return achievements.stream()
            .map(a -> convertToResponse(a, userAchievementMap.get(a.getId()), stats))
            .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getUserAchievements(Long userId) {
        // 获取用户已获得的成就ID
        List<UserAchievement> userAchievements = userAchievementMapper.selectList(
            new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId)
        );

        if (userAchievements.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> achievementIds = userAchievements.stream()
            .map(UserAchievement::getAchievementId)
            .collect(Collectors.toList());

        // 获取成就详情
        List<Achievement> achievements = listByIds(achievementIds);
        
        Map<Long, UserAchievement> userAchievementMap = userAchievements.stream()
            .collect(Collectors.toMap(UserAchievement::getAchievementId, ua -> ua));

        UserLearningStats stats = userLearningStatsService.getOrCreateStats(userId);

        return achievements.stream()
            .map(a -> convertToResponse(a, userAchievementMap.get(a.getId()), stats))
            .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getRecentAchievements(Long userId, int limit) {
        // 获取用户最近获得的成就
        List<UserAchievement> userAchievements = userAchievementMapper.selectList(
            new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId)
                .orderByDesc(UserAchievement::getEarnedAt)
                .last("LIMIT " + limit)
        );

        if (userAchievements.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> achievementIds = userAchievements.stream()
            .map(UserAchievement::getAchievementId)
            .collect(Collectors.toList());

        List<Achievement> achievements = listByIds(achievementIds);
        
        Map<Long, Achievement> achievementMap = achievements.stream()
            .collect(Collectors.toMap(Achievement::getId, a -> a));

        UserLearningStats stats = userLearningStatsService.getOrCreateStats(userId);

        return userAchievements.stream()
            .map(ua -> {
                Achievement a = achievementMap.get(ua.getAchievementId());
                return a != null ? convertToResponse(a, ua, stats) : null;
            })
            .filter(r -> r != null)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AchievementResponse> checkAndGrantAchievements(Long userId) {
        List<AchievementResponse> newAchievements = new ArrayList<>();
        
        // 获取用户统计数据
        UserLearningStats stats = userLearningStatsService.getOrCreateStats(userId);
        
        // 获取所有启用的成就
        List<Achievement> achievements = list(
            new LambdaQueryWrapper<Achievement>().eq(Achievement::getStatus, 1)
        );
        
        // 获取用户已获得的成就
        Map<Long, UserAchievement> userAchievementMap = getUserAchievementMap(userId);
        
        for (Achievement achievement : achievements) {
            // 跳过已获得的成就
            if (userAchievementMap.containsKey(achievement.getId())) {
                continue;
            }
            
            // 检查是否满足条件
            boolean earned = checkAchievementCondition(achievement, stats);
            
            if (earned) {
                // 授予成就
                UserAchievement userAchievement = new UserAchievement();
                userAchievement.setUserId(userId);
                userAchievement.setAchievementId(achievement.getId());
                userAchievement.setEarnedAt(LocalDateTime.now());
                userAchievementMapper.insert(userAchievement);
                
                // 增加成就积分
                userLearningStatsService.addAchievementPoints(userId, achievement.getPoints());
                
                // 添加到新获得列表
                AchievementResponse response = convertToResponse(achievement, userAchievement, stats);
                newAchievements.add(response);
            }
        }
        
        return newAchievements;
    }

    @Override
    public int getUserAchievementPoints(Long userId) {
        UserLearningStats stats = userLearningStatsService.getOrCreateStats(userId);
        return stats.getTotalAchievementPoints();
    }

    @Override
    public AchievementResponse getAchievementDetail(Long achievementId, Long userId) {
        Achievement achievement = getById(achievementId);
        if (achievement == null) {
            return null;
        }
        
        UserAchievement userAchievement = userAchievementMapper.selectOne(
            new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId)
                .eq(UserAchievement::getAchievementId, achievementId)
        );
        
        UserLearningStats stats = userLearningStatsService.getOrCreateStats(userId);
        
        return convertToResponse(achievement, userAchievement, stats);
    }

    /**
     * 获取用户成就映射
     */
    private Map<Long, UserAchievement> getUserAchievementMap(Long userId) {
        List<UserAchievement> userAchievements = userAchievementMapper.selectList(
            new LambdaQueryWrapper<UserAchievement>().eq(UserAchievement::getUserId, userId)
        );
        return userAchievements.stream()
            .collect(Collectors.toMap(UserAchievement::getAchievementId, ua -> ua));
    }

    /**
     * 检查成就条件是否满足
     */
    private boolean checkAchievementCondition(Achievement achievement, UserLearningStats stats) {
        String conditionType = achievement.getConditionType();
        Integer conditionValue = achievement.getConditionValue();
        
        switch (conditionType) {
            case "streak_days":
                return stats.getCurrentStreakDays() >= conditionValue 
                    || stats.getMaxStreakDays() >= conditionValue;
            case "courses_completed":
                return stats.getTotalCoursesCompleted() >= conditionValue;
            case "hours_studied":
                int hoursStudied = stats.getTotalStudyMinutes() / 60;
                return hoursStudied >= conditionValue;
            case "checkin_days":
                return stats.getTotalCheckinDays() >= conditionValue;
            default:
                return false;
        }
    }

    /**
     * 转换为响应DTO
     */
    private AchievementResponse convertToResponse(Achievement achievement, 
            UserAchievement userAchievement, UserLearningStats stats) {
        AchievementResponse response = new AchievementResponse();
        response.setId(achievement.getId());
        response.setName(achievement.getName());
        response.setDescription(achievement.getDescription());
        response.setIcon(achievement.getIconUrl());
        response.setPoints(achievement.getPoints());
        response.setCategory(achievement.getCategory());
        response.setConditionType(achievement.getConditionType());
        response.setConditionValue(achievement.getConditionValue());
        
        // 是否已获得
        response.setEarned(userAchievement != null);
        response.setEarnedAt(userAchievement != null ? userAchievement.getEarnedAt() : null);
        
        // 计算当前进度
        int currentProgress = calculateCurrentProgress(achievement, stats);
        response.setCurrentProgress(currentProgress);
        
        // 计算进度百分比
        int progressPercent = Math.min(100, 
            (int) ((double) currentProgress / achievement.getConditionValue() * 100));
        response.setProgressPercent(progressPercent);
        
        return response;
    }

    /**
     * 计算当前进度
     */
    private int calculateCurrentProgress(Achievement achievement, UserLearningStats stats) {
        String conditionType = achievement.getConditionType();
        
        switch (conditionType) {
            case "streak_days":
                return Math.max(stats.getCurrentStreakDays(), stats.getMaxStreakDays());
            case "courses_completed":
                return stats.getTotalCoursesCompleted();
            case "hours_studied":
                return stats.getTotalStudyMinutes() / 60;
            case "checkin_days":
                return stats.getTotalCheckinDays();
            default:
                return 0;
        }
    }
}