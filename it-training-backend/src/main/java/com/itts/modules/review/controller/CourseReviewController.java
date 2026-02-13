package com.itts.modules.review.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.review.dto.CourseReviewRequest;
import com.itts.modules.review.dto.CourseReviewResponse;
import com.itts.modules.review.dto.CourseReviewSummaryResponse;
import com.itts.modules.review.service.CourseReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程评价控制器
 */
@Tag(name = "课程评价", description = "课程评价和评分相关接口")
@RestController
@RequestMapping("/api/v1/courses/{courseId}/reviews")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    @Operation(summary = "添加课程评价")
    @PostMapping
    public R<CourseReviewResponse> addReview(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseReviewRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        CourseReviewResponse response = courseReviewService.addReview(userId, courseId, request);
        return R.ok(response);
    }

    @Operation(summary = "获取课程评价列表")
    @GetMapping
    public R<IPage<CourseReviewResponse>> getCourseReviews(
            @PathVariable Long courseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        IPage<CourseReviewResponse> reviews = courseReviewService.getCourseReviews(courseId, page, size);
        return R.ok(reviews);
    }

    @Operation(summary = "获取课程评分摘要")
    @GetMapping("/summary")
    public R<CourseReviewSummaryResponse> getReviewSummary(@PathVariable Long courseId) {
        CourseReviewSummaryResponse summary = courseReviewService.getReviewSummary(courseId);
        return R.ok(summary);
    }
}
