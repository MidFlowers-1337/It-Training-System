package com.itts.modules.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.modules.ai.dto.AiRecommendResponse;
import com.itts.modules.ai.dto.AiRecommendResponse.RecommendedCourse;
import com.itts.modules.ai.entity.AiRecommendLog;
import com.itts.modules.ai.mapper.AiRecommendLogMapper;
import com.itts.modules.ai.service.AiRecommendService;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.common.util.SecurityUtils;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI推荐服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiRecommendServiceImpl implements AiRecommendService {

    /**
     * 最大推荐课程数量
     */
    private static final int MAX_RECOMMENDED_COURSES = 5;

    private final ChatClient.Builder chatClientBuilder;
    private final CourseMapper courseMapper;
    private final SysUserMapper sysUserMapper;
    private final AiRecommendLogMapper aiRecommendLogMapper;
    private final ObjectMapper objectMapper;

    @Value("${ai.recommend.enabled:true}")
    private boolean aiEnabled;

    @Value("${ai.recommend.timeout:10000}")
    private int timeout;

    @Value("${ai.recommend.fallback-on-error:true}")
    private boolean fallbackOnError;

    @Override
    public AiRecommendResponse getRecommendation(String learningGoal) {
        long startTime = System.currentTimeMillis();
        Long userId = getCurrentUserId();

        log.info("开始AI推荐, userId={}, learningGoal={}", userId, learningGoal);

        // 获取所有上架课程
        List<Course> availableCourses = courseMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Course>()
                        .eq(Course::getStatus, 1)
                        .eq(Course::getDeleted, 0)
        );

        if (availableCourses.isEmpty()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND, "暂无可推荐的课程");
        }

        AiRecommendResponse response;
        String modelUsed;

        if (aiEnabled) {
            try {
                response = callAiForRecommendation(learningGoal, availableCourses);
                modelUsed = "deepseek-chat";
                log.info("AI推荐成功");
            } catch (Exception e) {
                log.error("AI推荐失败，使用降级策略: {}", e.getMessage());
                if (fallbackOnError) {
                    response = getFallbackRecommendation(learningGoal, availableCourses);
                    modelUsed = "FALLBACK";
                } else {
                    throw new BusinessException(ErrorCode.AI_SERVICE_ERROR);
                }
            }
        } else {
            log.info("AI未启用，使用降级策略");
            response = getFallbackRecommendation(learningGoal, availableCourses);
            modelUsed = "FALLBACK";
        }

        // 记录推荐日志
        long responseTime = System.currentTimeMillis() - startTime;
        saveRecommendLog(userId, learningGoal, response, modelUsed, (int) responseTime);

        return response;
    }

    /**
     * 调用AI获取推荐
     */
    private AiRecommendResponse callAiForRecommendation(String learningGoal, List<Course> courses) {
        // 构建课程信息
        StringBuilder courseInfo = new StringBuilder();
        for (Course course : courses) {
            courseInfo.append(String.format("- %s (编码: %s, 分类: %s, 难度: %d, 标签: %s)\n",
                    course.getName(), course.getCode(), course.getCategory(),
                    course.getDifficulty(), course.getTags()));
        }

        String systemPrompt = """
                你是一个IT技能培训课程推荐助手。根据用户的学习目标，从提供的课程列表中推荐最适合的课程。

                请按以下JSON格式返回推荐结果（不要包含markdown代码块标记）：
                {
                    "recommendedCourseIds": ["课程编码1", "课程编码2", ...],
                    "reasons": {
                        "课程编码1": "推荐理由",
                        "课程编码2": "推荐理由"
                    },
                    "overallReason": "整体推荐理由",
                    "learningPath": "建议的学习顺序说明"
                }

                要求：
                1. 推荐3-5门最相关的课程
                2. 按照建议的学习顺序排列
                3. 每门课程给出具体的推荐理由
                4. 考虑课程难度递进关系
                """;

        String userPrompt = String.format("""
                用户学习目标：%s

                可选课程列表：
                %s

                请推荐最适合的课程。
                """, learningGoal, courseInfo);

        ChatClient chatClient = chatClientBuilder.build();
        String aiResponse = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();

        log.debug("AI响应: {}", aiResponse);

        return parseAiResponse(aiResponse, courses);
    }

    /**
     * 解析AI响应
     */
    private AiRecommendResponse parseAiResponse(String aiResponse, List<Course> courses) {
        AiRecommendResponse response = new AiRecommendResponse();
        response.setFallback(false);
        response.setCourses(new ArrayList<>());

        try {
            // 清理响应中可能的markdown标记
            String cleanResponse = aiResponse
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            // 解析JSON
            var jsonNode = objectMapper.readTree(cleanResponse);

            // 获取推荐的课程编码
            var courseIds = jsonNode.get("recommendedCourseIds");
            var reasons = jsonNode.get("reasons");

            if (courseIds != null && courseIds.isArray()) {
                int order = 1;
                for (var idNode : courseIds) {
                    String courseCode = idNode.asText();
                    Course course = courses.stream()
                            .filter(c -> c.getCode().equals(courseCode))
                            .findFirst()
                            .orElse(null);

                    if (course != null) {
                        RecommendedCourse rc = new RecommendedCourse();
                        rc.setCourseId(course.getId());
                        rc.setCourseCode(course.getCode());
                        rc.setCourseName(course.getName());
                        rc.setCategory(course.getCategory());
                        rc.setDifficulty(course.getDifficulty());
                        rc.setDifficultyName(getDifficultyName(course.getDifficulty()));
                        rc.setTags(course.getTags());
                        rc.setOrder(order++);

                        if (reasons != null && reasons.has(courseCode)) {
                            rc.setReason(reasons.get(courseCode).asText());
                        }

                        response.getCourses().add(rc);
                    }
                }
            }

            if (jsonNode.has("overallReason")) {
                response.setOverallReason(jsonNode.get("overallReason").asText());
            }
            if (jsonNode.has("learningPath")) {
                response.setLearningPath(jsonNode.get("learningPath").asText());
            }

        } catch (Exception e) {
            log.error("解析AI响应失败: {}", e.getMessage());
            // 解析失败时返回基于关键词匹配的降级结果
            return getFallbackRecommendation("", courses);
        }

        // 如果没有解析到课程，返回降级结果
        if (response.getCourses().isEmpty()) {
            return getFallbackRecommendation("", courses);
        }

        return response;
    }

    /**
     * 降级推荐策略：基于关键词匹配和热门课程
     */
    private AiRecommendResponse getFallbackRecommendation(String learningGoal, List<Course> courses) {
        AiRecommendResponse response = new AiRecommendResponse();
        response.setFallback(true);
        response.setFallbackMessage("AI服务暂时不可用，为您推荐热门课程");
        response.setCourses(new ArrayList<>());

        String goal = learningGoal.toLowerCase();

        // 基于关键词匹配分类
        String targetCategory = null;
        if (goal.contains("后端") || goal.contains("java") || goal.contains("spring")) {
            targetCategory = "BACKEND";
        } else if (goal.contains("前端") || goal.contains("vue") || goal.contains("react") || goal.contains("javascript")) {
            targetCategory = "FRONTEND";
        } else if (goal.contains("数据库") || goal.contains("mysql") || goal.contains("sql")) {
            targetCategory = "DATABASE";
        } else if (goal.contains("ai") || goal.contains("人工智能") || goal.contains("机器学习")) {
            targetCategory = "AI";
        } else if (goal.contains("云") || goal.contains("docker") || goal.contains("kubernetes")) {
            targetCategory = "CLOUD";
        }

        // 筛选课程
        List<Course> filteredCourses;
        if (targetCategory != null) {
            String finalCategory = targetCategory;
            filteredCourses = courses.stream()
                    .filter(c -> c.getCategory().equals(finalCategory))
                    .sorted(Comparator.comparingInt(Course::getDifficulty))
                    .collect(Collectors.toList());
        } else {
            // 无法识别分类时，按难度排序返回热门课程
            filteredCourses = courses.stream()
                    .sorted(Comparator.comparingInt(Course::getDifficulty))
                    .limit(MAX_RECOMMENDED_COURSES)
                    .collect(Collectors.toList());
        }

        // 构建推荐结果
        int order = 1;
        for (Course course : filteredCourses) {
            if (order > MAX_RECOMMENDED_COURSES) break;

            RecommendedCourse rc = new RecommendedCourse();
            rc.setCourseId(course.getId());
            rc.setCourseCode(course.getCode());
            rc.setCourseName(course.getName());
            rc.setCategory(course.getCategory());
            rc.setDifficulty(course.getDifficulty());
            rc.setDifficultyName(getDifficultyName(course.getDifficulty()));
            rc.setTags(course.getTags());
            rc.setOrder(order++);
            rc.setReason("基于您的学习目标推荐的热门课程");

            response.getCourses().add(rc);
        }

        response.setOverallReason("根据您的学习目标，为您推荐以下热门课程");
        response.setLearningPath("建议按照课程难度从低到高依次学习");

        return response;
    }

    /**
     * 保存推荐日志
     */
    private void saveRecommendLog(Long userId, String inputText, AiRecommendResponse response,
                                  String modelUsed, int responseTimeMs) {
        try {
            AiRecommendLog log = new AiRecommendLog();
            log.setUserId(userId);
            log.setInputText(inputText);

            // 序列化推荐课程ID列表
            List<Long> courseIds = response.getCourses().stream()
                    .map(RecommendedCourse::getCourseId)
                    .collect(Collectors.toList());
            log.setRecommendedCourses(objectMapper.writeValueAsString(courseIds));

            log.setReason(response.getOverallReason());
            log.setModelUsed(modelUsed);
            log.setResponseTimeMs(responseTimeMs);
            log.setCreatedAt(LocalDateTime.now());

            aiRecommendLogMapper.insert(log);
        } catch (JsonProcessingException e) {
            // 日志记录失败不影响主流程
            AiRecommendServiceImpl.log.error("保存推荐日志失败: {}", e.getMessage());
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        String username = SecurityUtils.getCurrentUsername();
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user.getId();
    }

    /**
     * 获取难度名称
     */
    private String getDifficultyName(Integer difficulty) {
        return switch (difficulty) {
            case 1 -> "入门";
            case 2 -> "初级";
            case 3 -> "中级";
            case 4 -> "高级";
            default -> "未知";
        };
    }
}
