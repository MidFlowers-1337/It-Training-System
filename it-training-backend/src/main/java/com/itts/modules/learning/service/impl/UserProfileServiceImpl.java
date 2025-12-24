package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.utils.LevelDifficultyUtils;
import com.itts.modules.learning.dto.UserProfileResponse;
import com.itts.modules.learning.entity.Achievement;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.entity.StudyCheckin;
import com.itts.modules.learning.entity.UserAchievement;
import com.itts.modules.learning.entity.UserLearningStats;
import com.itts.modules.learning.entity.UserPreference;
import com.itts.modules.learning.entity.UserSkillTag;
import com.itts.modules.learning.mapper.AchievementMapper;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.learning.mapper.StudyCheckinMapper;
import com.itts.modules.learning.mapper.UserAchievementMapper;
import com.itts.modules.learning.mapper.UserLearningStatsMapper;
import com.itts.modules.learning.mapper.UserPreferenceMapper;
import com.itts.modules.learning.mapper.UserSkillTagMapper;
import com.itts.modules.learning.service.UserProfileService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户画像服务实现
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final SysUserMapper userMapper;
    private final LearningProgressMapper learningProgressMapper;
    private final StudyCheckinMapper studyCheckinMapper;
    private final UserAchievementMapper userAchievementMapper;
    private final UserLearningStatsMapper userLearningStatsMapper;
    private final UserSkillTagMapper userSkillTagMapper;
    private final UserPreferenceMapper userPreferenceMapper;
    private final AchievementMapper achievementMapper;

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        // 获取用户基本信息
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        // 获取学习统计
        UserLearningStats stats = userLearningStatsMapper.selectOne(
            new LambdaQueryWrapper<UserLearningStats>().eq(UserLearningStats::getUserId, userId)
        );

        // 获取技能标签
        List<UserSkillTag> skillTags = userSkillTagMapper.selectList(
            new LambdaQueryWrapper<UserSkillTag>()
                .eq(UserSkillTag::getUserId, userId)
                .orderByDesc(UserSkillTag::getProficiencyLevel)
        );

        // 获取用户偏好
        UserPreference preference = userPreferenceMapper.selectOne(
            new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId)
        );

        // 获取成就数量
        Long achievementCount = userAchievementMapper.selectCount(
            new LambdaQueryWrapper<UserAchievement>().eq(UserAchievement::getUserId, userId)
        );

        // 获取学习进度统计
        List<LearningProgress> progressList = learningProgressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>().eq(LearningProgress::getUserId, userId)
        );
        int completedCourses = (int) progressList.stream()
            .filter(p -> "COMPLETED".equals(p.getStatus()))
            .count();
        int inProgressCourses = (int) progressList.stream()
            .filter(p -> "IN_PROGRESS".equals(p.getStatus()))
            .count();

        // 构建响应
        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
            .userId(userId)
            .username(user.getUsername())
            .realName(user.getRealName())
            .registerTime(user.getCreatedAt())
            .completedCourses(completedCourses)
            .inProgressCourses(inProgressCourses)
            .achievementCount(achievementCount.intValue());

        // 设置学习统计
        if (stats != null) {
            // 根据学习时长计算等级（使用工具类）
            int level = LevelDifficultyUtils.calculateLevelByMinutes(stats.getTotalStudyMinutes());
            builder.totalStudyMinutes(stats.getTotalStudyMinutes())
                   .learningLevel(level)
                   .levelName(LevelDifficultyUtils.getLevelName(level))
                   .currentStreak(stats.getCurrentStreakDays())
                   .maxStreak(stats.getMaxStreakDays())
                   .achievementPoints(stats.getTotalAchievementPoints())
                   .lastStudyTime(stats.getLastStudyDate() != null ? 
                       stats.getLastStudyDate().atStartOfDay() : null);
        } else {
            builder.totalStudyMinutes(0)
                   .learningLevel(1)
                   .levelName(LevelDifficultyUtils.getLevelName(1))
                   .currentStreak(0)
                   .maxStreak(0)
                   .achievementPoints(0);
        }

        // 设置技能标签
        List<UserProfileResponse.SkillTagItem> skillTagItems = skillTags.stream()
            .map(tag -> UserProfileResponse.SkillTagItem.builder()
                .tag(tag.getSkillName())
                .level(tag.getProficiencyLevel() / 20)  // 转换为1-5级
                .courseCount(0)  // 简化处理
                .build())
            .collect(Collectors.toList());
        builder.skillTags(skillTagItems);

        // 设置学习偏好
        if (preference != null) {
            List<String> categories = preference.getPreferredCategories() != null ?
                Arrays.asList(preference.getPreferredCategories().split(",")) :
                Collections.emptyList();
            builder.preference(UserProfileResponse.LearningPreference.builder()
                .preferredCategories(categories)
                .preferredDifficulty(LevelDifficultyUtils.getDifficultyName(preference.getPreferredDifficulty()))
                .dailyStudyGoal(preference.getWeeklyHours() != null ? 
                    preference.getWeeklyHours() * 60 / 7 : 30)  // 转换为每日分钟
                .preferredStudyTime("晚间")  // 默认值
                .build());
        }

        // 计算能力雷达图
        builder.abilityRadar(calculateAbilityRadar(userId, stats, progressList));

        // 计算时间分布
        builder.timeDistribution(calculateTimeDistribution(userId));

        // 计算类别分布
        builder.categoryDistribution(calculateCategoryDistribution(progressList));

        // 获取里程碑
        builder.milestones(getMilestones(userId));

        return builder.build();
    }

    @Override
    @Transactional
    public void updateSkillTags(Long userId, List<String> skillTags) {
        // 删除现有标签
        userSkillTagMapper.delete(
            new LambdaQueryWrapper<UserSkillTag>().eq(UserSkillTag::getUserId, userId)
        );

        // 添加新标签
        for (String tag : skillTags) {
            UserSkillTag skillTag = new UserSkillTag();
            skillTag.setUserId(userId);
            skillTag.setSkillName(tag);
            skillTag.setProficiencyLevel(20);  // 初始熟练度
            skillTag.setSource("self_report");
            skillTag.setCreatedAt(LocalDateTime.now());
            skillTag.setUpdatedAt(LocalDateTime.now());
            userSkillTagMapper.insert(skillTag);
        }
    }

    @Override
    @Transactional
    public void updatePreferences(Long userId, List<String> preferredCategories,
                                  String preferredDifficulty, Integer dailyStudyGoal) {
        UserPreference preference = userPreferenceMapper.selectOne(
            new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId)
        );

        if (preference == null) {
            preference = new UserPreference();
            preference.setUserId(userId);
            preference.setCreatedAt(LocalDateTime.now());
        }

        if (preferredCategories != null && !preferredCategories.isEmpty()) {
            preference.setPreferredCategories(String.join(",", preferredCategories));
        }
        if (preferredDifficulty != null) {
            preference.setPreferredDifficulty(LevelDifficultyUtils.getDifficultyValue(preferredDifficulty));
        }
        if (dailyStudyGoal != null) {
            preference.setWeeklyHours(dailyStudyGoal * 7 / 60);  // 转换为每周小时
        }
        preference.setUpdatedAt(LocalDateTime.now());

        if (preference.getId() == null) {
            userPreferenceMapper.insert(preference);
        } else {
            userPreferenceMapper.updateById(preference);
        }
    }

    @Override
    public void analyzeLearningBehavior(Long userId) {
        // 分析学习行为，更新用户画像
        // 1. 分析学习时间偏好
        analyzeStudyTimePreference(userId);
        
        // 2. 分析类别偏好
        analyzeCategoryPreference(userId);
        
        // 3. 更新技能标签
        updateSkillTagsFromLearning(userId);
    }

    @Override
    public Map<String, Object> getLearningAbilityAssessment(Long userId) {
        Map<String, Object> assessment = new HashMap<>();
        
        UserLearningStats stats = userLearningStatsMapper.selectOne(
            new LambdaQueryWrapper<UserLearningStats>().eq(UserLearningStats::getUserId, userId)
        );
        
        List<LearningProgress> progressList = learningProgressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>().eq(LearningProgress::getUserId, userId)
        );

        // 计算各项能力指标
        UserProfileResponse.AbilityRadar radar = calculateAbilityRadar(userId, stats, progressList);
        assessment.put("radar", radar);

        // 计算综合评分
        int overallScore = (radar.getLearningSpeed() + radar.getPersistence() + 
            radar.getComprehension() + radar.getPracticeAbility() + 
            radar.getBreadth() + radar.getDepth()) / 6;
        assessment.put("overallScore", overallScore);

        // 生成评估建议
        List<String> suggestions = generateSuggestions(radar);
        assessment.put("suggestions", suggestions);

        // 学习者类型
        String learnerType = determineLearnerType(radar);
        assessment.put("learnerType", learnerType);

        return assessment;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 计算能力雷达图数据
     */
    private UserProfileResponse.AbilityRadar calculateAbilityRadar(Long userId, 
            UserLearningStats stats, List<LearningProgress> progressList) {
        
        int learningSpeed = 50;
        int persistence = 50;
        int comprehension = 50;
        int practiceAbility = 50;
        int breadth = 50;
        int depth = 50;

        if (stats != null) {
            // 学习速度：基于平均完成课程时间
            int totalCourses = stats.getTotalCoursesCompleted() != null ? 
                stats.getTotalCoursesCompleted() : 0;
            int totalMinutes = stats.getTotalStudyMinutes() != null ? 
                stats.getTotalStudyMinutes() : 0;
            if (totalCourses > 0 && totalMinutes > 0) {
                int avgMinutesPerCourse = totalMinutes / totalCourses;
                learningSpeed = Math.min(100, Math.max(20, 100 - avgMinutesPerCourse / 10));
            }

            // 坚持度：基于连续打卡天数
            int currentStreak = stats.getCurrentStreakDays() != null ? 
                stats.getCurrentStreakDays() : 0;
            int maxStreak = stats.getMaxStreakDays() != null ? 
                stats.getMaxStreakDays() : 0;
            persistence = Math.min(100, 30 + currentStreak * 2 + maxStreak);

            // 知识广度：基于学习的课程数
            Set<Long> courseIds = progressList.stream()
                .map(LearningProgress::getCourseId)
                .collect(Collectors.toSet());
            breadth = Math.min(100, 30 + courseIds.size() * 10);

            // 知识深度：基于完成课程数
            depth = Math.min(100, 30 + totalCourses * 5);
        }

        // 理解力：基于课程完成率
        if (!progressList.isEmpty()) {
            double avgProgress = progressList.stream()
                .mapToInt(LearningProgress::getProgressPercent)
                .average()
                .orElse(50);
            comprehension = (int) Math.min(100, avgProgress);
        }

        // 实践能力：基于学习时长
        if (stats != null && stats.getTotalStudyMinutes() != null && stats.getTotalStudyMinutes() > 0) {
            practiceAbility = Math.min(100, 30 + stats.getTotalStudyMinutes() / 60);
        }

        return UserProfileResponse.AbilityRadar.builder()
            .learningSpeed(learningSpeed)
            .persistence(persistence)
            .comprehension(comprehension)
            .practiceAbility(practiceAbility)
            .breadth(breadth)
            .depth(depth)
            .build();
    }

    /**
     * 计算学习时间分布
     */
    private List<UserProfileResponse.TimeDistributionItem> calculateTimeDistribution(Long userId) {
        // 简化实现：返回固定时间段
        List<UserProfileResponse.TimeDistributionItem> distribution = new ArrayList<>();
        String[] timeSlots = {"06:00-09:00", "09:00-12:00", "12:00-14:00", 
                             "14:00-18:00", "18:00-21:00", "21:00-24:00"};
        
        // 实际应该从打卡记录中统计
        for (String slot : timeSlots) {
            distribution.add(UserProfileResponse.TimeDistributionItem.builder()
                .timeSlot(slot)
                .minutes(0)
                .percentage(0.0)
                .build());
        }
        
        return distribution;
    }

    /**
     * 计算类别分布
     */
    private List<UserProfileResponse.CategoryDistributionItem> calculateCategoryDistribution(
            List<LearningProgress> progressList) {
        // 简化实现
        Map<Long, Integer> courseMinutes = progressList.stream()
            .collect(Collectors.groupingBy(
                LearningProgress::getCourseId,
                Collectors.summingInt(LearningProgress::getStudyDurationMinutes)
            ));

        int totalMinutes = courseMinutes.values().stream().mapToInt(Integer::intValue).sum();
        
        List<UserProfileResponse.CategoryDistributionItem> distribution = new ArrayList<>();
        // 实际应该关联课程表获取类别信息
        
        return distribution;
    }

    /**
     * 获取学习里程碑
     */
    private List<UserProfileResponse.MilestoneItem> getMilestones(Long userId) {
        List<UserProfileResponse.MilestoneItem> milestones = new ArrayList<>();
        
        // 获取用户成就作为里程碑
        List<UserAchievement> achievements = userAchievementMapper.selectList(
            new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId)
                .orderByDesc(UserAchievement::getEarnedAt)
                .last("LIMIT 10")
        );

        for (UserAchievement ua : achievements) {
            Achievement achievement = achievementMapper.selectById(ua.getAchievementId());
            if (achievement != null) {
                milestones.add(UserProfileResponse.MilestoneItem.builder()
                    .title(achievement.getName())
                    .description(achievement.getDescription())
                    .icon(achievement.getIconUrl())
                    .achievedAt(ua.getEarnedAt())
                    .build());
            }
        }

        return milestones;
    }

    /**
     * 分析学习时间偏好
     */
    private void analyzeStudyTimePreference(Long userId) {
        // 从打卡记录分析用户偏好的学习时间段
        // 简化实现
    }

    /**
     * 分析类别偏好
     */
    private void analyzeCategoryPreference(Long userId) {
        // 从学习记录分析用户偏好的课程类别
        // 简化实现
    }

    /**
     * 从学习记录更新技能标签
     */
    private void updateSkillTagsFromLearning(Long userId) {
        // 根据完成的课程自动更新技能标签
        // 简化实现
    }

    /**
     * 生成学习建议
     */
    private List<String> generateSuggestions(UserProfileResponse.AbilityRadar radar) {
        List<String> suggestions = new ArrayList<>();
        
        if (radar.getLearningSpeed() < 50) {
            suggestions.add("建议适当加快学习节奏，可以尝试设定每日学习目标");
        }
        if (radar.getPersistence() < 50) {
            suggestions.add("坚持是成功的关键，建议每天固定时间学习，养成习惯");
        }
        if (radar.getComprehension() < 50) {
            suggestions.add("建议多做练习和复习，加深对知识的理解");
        }
        if (radar.getPracticeAbility() < 50) {
            suggestions.add("理论结合实践，建议多动手做项目练习");
        }
        if (radar.getBreadth() < 50) {
            suggestions.add("建议拓宽学习领域，尝试不同类型的课程");
        }
        if (radar.getDepth() < 50) {
            suggestions.add("建议深入学习某一领域，成为专家");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("继续保持良好的学习状态！");
        }
        
        return suggestions;
    }

    /**
     * 确定学习者类型
     */
    private String determineLearnerType(UserProfileResponse.AbilityRadar radar) {
        int maxValue = Math.max(radar.getLearningSpeed(),
            Math.max(radar.getPersistence(),
            Math.max(radar.getComprehension(),
            Math.max(radar.getPracticeAbility(),
            Math.max(radar.getBreadth(), radar.getDepth())))));

        if (maxValue == radar.getLearningSpeed()) {
            return "快速学习者";
        } else if (maxValue == radar.getPersistence()) {
            return "坚持型学习者";
        } else if (maxValue == radar.getComprehension()) {
            return "理解型学习者";
        } else if (maxValue == radar.getPracticeAbility()) {
            return "实践型学习者";
        } else if (maxValue == radar.getBreadth()) {
            return "广博型学习者";
        } else {
            return "专精型学习者";
        }
    }
}