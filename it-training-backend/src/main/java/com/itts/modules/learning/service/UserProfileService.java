package com.itts.modules.learning.service;

import com.itts.modules.learning.dto.UserProfileResponse;

/**
 * 用户画像服务接口
 */
public interface UserProfileService {

    /**
     * 获取用户完整画像
     * @param userId 用户ID
     * @return 用户画像
     */
    UserProfileResponse getUserProfile(Long userId);

    /**
     * 更新用户技能标签
     * @param userId 用户ID
     * @param skillTags 技能标签列表
     */
    void updateSkillTags(Long userId, java.util.List<String> skillTags);

    /**
     * 更新用户学习偏好
     * @param userId 用户ID
     * @param preferredCategories 偏好类别
     * @param preferredDifficulty 偏好难度
     * @param dailyStudyGoal 每日学习目标（分钟）
     */
    void updatePreferences(Long userId, java.util.List<String> preferredCategories, 
                          String preferredDifficulty, Integer dailyStudyGoal);

    /**
     * 分析用户学习行为
     * @param userId 用户ID
     */
    void analyzeLearningBehavior(Long userId);

    /**
     * 获取用户学习能力评估
     * @param userId 用户ID
     * @return 能力评估结果
     */
    java.util.Map<String, Object> getLearningAbilityAssessment(Long userId);
}