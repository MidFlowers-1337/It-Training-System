package com.itts.modules.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程评分摘要 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewSummary {

    /**
     * 平均评分
     */
    private Double averageRating;

    /**
     * 评价总数
     */
    private Long reviewCount;
}
