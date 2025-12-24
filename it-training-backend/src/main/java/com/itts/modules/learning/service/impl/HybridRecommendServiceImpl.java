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
import com.itts.modules.learning.service.ContentBasedRecommendService;
import com.itts.modules.learning.service.HybridRecommendService;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 混合推荐服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HybridRecommendServiceImpl implements HybridRecommendService {

    private final ContentBasedRecommendService contentBasedService;
    private final CollaborativeFilteringService collaborativeService;
    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final ClassSessionMapper sessionMapper;
    private final LearningProgressMapper progressMapper;

    // 推荐权重配置
    private static final double CONTENT_BASED_WEIGHT = 0.4;
    private static final double COLLABORATIVE_USER_WEIGHT = 0.3;
    private static final double COLLABORATIVE_ITEM_WEIGHT = 0.3;

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
    public List<CourseRecommendResponse> getHybridRecommendations(Long userId, int limit) {
        // 获取各种推荐结果
        List<CourseRecommendResponse> contentBased = contentBasedService.recommendByUserHistory(userId, limit * 2);
        List<CourseRecommendResponse> userBased = collaborativeService.recommendByUserBased(userId, limit * 2);
        List<CourseRecommendResponse> itemBased = collaborativeService.recommendByItemBased(userId, limit * 2);

        // 合并推荐结果
        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();
        Map<Long, CourseRecommendResponse> courseMap = new HashMap<>();

        // 处理基于内容的推荐
        for (CourseRecommendResponse rec : contentBased) {
            Long courseId = rec.getCourseId();
            double score = (rec.getRecommendScore() / 100) * CONTENT_BASED_WEIGHT;
            courseScores.merge(courseId, score, Double::sum);
            courseReasons.computeIfAbsent(courseId, k -> new ArrayList<>())
                .addAll(rec.getRecommendReasons());
            courseMap.put(courseId, rec);
        }

        // 处理基于用户的协同过滤
        for (CourseRecommendResponse rec : userBased) {
            Long courseId = rec.getCourseId();
            double score = (rec.getRecommendScore() / 100) * COLLABORATIVE_USER_WEIGHT;
            courseScores.merge(courseId, score, Double::sum);
            courseReasons.computeIfAbsent(courseId, k -> new ArrayList<>())
                .addAll(rec.getRecommendReasons());
            if (!courseMap.containsKey(courseId)) {
                courseMap.put(courseId, rec);
            }
        }

        // 处理基于物品的协同过滤
        for (CourseRecommendResponse rec : itemBased) {
            Long courseId = rec.getCourseId();
            double score = (rec.getRecommendScore() / 100) * COLLABORATIVE_ITEM_WEIGHT;
            courseScores.merge(courseId, score, Double::sum);
            courseReasons.computeIfAbsent(courseId, k -> new ArrayList<>())
                .addAll(rec.getRecommendReasons());
            if (!courseMap.containsKey(courseId)) {
                courseMap.put(courseId, rec);
            }
        }

        // 排序并返回
        return courseScores.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Long courseId = entry.getKey();
                CourseRecommendResponse rec = courseMap.get(courseId);
                if (rec != null) {
                    rec.setRecommendScore(entry.getValue() * 100);
                    rec.setRecommendType("hybrid");
                    // 去重推荐理由
                    List<String> uniqueReasons = courseReasons.get(courseId).stream()
                        .distinct()
                        .limit(3)
                        .collect(Collectors.toList());
                    rec.setRecommendReasons(uniqueReasons);
                }
                return rec;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public List<CourseRecommendResponse> getHomePageRecommendations(Long userId) {
        List<CourseRecommendResponse> recommendations = new ArrayList<>();

        // 获取混合推荐 (6个)
        List<CourseRecommendResponse> hybrid = getHybridRecommendations(userId, 6);
        recommendations.addAll(hybrid);

        // 获取热门课程 (3个)
        List<CourseRecommendResponse> popular = getPopularCourses(3);
        for (CourseRecommendResponse rec : popular) {
            if (recommendations.stream().noneMatch(r -> r.getCourseId().equals(rec.getCourseId()))) {
                recommendations.add(rec);
            }
        }

        // 获取新课程 (3个)
        List<CourseRecommendResponse> newCourses = getNewCourses(3);
        for (CourseRecommendResponse rec : newCourses) {
            if (recommendations.stream().noneMatch(r -> r.getCourseId().equals(rec.getCourseId()))) {
                recommendations.add(rec);
            }
        }

        return recommendations.stream().limit(12).collect(Collectors.toList());
    }

    @Override
    public List<CourseRecommendResponse> getYouMayLike(Long userId, int limit) {
        // 结合用户偏好和协同过滤
        List<CourseRecommendResponse> preferenceRecs = contentBasedService.recommendByUserPreference(userId, limit);
        List<CourseRecommendResponse> collaborativeRecs = collaborativeService.recommendByUserBased(userId, limit);

        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, CourseRecommendResponse> courseMap = new HashMap<>();

        for (CourseRecommendResponse rec : preferenceRecs) {
            courseScores.merge(rec.getCourseId(), rec.getRecommendScore() * 0.5, Double::sum);
            courseMap.put(rec.getCourseId(), rec);
        }

        for (CourseRecommendResponse rec : collaborativeRecs) {
            courseScores.merge(rec.getCourseId(), rec.getRecommendScore() * 0.5, Double::sum);
            if (!courseMap.containsKey(rec.getCourseId())) {
                courseMap.put(rec.getCourseId(), rec);
            }
        }

        return courseScores.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                CourseRecommendResponse rec = courseMap.get(entry.getKey());
                if (rec != null) {
                    rec.setRecommendScore(entry.getValue());
                    rec.setRecommendType("you_may_like");
                    rec.setRecommendReasons(List.of("猜你喜欢"));
                }
                return rec;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public List<CourseRecommendResponse> getPopularCourses(int limit) {
        // 获取所有上架课程
        List<Course> courses = courseMapper.selectList(
            new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
        );

        // 计算每个课程的热度分数
        Map<Long, Integer> enrollmentCounts = new HashMap<>();
        for (Course course : courses) {
            List<ClassSession> sessions = sessionMapper.selectList(
                new LambdaQueryWrapper<ClassSession>()
                    .eq(ClassSession::getCourseId, course.getId())
            );
            int count = sessions.stream()
                .mapToInt(s -> s.getCurrentEnrollment() != null ? s.getCurrentEnrollment() : 0)
                .sum();
            enrollmentCounts.put(course.getId(), count);
        }

        // 按报名人数排序
        return courses.stream()
            .sorted((c1, c2) -> enrollmentCounts.getOrDefault(c2.getId(), 0)
                .compareTo(enrollmentCounts.getOrDefault(c1.getId(), 0)))
            .limit(limit)
            .map(course -> buildCourseResponse(course, "popular", 
                List.of("热门课程，" + enrollmentCounts.get(course.getId()) + "人在学"),
                enrollmentCounts.get(course.getId())))
            .collect(Collectors.toList());
    }

    @Override
    public List<CourseRecommendResponse> getNewCourses(int limit) {
        // 获取最新上架的课程
        List<Course> courses = courseMapper.selectList(
            new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
                .orderByDesc(Course::getCreatedAt)
                .last("LIMIT " + limit)
        );

        return courses.stream()
            .map(course -> {
                // 获取报名人数
                List<ClassSession> sessions = sessionMapper.selectList(
                    new LambdaQueryWrapper<ClassSession>()
                        .eq(ClassSession::getCourseId, course.getId())
                );
                int count = sessions.stream()
                    .mapToInt(s -> s.getCurrentEnrollment() != null ? s.getCurrentEnrollment() : 0)
                    .sum();
                return buildCourseResponse(course, "new", List.of("新上架课程"), count);
            })
            .collect(Collectors.toList());
    }

    /**
     * 构建课程推荐响应
     */
    private CourseRecommendResponse buildCourseResponse(Course course, String recommendType, 
                                                        List<String> reasons, int enrollmentCount) {
        CourseRecommendResponse response = new CourseRecommendResponse();
        response.setCourseId(course.getId());
        response.setCourseName(course.getName());
        response.setDescription(course.getDescription());
        response.setCategory(course.getCategory());
        response.setCategoryName(CATEGORY_NAMES.getOrDefault(course.getCategory(), course.getCategory()));
        response.setDifficulty(String.valueOf(course.getDifficulty()));
        response.setDifficultyName(DIFFICULTY_NAMES.getOrDefault(course.getDifficulty(), "未知"));
        response.setDurationHours(course.getDurationHours());
        response.setCoverImage(course.getCoverImage());
        response.setRecommendType(recommendType);
        response.setRecommendReasons(reasons);
        response.setEnrollmentCount(enrollmentCount);
        
        // 根据报名人数计算推荐分数
        response.setRecommendScore(Math.min(100, enrollmentCount * 2.0));

        // 解析标签
        if (course.getTags() != null && !course.getTags().isEmpty()) {
            response.setTags(Arrays.asList(course.getTags().split(",")));
        }

        return response;
    }
}