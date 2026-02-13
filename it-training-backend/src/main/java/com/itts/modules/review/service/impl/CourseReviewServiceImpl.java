package com.itts.modules.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.review.dto.CourseReviewRequest;
import com.itts.modules.review.dto.CourseReviewResponse;
import com.itts.modules.review.dto.CourseReviewSummaryResponse;
import com.itts.modules.review.entity.CourseReview;
import com.itts.modules.review.mapper.CourseReviewMapper;
import com.itts.modules.review.service.CourseReviewService;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程评价服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseReviewServiceImpl implements CourseReviewService {

    private final CourseReviewMapper courseReviewMapper;
    private final LearningProgressMapper learningProgressMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseReviewResponse addReview(Long userId, Long courseId, CourseReviewRequest request) {
        log.info("用户 {} 评价课程 {}", userId, courseId);

        // 检查用户是否有该课程的学习进度
        LearningProgress progress = learningProgressMapper.selectOne(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getCourseId, courseId)
        );

        if (progress == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "您尚未学习该课程，无法评价");
        }

        // 创建评价
        CourseReview review = new CourseReview();
        review.setUserId(userId);
        review.setCourseId(courseId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        try {
            courseReviewMapper.insert(review);
        } catch (DuplicateKeyException e) {
            // 唯一约束冲突说明已评价过
            throw new BusinessException(ErrorCode.PARAM_ERROR, "您已评价过该课程，不能重复评价");
        }

        // 构建响应
        SysUser user = sysUserMapper.selectById(userId);
        return convertToResponse(review, user != null ? user.getUsername() : "未知用户");
    }

    @Override
    public IPage<CourseReviewResponse> getCourseReviews(Long courseId, int page, int size) {
        Page<CourseReview> pageParam = new Page<>(page, size);

        IPage<CourseReview> reviewPage = courseReviewMapper.selectPage(pageParam,
            new LambdaQueryWrapper<CourseReview>()
                .eq(CourseReview::getCourseId, courseId)
                .orderByDesc(CourseReview::getCreatedAt)
        );

        // 批量查询用户名，避免 N+1
        Set<Long> userIds = reviewPage.getRecords().stream()
            .map(CourseReview::getUserId)
            .collect(Collectors.toSet());

        Map<Long, String> usernameMap = userIds.isEmpty()
            ? Map.of()
            : sysUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));

        return reviewPage.convert(review ->
            convertToResponse(review, usernameMap.getOrDefault(review.getUserId(), "未知用户"))
        );
    }

    @Override
    public CourseReviewSummaryResponse getReviewSummary(Long courseId) {
        // 查询该课程所有评价
        List<CourseReview> reviews = courseReviewMapper.selectList(
            new LambdaQueryWrapper<CourseReview>()
                .eq(CourseReview::getCourseId, courseId)
        );

        CourseReviewSummaryResponse response = new CourseReviewSummaryResponse();
        response.setTotalReviews((long) reviews.size());

        // 计算平均分
        if (reviews.isEmpty()) {
            response.setAverageRating(0.0);
        } else {
            double avg = reviews.stream()
                .mapToInt(CourseReview::getRating)
                .average()
                .orElse(0.0);
            response.setAverageRating(Math.round(avg * 10.0) / 10.0);
        }

        // 评分分布（1-5 每个分数有多少人）
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }
        reviews.stream()
            .collect(Collectors.groupingBy(CourseReview::getRating, Collectors.counting()))
            .forEach(distribution::put);

        response.setRatingDistribution(distribution);

        return response;
    }

    /**
     * 转换为响应 DTO
     */
    private CourseReviewResponse convertToResponse(CourseReview review, String username) {
        CourseReviewResponse response = new CourseReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUserId());
        response.setUsername(username);
        response.setCourseId(review.getCourseId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}
