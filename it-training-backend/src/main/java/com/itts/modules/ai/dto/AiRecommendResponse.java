package com.itts.modules.ai.dto;

import lombok.Data;

import java.util.List;

/**
 * AI推荐响应 DTO
 */
@Data
public class AiRecommendResponse {

    /**
     * 推荐的课程列表
     */
    private List<RecommendedCourse> courses;

    /**
     * 整体推荐理由
     */
    private String overallReason;

    /**
     * 建议的学习顺序说明
     */
    private String learningPath;

    /**
     * 是否为降级结果
     */
    private boolean fallback;

    /**
     * 降级提示信息
     */
    private String fallbackMessage;

    /**
     * 推荐的单个课程信息
     */
    @Data
    public static class RecommendedCourse {
        /**
         * 课程ID
         */
        private Long courseId;

        /**
         * 课程编码
         */
        private String courseCode;

        /**
         * 课程名称
         */
        private String courseName;

        /**
         * 课程分类
         */
        private String category;

        /**
         * 难度等级
         */
        private Integer difficulty;

        /**
         * 难度名称
         */
        private String difficultyName;

        /**
         * 推荐顺序
         */
        private Integer order;

        /**
         * 推荐理由
         */
        private String reason;

        /**
         * 课程标签
         */
        private String tags;
    }
}
