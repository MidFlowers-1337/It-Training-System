package com.itts.modules.recommend.service;

import com.itts.modules.recommend.dto.CourseRecommendResponse;

import java.util.List;

/**
 * 混合推荐服务接口
 * 结合基于内容的推荐和协同过滤推荐
 */
public interface HybridRecommendService {

    /**
     * 获取混合推荐结果
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> getHybridRecommendations(Long userId, int limit);

    /**
     * 获取个性化首页推荐
     * 包含多种推荐类型的混合结果
     * @param userId 用户ID
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> getHomePageRecommendations(Long userId);

    /**
     * 获取"猜你喜欢"推荐
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> getYouMayLike(Long userId, int limit);

    /**
     * 获取热门课程推荐
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> getPopularCourses(int limit);

    /**
     * 获取新课程推荐
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendResponse> getNewCourses(int limit);
}