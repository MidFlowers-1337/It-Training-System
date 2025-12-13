package com.itts.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.learning.dto.CourseRecommendResponse;
import com.itts.modules.learning.entity.*;
import com.itts.modules.learning.mapper.*;
import com.itts.modules.learning.service.ContentBasedRecommendService;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于内容的推荐服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentBasedRecommendServiceImpl implements ContentBasedRecommendService {

    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final ClassSessionMapper sessionMapper;
    private final LearningProgressMapper progressMapper;
    private final CourseSimilarityMapper similarityMapper;
    private final UserSkillTagMapper skillTagMapper;
    private final UserPreferenceMapper preferenceMapper;
    private final ObjectMapper objectMapper;

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
    public List<CourseRecommendResponse> recommendByUserHistory(Long userId, int limit) {
        // 获取用户已学习的课程
        List<LearningProgress> progressList = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
        );

        if (progressList.isEmpty()) {
            // 如果没有学习记录，返回热门课程
            return getPopularCourses(userId, limit);
        }

        // 获取用户已学课程ID
        Set<Long> learnedCourseIds = progressList.stream()
            .map(LearningProgress::getCourseId)
            .collect(Collectors.toSet());

        // 获取用户已报名的课程ID
        Set<Long> enrolledCourseIds = getEnrolledCourseIds(userId);

        // 基于已学课程找相似课程
        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();

        for (LearningProgress progress : progressList) {
            Long courseId = progress.getCourseId();
            Course course = courseMapper.selectById(courseId);
            if (course == null) continue;

            // 获取相似课程
            List<CourseSimilarity> similarities = similarityMapper.selectList(
                new LambdaQueryWrapper<CourseSimilarity>()
                    .and(w -> w.eq(CourseSimilarity::getCourseIdA, courseId)
                        .or()
                        .eq(CourseSimilarity::getCourseIdB, courseId))
                    .orderByDesc(CourseSimilarity::getSimilarityScore)
                    .last("LIMIT 20")
            );

            for (CourseSimilarity sim : similarities) {
                Long similarCourseId = sim.getCourseIdA().equals(courseId) ? 
                    sim.getCourseIdB() : sim.getCourseIdA();

                // 排除已学习和已报名的课程
                if (learnedCourseIds.contains(similarCourseId) || 
                    enrolledCourseIds.contains(similarCourseId)) {
                    continue;
                }

                double score = sim.getSimilarityScore().doubleValue();
                // 根据学习进度加权
                double progressWeight = progress.getProgressPercent() / 100.0;
                score *= (0.5 + 0.5 * progressWeight);

                courseScores.merge(similarCourseId, score, Double::sum);
                
                courseReasons.computeIfAbsent(similarCourseId, k -> new ArrayList<>())
                    .add("与您学习的《" + course.getName() + "》相似");
            }
        }

        // 如果相似课程不足，补充同类别课程
        if (courseScores.size() < limit) {
            addCategoryCourses(userId, progressList, courseScores, courseReasons, 
                learnedCourseIds, enrolledCourseIds, limit);
        }

        return buildRecommendResponse(courseScores, courseReasons, "content_based", limit);
    }

    @Override
    public List<CourseRecommendResponse> recommendSimilarCourses(Long courseId, int limit) {
        Course baseCourse = courseMapper.selectById(courseId);
        if (baseCourse == null) {
            return Collections.emptyList();
        }

        // 从相似度表获取
        List<CourseSimilarity> similarities = similarityMapper.selectList(
            new LambdaQueryWrapper<CourseSimilarity>()
                .and(w -> w.eq(CourseSimilarity::getCourseIdA, courseId)
                    .or()
                    .eq(CourseSimilarity::getCourseIdB, courseId))
                .orderByDesc(CourseSimilarity::getSimilarityScore)
                .last("LIMIT " + limit)
        );

        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();

        for (CourseSimilarity sim : similarities) {
            Long similarCourseId = sim.getCourseIdA().equals(courseId) ? 
                sim.getCourseIdB() : sim.getCourseIdA();
            
            courseScores.put(similarCourseId, sim.getSimilarityScore().doubleValue());
            courseReasons.put(similarCourseId, 
                List.of("与《" + baseCourse.getName() + "》内容相似"));
        }

        // 如果相似课程不足，补充同类别课程
        if (courseScores.size() < limit) {
            List<Course> sameCategoryCourses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                    .eq(Course::getCategory, baseCourse.getCategory())
                    .eq(Course::getStatus, 1)
                    .ne(Course::getId, courseId)
                    .notIn(!courseScores.isEmpty(), Course::getId, courseScores.keySet())
                    .last("LIMIT " + (limit - courseScores.size()))
            );

            for (Course course : sameCategoryCourses) {
                double score = 0.5; // 同类别基础分
                if (course.getDifficulty().equals(baseCourse.getDifficulty())) {
                    score += 0.2;
                }
                courseScores.put(course.getId(), score);
                courseReasons.put(course.getId(), 
                    List.of("同属" + CATEGORY_NAMES.getOrDefault(baseCourse.getCategory(), "相同") + "类别"));
            }
        }

        return buildRecommendResponse(courseScores, courseReasons, "content_based", limit);
    }

    @Override
    public List<CourseRecommendResponse> recommendByUserSkills(Long userId, int limit) {
        // 获取用户技能标签
        List<UserSkillTag> skillTags = skillTagMapper.selectList(
            new LambdaQueryWrapper<UserSkillTag>()
                .eq(UserSkillTag::getUserId, userId)
                .orderByDesc(UserSkillTag::getProficiencyLevel)
        );

        if (skillTags.isEmpty()) {
            return recommendByUserHistory(userId, limit);
        }

        Set<Long> enrolledCourseIds = getEnrolledCourseIds(userId);
        Set<Long> learnedCourseIds = getLearnedCourseIds(userId);

        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();

        // 获取所有上架课程
        List<Course> allCourses = courseMapper.selectList(
            new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
        );

        for (Course course : allCourses) {
            if (enrolledCourseIds.contains(course.getId()) || 
                learnedCourseIds.contains(course.getId())) {
                continue;
            }

            String courseTags = course.getTags();
            if (courseTags == null || courseTags.isEmpty()) continue;

            Set<String> courseTagSet = Arrays.stream(courseTags.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

            double score = 0;
            List<String> reasons = new ArrayList<>();

            for (UserSkillTag skillTag : skillTags) {
                String skillName = skillTag.getSkillName().toLowerCase();
                if (courseTagSet.contains(skillName)) {
                    // 根据熟练度推荐不同难度
                    int proficiency = skillTag.getProficiencyLevel();
                    int recommendedDifficulty = proficiency < 30 ? 1 : 
                        proficiency < 60 ? 2 : proficiency < 80 ? 3 : 4;
                    
                    double difficultyMatch = 1.0 - Math.abs(course.getDifficulty() - recommendedDifficulty) * 0.2;
                    score += difficultyMatch * (proficiency / 100.0);
                    reasons.add("匹配您的技能「" + skillTag.getSkillName() + "」");
                }
            }

            if (score > 0) {
                courseScores.put(course.getId(), score);
                courseReasons.put(course.getId(), reasons);
            }
        }

        return buildRecommendResponse(courseScores, courseReasons, "content_based", limit);
    }

    @Override
    public List<CourseRecommendResponse> recommendByUserPreference(Long userId, int limit) {
        UserPreference preference = preferenceMapper.selectOne(
            new LambdaQueryWrapper<UserPreference>()
                .eq(UserPreference::getUserId, userId)
        );

        if (preference == null) {
            return recommendByUserHistory(userId, limit);
        }

        Set<Long> enrolledCourseIds = getEnrolledCourseIds(userId);
        Set<Long> learnedCourseIds = getLearnedCourseIds(userId);

        // 解析偏好类别
        List<String> preferredCategories = new ArrayList<>();
        if (preference.getPreferredCategories() != null) {
            try {
                preferredCategories = objectMapper.readValue(
                    preference.getPreferredCategories(),
                    new TypeReference<List<String>>() {}
                );
            } catch (Exception e) {
                log.warn("解析偏好类别失败", e);
            }
        }

        Integer preferredDifficulty = preference.getPreferredDifficulty();

        // 构建查询条件
        LambdaQueryWrapper<Course> query = new LambdaQueryWrapper<Course>()
            .eq(Course::getStatus, 1);

        if (!preferredCategories.isEmpty()) {
            query.in(Course::getCategory, preferredCategories);
        }

        List<Course> courses = courseMapper.selectList(query);

        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();

        for (Course course : courses) {
            if (enrolledCourseIds.contains(course.getId()) || 
                learnedCourseIds.contains(course.getId())) {
                continue;
            }

            double score = 0.5; // 基础分
            List<String> reasons = new ArrayList<>();

            // 类别匹配
            if (preferredCategories.contains(course.getCategory())) {
                score += 0.3;
                reasons.add("符合您偏好的" + CATEGORY_NAMES.getOrDefault(course.getCategory(), "课程") + "类别");
            }

            // 难度匹配
            if (preferredDifficulty != null) {
                int diffDiff = Math.abs(course.getDifficulty() - preferredDifficulty);
                if (diffDiff == 0) {
                    score += 0.2;
                    reasons.add("难度适合您的水平");
                } else if (diffDiff == 1) {
                    score += 0.1;
                }
            }

            if (!reasons.isEmpty()) {
                courseScores.put(course.getId(), score);
                courseReasons.put(course.getId(), reasons);
            }
        }

        return buildRecommendResponse(courseScores, courseReasons, "content_based", limit);
    }

    @Override
    public double calculateCourseSimilarity(Long courseId1, Long courseId2) {
        Course course1 = courseMapper.selectById(courseId1);
        Course course2 = courseMapper.selectById(courseId2);

        if (course1 == null || course2 == null) {
            return 0;
        }

        double similarity = 0;

        // 类别相似度 (权重 0.4)
        if (course1.getCategory().equals(course2.getCategory())) {
            similarity += 0.4;
        }

        // 难度相似度 (权重 0.2)
        int diffDiff = Math.abs(course1.getDifficulty() - course2.getDifficulty());
        similarity += 0.2 * (1 - diffDiff / 3.0);

        // 标签相似度 (权重 0.4)
        Set<String> tags1 = parseTags(course1.getTags());
        Set<String> tags2 = parseTags(course2.getTags());
        
        if (!tags1.isEmpty() && !tags2.isEmpty()) {
            Set<String> intersection = new HashSet<>(tags1);
            intersection.retainAll(tags2);
            Set<String> union = new HashSet<>(tags1);
            union.addAll(tags2);
            
            double jaccardSimilarity = (double) intersection.size() / union.size();
            similarity += 0.4 * jaccardSimilarity;
        }

        return similarity;
    }

    @Override
    @Transactional
    public void updateCourseSimilarityMatrix() {
        log.info("开始更新课程相似度矩阵");

        // 获取所有上架课程
        List<Course> courses = courseMapper.selectList(
            new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
        );

        int count = 0;
        for (int i = 0; i < courses.size(); i++) {
            for (int j = i + 1; j < courses.size(); j++) {
                Course course1 = courses.get(i);
                Course course2 = courses.get(j);

                double similarity = calculateCourseSimilarity(course1.getId(), course2.getId());

                // 只保存相似度大于阈值的记录
                if (similarity > 0.3) {
                    // 检查是否已存在
                    CourseSimilarity existing = similarityMapper.selectOne(
                        new LambdaQueryWrapper<CourseSimilarity>()
                            .and(w -> w
                                .and(w2 -> w2.eq(CourseSimilarity::getCourseIdA, course1.getId())
                                    .eq(CourseSimilarity::getCourseIdB, course2.getId()))
                                .or(w2 -> w2.eq(CourseSimilarity::getCourseIdA, course2.getId())
                                    .eq(CourseSimilarity::getCourseIdB, course1.getId()))
                            )
                    );

                    if (existing != null) {
                        existing.setSimilarityScore(BigDecimal.valueOf(similarity).setScale(4, RoundingMode.HALF_UP));
                        similarityMapper.updateById(existing);
                    } else {
                        CourseSimilarity newSim = new CourseSimilarity();
                        newSim.setCourseIdA(course1.getId());
                        newSim.setCourseIdB(course2.getId());
                        newSim.setSimilarityScore(BigDecimal.valueOf(similarity).setScale(4, RoundingMode.HALF_UP));
                        similarityMapper.insert(newSim);
                    }
                    count++;
                }
            }
        }

        log.info("课程相似度矩阵更新完成，共更新 {} 条记录", count);
    }

    // ==================== 辅助方法 ====================

    private Set<Long> getEnrolledCourseIds(Long userId) {
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

    private Set<Long> getLearnedCourseIds(Long userId) {
        List<LearningProgress> progressList = progressMapper.selectList(
            new LambdaQueryWrapper<LearningProgress>()
                .eq(LearningProgress::getUserId, userId)
        );
        return progressList.stream()
            .map(LearningProgress::getCourseId)
            .collect(Collectors.toSet());
    }

    private Set<String> parseTags(String tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(tags.split(","))
            .map(String::trim)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    private List<CourseRecommendResponse> getPopularCourses(Long userId, int limit) {
        Set<Long> enrolledCourseIds = getEnrolledCourseIds(userId);

        // 获取报名人数最多的课程
        List<Course> courses = courseMapper.selectList(
            new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
                .notIn(!enrolledCourseIds.isEmpty(), Course::getId, enrolledCourseIds)
                .last("LIMIT " + limit)
        );

        Map<Long, Double> courseScores = new HashMap<>();
        Map<Long, List<String>> courseReasons = new HashMap<>();

        for (Course course : courses) {
            courseScores.put(course.getId(), 0.5);
            courseReasons.put(course.getId(), List.of("热门推荐课程"));
        }

        return buildRecommendResponse(courseScores, courseReasons, "popular", limit);
    }

    private void addCategoryCourses(Long userId, List<LearningProgress> progressList,
                                    Map<Long, Double> courseScores, Map<Long, List<String>> courseReasons,
                                    Set<Long> learnedCourseIds, Set<Long> enrolledCourseIds, int limit) {
        // 统计用户学习的类别
        Map<String, Integer> categoryCount = new HashMap<>();
        for (LearningProgress progress : progressList) {
            Course course = courseMapper.selectById(progress.getCourseId());
            if (course != null) {
                categoryCount.merge(course.getCategory(), 1, Integer::sum);
            }
        }

        // 按学习次数排序类别
        List<String> topCategories = categoryCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .limit(3)
            .collect(Collectors.toList());

        for (String category : topCategories) {
            if (courseScores.size() >= limit) break;

            List<Course> categoryCourses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                    .eq(Course::getCategory, category)
                    .eq(Course::getStatus, 1)
                    .notIn(!learnedCourseIds.isEmpty(), Course::getId, learnedCourseIds)
                    .notIn(!enrolledCourseIds.isEmpty(), Course::getId, enrolledCourseIds)
                    .notIn(!courseScores.isEmpty(), Course::getId, courseScores.keySet())
                    .last("LIMIT " + (limit - courseScores.size()))
            );

            for (Course course : categoryCourses) {
                courseScores.put(course.getId(), 0.4);
                courseReasons.put(course.getId(), 
                    List.of("您经常学习" + CATEGORY_NAMES.getOrDefault(category, "该") + "类课程"));
            }
        }
    }

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
                response.setSimilarityScore(entry.getValue());
                
                // 解析标签
                if (course.getTags() != null && !course.getTags().isEmpty()) {
                    response.setTags(Arrays.asList(course.getTags().split(",")));
                }

                // 获取报名人数（通过班期统计）
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