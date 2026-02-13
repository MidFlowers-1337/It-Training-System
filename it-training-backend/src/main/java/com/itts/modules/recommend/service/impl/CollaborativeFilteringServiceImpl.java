package com.itts.modules.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.common.util.LevelDifficultyUtils;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.recommend.dto.CourseRecommendResponse;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.recommend.service.CollaborativeFilteringService;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 协同过滤推荐服务实现
 *
 * [Phase 4 重构] 消除所有 N+1 查询：
 * - getUserCourseIds: 使用 JOIN 查询替代逐条 selectById
 * - getUsersWhoLearnedCourse: 使用 IN 查询替代逐条查 enrollment
 * - getSimilarUsers / recommendByUserBased: 返回 Map 传递相似度，避免重复计算
 * - recommendByItemBased: 缓存用户课程集合，批量查询课程信息
 * - buildRecommendResponse: 批量查询课程和 session 信息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollaborativeFilteringServiceImpl implements CollaborativeFilteringService {

    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final ClassSessionMapper sessionMapper;
    private final LearningProgressMapper progressMapper;

    @Override
    public List<CourseRecommendResponse> recommendByUserBased(Long userId, int limit) {
        // 获取当前用户学习的课程
        Set<Long> userCourseIds = getUserCourseIds(userId);

        if (userCourseIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 找到相似用户（携带相似度分数，避免重复计算）
        Map<Long, Double> similarUsersWithScores = getSimilarUsersWithScores(userId, userCourseIds, 20);

        if (similarUsersWithScores.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量获取所有相似用户的课程ID集合，避免循环内重复查库
        Map<Long, Set<Long>> similarUserCourseMap = batchGetUserCourseIds(similarUsersWithScores.keySet());

        // 统计相似用户学习的课程
        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();

        for (Map.Entry<Long, Double> entry : similarUsersWithScores.entrySet()) {
            Long similarUserId = entry.getKey();
            double similarity = entry.getValue();
            Set<Long> similarUserCourses = similarUserCourseMap.getOrDefault(similarUserId, Collections.emptySet());

            for (Long courseId : similarUserCourses) {
                // 排除用户已学习的课程
                if (userCourseIds.contains(courseId)) {
                    continue;
                }

                // 加权分数
                courseScores.merge(courseId, similarity, Double::sum);
                courseReasons.computeIfAbsent(courseId, k -> new ArrayList<>())
                    .add("与您学习路径相似的用户也在学习");
            }
        }

        return buildRecommendResponse(courseScores, courseReasons, "collaborative_user", limit);
    }

    @Override
    public List<CourseRecommendResponse> recommendByItemBased(Long userId, int limit) {
        // 获取当前用户学习的课程
        Set<Long> userCourseIds = getUserCourseIds(userId);

        if (userCourseIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询用户课程信息，替代循环内逐条 selectById
        Map<Long, Course> courseCache = userCourseIds.isEmpty()
            ? Collections.emptyMap()
            : courseMapper.selectBatchIds(userCourseIds).stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        // 缓存其他用户的课程集合，避免三层循环内重复查库
        Map<Long, Set<Long>> userCourseCache = new HashMap<>();

        // 找到经常与用户课程一起被学习的其他课程
        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();

        for (Long courseId : userCourseIds) {
            Course course = courseCache.get(courseId);
            if (course == null) continue;

            // 找到学习过这门课的其他用户
            List<Long> usersWhoLearnedCourse = getUsersWhoLearnedCourse(courseId);

            // 收集需要查询课程集合的用户ID（排除自己和已缓存的）
            Set<Long> uncachedUserIds = usersWhoLearnedCourse.stream()
                .filter(uid -> !uid.equals(userId) && !userCourseCache.containsKey(uid))
                .collect(Collectors.toSet());

            // 批量获取未缓存用户的课程集合
            if (!uncachedUserIds.isEmpty()) {
                Map<Long, Set<Long>> batchResult = batchGetUserCourseIds(uncachedUserIds);
                userCourseCache.putAll(batchResult);
            }

            // 统计共现
            Map<Long, Integer> coOccurrence = new HashMap<>();
            for (Long otherUserId : usersWhoLearnedCourse) {
                if (otherUserId.equals(userId)) continue;

                Set<Long> otherUserCourses = userCourseCache.getOrDefault(otherUserId, Collections.emptySet());
                for (Long otherCourseId : otherUserCourses) {
                    if (!userCourseIds.contains(otherCourseId)) {
                        coOccurrence.merge(otherCourseId, 1, Integer::sum);
                    }
                }
            }

            // 计算共现分数
            int totalUsers = usersWhoLearnedCourse.size();
            if (totalUsers == 0) continue;

            for (Map.Entry<Long, Integer> coEntry : coOccurrence.entrySet()) {
                Long otherCourseId = coEntry.getKey();
                double score = (double) coEntry.getValue() / totalUsers;

                courseScores.merge(otherCourseId, score, Double::sum);
                courseReasons.computeIfAbsent(otherCourseId, k -> new ArrayList<>())
                    .add("学习《" + course.getName() + "》的用户也在学习");
            }
        }

        return buildRecommendResponse(courseScores, courseReasons, "collaborative_item", limit);
    }

    @Override
    public double calculateUserSimilarity(Long userId1, Long userId2) {
        Set<Long> courses1 = getUserCourseIds(userId1);
        Set<Long> courses2 = getUserCourseIds(userId2);
        return calculateJaccardSimilarity(courses1, courses2);
    }

    @Override
    public List<Long> getSimilarUsers(Long userId, int limit) {
        Set<Long> userCourseIds = getUserCourseIds(userId);
        Map<Long, Double> similarUsersWithScores = getSimilarUsersWithScores(userId, userCourseIds, limit);
        // 按相似度降序返回用户ID列表（保持接口兼容）
        return similarUsersWithScores.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取用户学习的课程ID集合
     * [重构] 使用 enrollmentMapper.selectEnrolledCourseIdsByUserId() JOIN 查询替代逐条 selectById
     */
    private Set<Long> getUserCourseIds(Long userId) {
        // 从学习进度获取
        List<LearningProgress> progressList = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
                .select(LearningProgress::getCourseId)
        );

        Set<Long> courseIds = progressList.stream()
            .map(LearningProgress::getCourseId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        // 从报名记录获取 — 使用 JOIN 查询一次获取所有课程ID，替代 N+1
        List<Long> enrolledCourseIds = enrollmentMapper.selectEnrolledCourseIdsByUserId(userId);
        courseIds.addAll(enrolledCourseIds);

        return courseIds;
    }

    /**
     * 批量获取多个用户的课程ID集合
     * 用于 recommendByUserBased / recommendByItemBased 中避免循环内重复查库
     */
    private Map<Long, Set<Long>> batchGetUserCourseIds(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 批量查询所有用户的学习进度
        List<LearningProgress> allProgress = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .in(LearningProgress::getUserId, userIds)
                .select(LearningProgress::getUserId, LearningProgress::getCourseId)
        );

        // 按用户分组
        Map<Long, Set<Long>> result = new HashMap<>();
        for (LearningProgress p : allProgress) {
            if (p.getCourseId() != null) {
                result.computeIfAbsent(p.getUserId(), k -> new HashSet<>()).add(p.getCourseId());
            }
        }

        // 批量查询所有用户的报名课程ID（1次SQL替代N次循环）
        if (!userIds.isEmpty()) {
            List<Map<String, Object>> enrolledRows = enrollmentMapper.selectEnrolledCourseIdsByUserIds(userIds);
            for (Map<String, Object> row : enrolledRows) {
                Long uid = ((Number) row.get("userId")).longValue();
                Long courseId = ((Number) row.get("courseId")).longValue();
                result.computeIfAbsent(uid, k -> new HashSet<>()).add(courseId);
            }
        }

        // 确保所有 userIds 都有条目（即使为空集合）
        for (Long uid : userIds) {
            result.putIfAbsent(uid, Collections.emptySet());
        }

        return result;
    }

    /**
     * 获取学习过某课程的用户列表
     * [重构] 使用 IN 查询替代对每个 session 逐条查 enrollment
     */
    private List<Long> getUsersWhoLearnedCourse(Long courseId) {
        // 从学习进度获取
        List<LearningProgress> progressList = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getCourseId, courseId)
                .select(LearningProgress::getUserId)
        );

        Set<Long> userIds = progressList.stream()
            .map(LearningProgress::getUserId)
            .collect(Collectors.toSet());

        // 从报名记录获取 — 先查所有 session ID，再一次 IN 查询 enrollment
        List<ClassSession> sessions = sessionMapper.selectList(
            new LambdaQueryWrapper<ClassSession>()
                .eq(ClassSession::getCourseId, courseId)
                .select(ClassSession::getId)
        );

        List<Long> sessionIds = sessions.stream()
            .map(ClassSession::getId)
            .collect(Collectors.toList());

        if (!sessionIds.isEmpty()) {
            List<Enrollment> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<Enrollment>()
                    .in(Enrollment::getSessionId, sessionIds)
                    .eq(Enrollment::getStatus, 0)
                    .select(Enrollment::getUserId)
            );
            for (Enrollment enrollment : enrollments) {
                userIds.add(enrollment.getUserId());
            }
        }

        return new ArrayList<>(userIds);
    }

    /**
     * 获取相似用户及其相似度分数（内部方法，避免重复计算）
     * [重构] 返回 Map<userId, similarity>，替代原来先取 List 再重新算 similarity 的模式
     */
    private Map<Long, Double> getSimilarUsersWithScores(Long userId, Set<Long> userCourseIds, int limit) {
        if (userCourseIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 获取所有学习过相同课程的用户
        Set<Long> candidateUsers = new HashSet<>();
        for (Long courseId : userCourseIds) {
            candidateUsers.addAll(getUsersWhoLearnedCourse(courseId));
        }
        candidateUsers.remove(userId);

        if (candidateUsers.isEmpty()) {
            return Collections.emptyMap();
        }

        // 批量获取候选用户的课程集合（1次批量查询替代 N 次单独查询）
        Map<Long, Set<Long>> candidateCourseMap = batchGetUserCourseIds(candidateUsers);

        // 计算相似度（使用预取的课程集合，无额外数据库调用）
        return candidateUsers.stream()
            .map(otherUserId -> {
                Set<Long> otherCourses = candidateCourseMap.getOrDefault(otherUserId, Collections.emptySet());
                double similarity = calculateJaccardSimilarity(userCourseIds, otherCourses);
                return Map.entry(otherUserId, similarity);
            })
            .filter(entry -> entry.getValue() > 0.1) // 过滤相似度太低的用户
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 计算 Jaccard 相似度（纯计算，无数据库调用）
     */
    private double calculateJaccardSimilarity(Set<Long> set1, Set<Long> set2) {
        if (set1.isEmpty() || set2.isEmpty()) {
            return 0;
        }

        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Long> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    /**
     * 构建推荐响应
     * [重构] 批量查询课程和 session 信息，替代逐条 selectById
     */
    private List<CourseRecommendResponse> buildRecommendResponse(
            Map<Long, Double> courseScores,
            Map<Long, List<String>> courseReasons,
            String recommendType,
            int limit) {

        // 先取 Top N 的课程ID
        List<Long> candidateIds = courseScores.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        if (candidateIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询所有候选课程信息
        Map<Long, Course> courseMap = courseMapper.selectBatchIds(candidateIds).stream()
            .collect(Collectors.toMap(Course::getId, c -> c));

        // 批量查询所有候选课程的 session 报名人数
        List<ClassSession> allSessions = sessionMapper.selectList(
            new LambdaQueryWrapper<ClassSession>()
                .in(ClassSession::getCourseId, candidateIds)
        );
        Map<Long, Integer> enrollmentCountMap = new HashMap<>();
        for (ClassSession s : allSessions) {
            int count = s.getCurrentEnrollment() != null ? s.getCurrentEnrollment() : 0;
            enrollmentCountMap.merge(s.getCourseId(), count, Integer::sum);
        }

        return courseScores.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Long courseId = entry.getKey();
                Course course = courseMap.get(courseId);
                if (course == null) return null;

                CourseRecommendResponse response = new CourseRecommendResponse();
                response.setCourseId(courseId);
                response.setCourseName(course.getName());
                response.setDescription(course.getDescription());
                response.setCategory(course.getCategory());
                response.setCategoryName(LevelDifficultyUtils.getCategoryName(course.getCategory()));
                response.setDifficulty(String.valueOf(course.getDifficulty()));
                response.setDifficultyName(LevelDifficultyUtils.getDifficultyName(course.getDifficulty()));
                response.setDurationHours(course.getDurationHours());
                response.setCoverImage(course.getCoverImage());
                response.setRecommendScore(entry.getValue() * 100);
                response.setRecommendReasons(courseReasons.getOrDefault(courseId, Collections.emptyList()));
                response.setRecommendType(recommendType);
                response.setPredictedRating(entry.getValue() * 5); // 转换为5分制

                // 解析标签
                if (course.getTags() != null && !course.getTags().isEmpty()) {
                    response.setTags(Arrays.asList(course.getTags().split(",")));
                }

                // 获取报名人数（从批量查询结果）
                response.setEnrollmentCount(enrollmentCountMap.getOrDefault(courseId, 0));

                return response;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
