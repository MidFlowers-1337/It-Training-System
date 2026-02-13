package com.itts.modules.recommend.service;

import com.itts.modules.recommend.dto.CourseRecommendResponse;

import java.util.List;

/**
 * 协同过滤推荐服务接口
 */
public interface CollaborativeFilteringService {

    /**
     * 基于用户的协同过滤推荐
     * 找到与当前用户相似的用户，推荐他们学习过但当前用户未学习的课程
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> recommendByUserBased(Long userId, int limit);

    /**
     * 基于物品的协同过滤推荐
     * 基于用户学习过的课程，推荐与这些课程经常被一起学习的其他课程
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> recommendByItemBased(Long userId, int limit);

    /**
     * 计算两个用户的相似度
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 相似度分数 (0-1)
     */
    double calculateUserSimilarity(Long userId1, Long userId2);

    /**
     * 获取相似用户列表
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 相似用户ID列表
     */
    List<Long> getSimilarUsers(Long userId, int limit);
}