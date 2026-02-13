package com.itts.modules.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课程评价响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewResponse {

    /**
     * 评价ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 评分(1-5)
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
