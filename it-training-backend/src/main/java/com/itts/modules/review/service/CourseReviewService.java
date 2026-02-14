package com.itts.modules.review.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.modules.review.dto.CourseReviewRequest;
import com.itts.modules.review.dto.CourseReviewResponse;
import com.itts.modules.review.dto.CourseReviewSummaryResponse;

/**
 * 课程评价服务接口
 */
public interface CourseReviewService {

    /**
     * 添加课程评价
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param request 评价请求
     * @return 评价响应
     */
    CourseReviewResponse addReview(Long userId, Long courseId, CourseReviewRequest request);

    /**
     * 获取课程评价列表（分页）
     * @param courseId 课程ID
     * @param page 页码
     * @param size 每页大小
     * @return 评价分页列表
     */
    IPage<CourseReviewResponse> getCourseReviews(Long courseId, int page, int size);

    /**
     * 获取课程评分摘要
     * @param courseId 课程ID
     * @return 评分摘要（含评分分布）
     */
    CourseReviewSummaryResponse getReviewSummary(Long courseId);
}
