package com.itts.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户课程查询辅助工具类
 * 提取用户已报名/已学习课程的通用查询逻辑，避免重复代码
 */
@Component
@RequiredArgsConstructor
public class UserCourseQueryHelper {

    private final EnrollmentMapper enrollmentMapper;
    private final ClassSessionMapper sessionMapper;
    private final LearningProgressMapper progressMapper;

    /**
     * 获取用户已报名的课程ID集合
     *
     * @param userId 用户ID
     * @return 课程ID集合
     */
    public Set<Long> getEnrolledCourseIds(Long userId) {
        // 获取用户报名的所有班期
        List<Enrollment> enrollments = enrollmentMapper.selectList(
            new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getUserId, userId)
                .eq(Enrollment::getStatus, 0) // 已报名状态
        );

        // 通过班期获取课程ID
        Set<Long> courseIds = new HashSet<>();
        for (Enrollment enrollment : enrollments) {
            ClassSession session = sessionMapper.selectById(enrollment.getSessionId());
            if (session != null) {
                courseIds.add(session.getCourseId());
            }
        }
        return courseIds;
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
     * 检查用户是否已报名或已学习某课程
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return true=已报名或已学习
     */
    public boolean hasUserCourse(Long userId, Long courseId) {
        return getAllUserCourseIds(userId).contains(courseId);
    }
}
