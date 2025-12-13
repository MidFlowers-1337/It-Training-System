package com.itts.modules.ai.service;

import com.itts.modules.ai.dto.AiRecommendResponse;

/**
 * AI推荐服务接口
 */
public interface AiRecommendService {

    /**
     * 根据用户学习目标获取课程推荐
     * @param learningGoal 用户输入的学习目标
     * @return 推荐结果
     */
    AiRecommendResponse getRecommendation(String learningGoal);
}
