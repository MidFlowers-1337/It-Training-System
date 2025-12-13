package com.itts.modules.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itts.modules.learning.dto.UserStatsResponse;
import com.itts.modules.learning.entity.UserLearningStats;

/**
 * 用户学习统计服务接口
 */
public interface UserLearningStatsService extends IService<UserLearningStats> {

    /**
     * 获取用户学习统计
     * @param userId 用户ID
     * @return 学习统计
     */
    UserStatsResponse getUserStats(Long userId);

    /**
     * 初始化用户学习统计（注册时调用）
     * @param userId 用户ID
     * @return 初始化的统计记录
     */
    UserLearningStats initUserStats(Long userId);

    /**
     * 更新用户学习统计
     * @param userId 用户ID
     * @param studyMinutes 新增学习时长
     */
    void addStudyTime(Long userId, int studyMinutes);

    /**
     * 更新连续打卡天数
     * @param userId 用户ID
     */
    void updateStreakDays(Long userId);

    /**
     * 增加完成课程数
     * @param userId 用户ID
     */
    void incrementCompletedCourses(Long userId);

    /**
     * 增加报名课程数
     * @param userId 用户ID
     */
    void incrementEnrolledCourses(Long userId);

    /**
     * 增加成就积分
     * @param userId 用户ID
     * @param points 积分
     */
    void addAchievementPoints(Long userId, int points);

    /**
     * 获取或创建用户统计
     * @param userId 用户ID
     * @return 用户统计
     */
    UserLearningStats getOrCreateStats(Long userId);
}