package com.itts.modules.recommend.service;

import com.itts.modules.recommend.dto.CourseRecommendResponse;

import java.util.List;

/**
 * 基于内容的推荐服务接口
 */
public interface ContentBasedRecommendService {

    /**
     * 基于用户历史学习记录推荐课程
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> recommendByUserHistory(Long userId, int limit);

    /**
     * 基于指定课程推荐相似课程
     * @param courseId 课程ID
     * @param limit 推荐数量
     * @return 相似课程列表
     */
    List<CourseRecommendResponse> recommendSimilarCourses(Long courseId, int limit);

    /**
     * 基于用户技能标签推荐课程
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> recommendByUserSkills(Long userId, int limit);

    /**
     * 基于用户偏好推荐课程
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> recommendByUserPreference(Long userId, int limit);

    /**
     * 计算两个课程的相似度
     * @param courseId1 课程1 ID
     * @param courseId2 课程2 ID
     * @return 相似度分数 (0-1)
     */
    double calculateCourseSimilarity(Long courseId1, Long courseId2);

    /**
     * 更新课程相似度矩阵
     */
    void updateCourseSimilarityMatrix();
}