package com.itts.modules.student.service;

import com.itts.modules.student.dto.StudentStatsResponse;

/**
 * 学生服务接口
 * <p>
 * [Phase 6 #7] getDashboard 已拆分至 StudentDashboardService
 */
public interface StudentService {

    /**
     * 获取学生学习统计
     *
     * @param userId 用户ID
     * @return 学习统计数据
     */
    StudentStatsResponse getStats(Long userId);

    /**
     * 添加经验值
     *
     * @param userId 用户ID
     * @param exp 经验值
     */
    void addExperience(Long userId, Integer exp);

    /**
     * 检查并解锁成就
     *
     * @param userId 用户ID
     */
    void checkAndUnlockAchievements(Long userId);
}
