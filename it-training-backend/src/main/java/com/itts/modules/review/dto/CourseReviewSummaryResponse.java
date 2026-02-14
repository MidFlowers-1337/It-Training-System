package com.itts.modules.review.dto;

import lombok.Data;

import java.util.Map;

/**
 * 课程评价摘要响应 DTO
 */
@Data
public class CourseReviewSummaryResponse {

    /**
     * 平均评分
     */
    private Double averageRating;

    /**
     * 评价总数
     */
    private Long totalReviews;

    /**
     * 评分分布（key: 评分1-5, value: 数量）
     */
    private Map<Integer, Long> ratingDistribution;
}
