package com.itts.common.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户课程查询辅助工具类
 * 提取用户已报名/已学习课程的通用查询逻辑，避免重复代码
 *
 * [Phase 1.1] 从 common.utils 迁移到 common.util，统一包路径
 */
@Component
@RequiredArgsConstructor
public class UserCourseQueryHelper {

    private final EnrollmentMapper enrollmentMapper;
    private final LearningProgressMapper progressMapper;

    /**
     * 获取用户已报名的课程ID集合
     * 使用 JOIN SQL 一次查询，替代原先的 N+1 查询模式
     *
     * @param userId 用户ID
     * @return 课程ID集合
     */
    public Set<Long> getEnrolledCourseIds(Long userId) {
        List<Long> courseIds = enrollmentMapper.selectEnrolledCourseIdsByUserId(userId);
        return new HashSet<>(courseIds);
    }

    /**
     * 获取用户已学习的课程ID集合
     *
     * @param userId 用户ID
     * @return 课程ID集合
     */
    public Set<Long> getLearnedCourseIds(Long userId) {
        List<LearningProgress> progressList = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
        );
        return progressList.stream()
            .map(LearningProgress::getCourseId)
            .collect(Collectors.toSet());
    }

    /**
     * 获取用户已报名+已学习的所有课程ID集合
     *
     * @param userId 用户ID
     * @return 课程ID集合
     */
    public Set<Long> getAllUserCourseIds(Long userId) {
        Set<Long> allIds = new HashSet<>();
        allIds.addAll(getEnrolledCourseIds(userId));
        allIds.addAll(getLearnedCourseIds(userId));
        return allIds;
    }

    /**
     * 获取用户学习进度列表
     *
     * @param userId 用户ID
     * @return 学习进度列表
     */
    public List<LearningProgress> getUserProgressList(Long userId) {
        return progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
        );
    }

    /**
     * 检查用户是否已报名或已学习某课程（高效版本）
     * 优化：先通过精确条件查询单条记录是否存在，避免加载用户全部报名和学习记录
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return true=已报名或已学习
     */
    public boolean hasUserCourse(Long userId, Long courseId) {
        // 1. 先检查是否有该课程的学习进度记录（单表精确查询，最快）
        Long progressCount = progressMapper.selectCount(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getCourseId, courseId)
        );
        if (progressCount > 0) {
            return true;
        }

        // 2. 再检查是否通过班期报名了该课程
        //    由于 enrollment 表没有直接的 course_id 字段，需要通过 session 关联查询
        //    这里保留原有逻辑获取已报名课程ID集合
        return getEnrolledCourseIds(userId).contains(courseId);
    }
}
