package com.itts.modules.recommend.controller;

import com.itts.common.response.R;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.recommend.dto.CourseRecommendResponse;
import com.itts.modules.recommend.dto.RecommendFeedbackRequest;
import com.itts.modules.recommend.entity.RecommendFeedback;
import com.itts.modules.recommend.mapper.RecommendFeedbackMapper;
import com.itts.modules.recommend.service.CollaborativeFilteringService;
import com.itts.modules.recommend.service.ContentBasedRecommendService;
import com.itts.modules.recommend.service.HybridRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "智能推荐", description = "智能课程推荐相关接口")
@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class RecommendController {

    private final ContentBasedRecommendService contentBasedRecommendService;
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final HybridRecommendService hybridRecommendService;
    private final RecommendFeedbackMapper recommendFeedbackMapper;

    // ==================== 基于内容的推荐 ====================

    @Operation(summary = "基于学习历史推荐课程")
    @GetMapping("/recommend/history")
    public R<List<CourseRecommendResponse>> recommendByHistory(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(contentBasedRecommendService.recommendByUserHistory(userId, limit));
    }

    @Operation(summary = "推荐相似课程")
    @GetMapping("/recommend/similar/{courseId}")
    public R<List<CourseRecommendResponse>> recommendSimilarCourses(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "5") int limit) {
        return R.ok(contentBasedRecommendService.recommendSimilarCourses(courseId, limit));
    }

    @Operation(summary = "基于技能标签推荐课程")
    @GetMapping("/recommend/skills")
    public R<List<CourseRecommendResponse>> recommendBySkills(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(contentBasedRecommendService.recommendByUserSkills(userId, limit));
    }

    @Operation(summary = "基于用户偏好推荐课程")
    @GetMapping("/recommend/preference")
    public R<List<CourseRecommendResponse>> recommendByPreference(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(contentBasedRecommendService.recommendByUserPreference(userId, limit));
    }

    // ==================== 协同过滤推荐 ====================

    @Operation(summary = "基于用户的协同过滤推荐")
    @GetMapping("/recommend/user-based")
    public R<List<CourseRecommendResponse>> recommendByUserBased(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(collaborativeFilteringService.recommendByUserBased(userId, limit));
    }

    @Operation(summary = "基于物品的协同过滤推荐")
    @GetMapping("/recommend/item-based")
    public R<List<CourseRecommendResponse>> recommendByItemBased(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(collaborativeFilteringService.recommendByItemBased(userId, limit));
    }

    @Operation(summary = "获取相似用户")
    @GetMapping("/recommend/similar-users")
    public R<List<Long>> getSimilarUsers(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(collaborativeFilteringService.getSimilarUsers(userId, limit));
    }

    // ==================== 混合推荐 ====================

    @Operation(summary = "获取混合推荐课程")
    @GetMapping("/recommend/hybrid")
    public R<List<CourseRecommendResponse>> getHybridRecommendations(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(hybridRecommendService.getHybridRecommendations(userId, limit));
    }

    @Operation(summary = "获取首页推荐课程")
    @GetMapping("/recommend/home")
    public R<List<CourseRecommendResponse>> getHomePageRecommendations() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(hybridRecommendService.getHomePageRecommendations(userId));
    }

    @Operation(summary = "获取猜你喜欢课程")
    @GetMapping("/recommend/you-may-like")
    public R<List<CourseRecommendResponse>> getYouMayLike(
            @RequestParam(defaultValue = "6") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(hybridRecommendService.getYouMayLike(userId, limit));
    }

    @Operation(summary = "获取热门课程")
    @GetMapping("/recommend/popular")
    public R<List<CourseRecommendResponse>> getPopularCourses(
            @RequestParam(defaultValue = "10") int limit) {
        return R.ok(hybridRecommendService.getPopularCourses(limit));
    }

    @Operation(summary = "获取新上架课程")
    @GetMapping("/recommend/new")
    public R<List<CourseRecommendResponse>> getNewCourses(
            @RequestParam(defaultValue = "10") int limit) {
        return R.ok(hybridRecommendService.getNewCourses(limit));
    }

    // ==================== 推荐反馈 ====================

    @Operation(summary = "提交推荐反馈")
    @PostMapping("/recommend/feedback")
    public R<Void> submitFeedback(@Valid @RequestBody RecommendFeedbackRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("提交推荐反馈: userId={}, courseId={}, type={}", userId, request.getCourseId(), request.getFeedbackType());

        RecommendFeedback feedback = new RecommendFeedback();
        feedback.setUserId(userId);
        feedback.setCourseId(request.getCourseId());
        feedback.setFeedbackType(request.getFeedbackType());
        feedback.setCreatedAt(LocalDateTime.now());

        // 使用 UNIQUE KEY (user_id, course_id) 实现 upsert 语义：
        // 如果已存在则更新反馈类型，否则插入新记录
        try {
            recommendFeedbackMapper.insert(feedback);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 已有反馈，更新
            com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<RecommendFeedback> wrapper =
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
            wrapper.eq(RecommendFeedback::getUserId, userId)
                   .eq(RecommendFeedback::getCourseId, request.getCourseId())
                   .set(RecommendFeedback::getFeedbackType, request.getFeedbackType())
                   .set(RecommendFeedback::getCreatedAt, LocalDateTime.now());
            recommendFeedbackMapper.update(null, wrapper);
        }

        return R.ok();
    }
}
