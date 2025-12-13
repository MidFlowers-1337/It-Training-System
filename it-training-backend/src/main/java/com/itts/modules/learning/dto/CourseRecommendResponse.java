package com.itts.modules.learning.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程推荐响应 DTO
 */
@Data
public class CourseRecommendResponse {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 课程类别
     */
    private String category;

    /**
     * 类别名称
     */
    private String categoryName;

    /**
     * 难度级别
     */
    private String difficulty;

    /**
     * 难度名称
     */
    private String difficultyName;

    /**
     * 课程价格
     */
    private BigDecimal price;

    /**
     * 课程时长(小时)
     */
    private Integer durationHours;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 推荐分数 (0-100)
     */
    private Double recommendScore;

    /**
     * 推荐理由
     */
    private List<String> recommendReasons;

    /**
     * 推荐类型: content_based, collaborative, hybrid, popular, new
     */
    private String recommendType;

    /**
     * 相似度分数 (用于基于内容的推荐)
     */
    private Double similarityScore;

    /**
     * 预测评分 (用于协同过滤)
     */
    private Double predictedRating;

    /**
     * 课程标签
     */
    private List<String> tags;

    /**
     * 报名人数
     */
    private Integer enrollmentCount;

    /**
     * 平均评分
     */
    private Double avgRating;
}