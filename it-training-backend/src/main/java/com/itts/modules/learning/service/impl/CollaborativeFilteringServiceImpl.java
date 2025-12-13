package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.learning.dto.CourseRecommendResponse;
import com.itts.modules.learning.entity.LearningProgress;
import com.itts.modules.learning.mapper.LearningProgressMapper;
import com.itts.modules.learning.service.CollaborativeFilteringService;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 协同过滤推荐服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollaborativeFilteringServiceImpl implements CollaborativeFilteringService {

    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final ClassSessionMapper sessionMapper;
    private final LearningProgressMapper progressMapper;

    // 类别名称映射
    private static final Map<String, String> CATEGORY_NAMES = Map.of(
        "BACKEND", "后端开发",
        "FRONTEND", "前端开发",
        "DATABASE", "数据库",
        "CLOUD", "云计算",
        "AI", "人工智能",
        "OTHER", "其他"
    );

    // 难度名称映射
    private static final Map<Integer, String> DIFFICULTY_NAMES = Map.of(
        1, "入门",
        2, "初级",
        3, "中级",
        4, "高级"
    );

    @Override
    public List<CourseRecommendResponse> recommendByUserBased(Long userId, int limit) {
        // 获取当前用户学习的课程
        Set<Long> userCourseIds = getUserCourseIds(userId);
        
        if (userCourseIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 找到相似用户
        List<Long> similarUsers = getSimilarUsers(userId, 20);
        
        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }

        // 统计相似用户学习的课程
        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();

        for (Long similarUserId : similarUsers) {
            double similarity = calculateUserSimilarity(userId, similarUserId);
            Set<Long> similarUserCourses = getUserCourseIds(similarUserId);
            
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

        // 找到经常与用户课程一起被学习的其他课程
        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();

        for (Long courseId : userCourseIds) {
            Course course = courseMapper.selectById(courseId);
            if (course == null) continue;

            // 找到学习过这门课的其他用户
            List<Long> usersWhoLearnedCourse = getUsersWhoLearnedCourse(courseId);
            
            // 统计这些用户还学习了哪些课程
            Map<Long, Integer> coOccurrence = new HashMap<>();
            for (Long otherUserId : usersWhoLearnedCourse) {
                if (otherUserId.equals(userId)) continue;
                
                Set<Long> otherUserCourses = getUserCourseIds(otherUserId);
                for (Long otherCourseId : otherUserCourses) {
                    if (!userCourseIds.contains(otherCourseId)) {
                        coOccurrence.merge(otherCourseId, 1, Integer::sum);
                    }
                }
            }

            // 计算共现分数
            int totalUsers = usersWhoLearnedCourse.size();
            for (Map.Entry<Long, Integer> entry : coOccurrence.entrySet()) {
                Long otherCourseId = entry.getKey();
                double score = (double) entry.getValue() / totalUsers;
                
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

        if (courses1.isEmpty() || courses2.isEmpty()) {
            return 0;
        }

        // 使用 Jaccard 相似度
        Set<Long> intersection = new HashSet<>(courses1);
        intersection.retainAll(courses2);

        Set<Long> union = new HashSet<>(courses1);
        union.addAll(courses2);

        return (double) intersection.size() / union.size();
    }

    @Override
    public List<Long> getSimilarUsers(Long userId, int limit) {
        Set<Long> userCourseIds = getUserCourseIds(userId);
        
        if (userCourseIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取所有学习过相同课程的用户
        Set<Long> candidateUsers = new HashSet<>();
        for (Long courseId : userCourseIds) {
            candidateUsers.addAll(getUsersWhoLearnedCourse(courseId));
        }
        candidateUsers.remove(userId);

        // 计算相似度并排序
        return candidateUsers.stream()
            .map(otherUserId -> new AbstractMap.SimpleEntry<>(
                otherUserId, 
                calculateUserSimilarity(userId, otherUserId)
            ))
            .filter(entry -> entry.getValue() > 0.1) // 过滤相似度太低的用户
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取用户学习的课程ID集合
     */
    private Set<Long> getUserCourseIds(Long userId) {
        // 从学习进度获取
        List<LearningProgress> progressList = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
        );
        
        Set<Long> courseIds = progressList.stream()
            .map(LearningProgress::getCourseId)
            .collect(Collectors.toSet());

        // 从报名记录获取
        List<Enrollment> enrollments = enrollmentMapper.selectList(
            new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getUserId, userId)
                .eq(Enrollment::getStatus, 0)
        );
        
        for (Enrollment enrollment : enrollments) {
            ClassSession session = sessionMapper.selectById(enrollment.getSessionId());
            if (session != null) {
                courseIds.add(session.getCourseId());
            }
        }

        return courseIds;
    }

    /**
     * 获取学习过某课程的用户列表
     */
    private List<Long> getUsersWhoLearnedCourse(Long courseId) {
        // 从学习进度获取
        List<LearningProgress> progressList = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getCourseId, courseId)
        );
        
        Set<Long> userIds = progressList.stream()
            .map(LearningProgress::getUserId)
            .collect(Collectors.toSet());

        // 从报名记录获取
        List<ClassSession> sessions = sessionMapper.selectList(
            new LambdaQueryWrapper<ClassSession>()
                .eq(ClassSession::getCourseId, courseId)
        );
        
        for (ClassSession session : sessions) {
            List<Enrollment> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<Enrollment>()
                    .eq(Enrollment::getSessionId, session.getId())
                    .eq(Enrollment::getStatus, 0)
            );
            for (Enrollment enrollment : enrollments) {
                userIds.add(enrollment.getUserId());
            }
        }

        return new ArrayList<>(userIds);
    }

    /**
     * 构建推荐响应
     */
    private List<CourseRecommendResponse> buildRecommendResponse(
            Map<Long, Double> courseScores,
            Map<Long, List<String>> courseReasons,
            String recommendType,
            int limit) {

        return courseScores.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Long courseId = entry.getKey();
                Course course = courseMapper.selectById(courseId);
                if (course == null) return null;

                CourseRecommendResponse response = new CourseRecommendResponse();
                response.setCourseId(courseId);
                response.setCourseName(course.getName());
                response.setDescription(course.getDescription());
                response.setCategory(course.getCategory());
                response.setCategoryName(CATEGORY_NAMES.getOrDefault(course.getCategory(), course.getCategory()));
                response.setDifficulty(String.valueOf(course.getDifficulty()));
                response.setDifficultyName(DIFFICULTY_NAMES.getOrDefault(course.getDifficulty(), "未知"));
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

                // 获取报名人数
                List<ClassSession> sessions = sessionMapper.selectList(
                    new LambdaQueryWrapper<ClassSession>()
                        .eq(ClassSession::getCourseId, courseId)
                );
                int enrollmentCount = sessions.stream()
                    .mapToInt(s -> s.getCurrentEnrollment() != null ? s.getCurrentEnrollment() : 0)
                    .sum();
                response.setEnrollmentCount(enrollmentCount);

                return response;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}