package com.itts.modules.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itts.modules.learning.dto.AchievementResponse;
import com.itts.modules.learning.entity.Achievement;

import java.util.List;

/**
 * 成就服务接口
 */
public interface AchievementService extends IService<Achievement> {

    /**
     * 获取所有成就列表（包含用户获得状态）
     * @param userId 用户ID
     * @return 成就列表
     */
    List<AchievementResponse> getAllAchievements(Long userId);

    /**
     * 获取用户已获得的成就
     * @param userId 用户ID
     * @return 已获得的成就列表
     */
    List<AchievementResponse> getUserAchievements(Long userId);

    /**
     * 获取用户最近获得的成就
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 最近获得的成就列表
     */
    List<AchievementResponse> getRecentAchievements(Long userId, int limit);

    /**
     * 检查并授予成就
     * @param userId 用户ID
     * @return 新获得的成就列表
     */
    List<AchievementResponse> checkAndGrantAchievements(Long userId);

    /**
     * 获取用户成就积分
     * @param userId 用户ID
     * @return 总积分
     */
    int getUserAchievementPoints(Long userId);

    /**
     * 获取成就详情
     * @param achievementId 成就ID
     * @param userId 用户ID（用于判断是否已获得）
     * @return 成就详情
     */
    AchievementResponse getAchievementDetail(Long achievementId, Long userId);
}