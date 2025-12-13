package com.itts.modules.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.BusinessException;
import com.itts.modules.ai.dto.AiRecommendResponse;
import com.itts.modules.ai.mapper.AiRecommendLogMapper;
import com.itts.modules.ai.service.impl.AiRecommendServiceImpl;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AI推荐服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AI推荐服务测试")
class AiRecommendServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private AiRecommendLogMapper aiRecommendLogMapper;

    private AiRecommendServiceImpl aiRecommendService;

    private SysUser testUser;
    private List<Course> testCourses;

    @BeforeEach
    void setUp() {
        // 创建 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // 手动创建服务实例
        aiRecommendService = new AiRecommendServiceImpl(
                chatClientBuilder,
                courseMapper,
                sysUserMapper,
                aiRecommendLogMapper,
                objectMapper
        );

        // 设置配置属性（禁用AI，使用降级策略）
        ReflectionTestUtils.setField(aiRecommendService, "aiEnabled", false);
        ReflectionTestUtils.setField(aiRecommendService, "timeout", 10000);
        ReflectionTestUtils.setField(aiRecommendService, "fallbackOnError", true);

        // 初始化测试用户
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("student1");
        testUser.setRealName("王小明");

        // 初始化测试课程
        Course course1 = new Course();
        course1.setId(1L);
        course1.setCode("JAVA-001");
        course1.setName("Java 基础入门");
        course1.setCategory("BACKEND");
        course1.setDifficulty(1);
        course1.setTags("Java,入门");
        course1.setStatus(1);
        course1.setDeleted(0);

        Course course2 = new Course();
        course2.setId(2L);
        course2.setCode("JAVA-002");
        course2.setName("Spring Boot 实战");
        course2.setCategory("BACKEND");
        course2.setDifficulty(3);
        course2.setTags("Java,Spring");
        course2.setStatus(1);
        course2.setDeleted(0);

        Course course3 = new Course();
        course3.setId(3L);
        course3.setCode("VUE-001");
        course3.setName("Vue 3 前端开发");
        course3.setCategory("FRONTEND");
        course3.setDifficulty(2);
        course3.setTags("Vue,前端");
        course3.setStatus(1);
        course3.setDeleted(0);

        testCourses = List.of(course1, course2, course3);
    }

    @Test
    @DisplayName("降级推荐 - 后端开发关键词匹配")
    void getRecommendation_Fallback_Backend() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(courseMapper.selectList(any())).thenReturn(testCourses);
            when(aiRecommendLogMapper.insert(any())).thenReturn(1);

            // 执行测试 - 后端开发关键词
            AiRecommendResponse result = aiRecommendService.getRecommendation("我想学习Java后端开发");

            // 验证结果
            assertNotNull(result);
            assertTrue(result.isFallback()); // 应该是降级结果
            assertNotNull(result.getCourses());
            assertFalse(result.getCourses().isEmpty());

            // 验证推荐的是后端课程
            assertTrue(result.getCourses().stream()
                    .allMatch(c -> "BACKEND".equals(c.getCategory())));

            // 验证日志记录
            verify(aiRecommendLogMapper).insert(any());
        }
    }

    @Test
    @DisplayName("降级推荐 - 前端开发关键词匹配")
    void getRecommendation_Fallback_Frontend() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(courseMapper.selectList(any())).thenReturn(testCourses);
            when(aiRecommendLogMapper.insert(any())).thenReturn(1);

            // 执行测试 - 前端开发关键词
            AiRecommendResponse result = aiRecommendService.getRecommendation("我想学习Vue前端开发");

            // 验证结果
            assertNotNull(result);
            assertTrue(result.isFallback());
            assertNotNull(result.getCourses());

            // 验证推荐的是前端课程
            assertTrue(result.getCourses().stream()
                    .allMatch(c -> "FRONTEND".equals(c.getCategory())));
        }
    }

    @Test
    @DisplayName("降级推荐 - 无关键词匹配返回热门课程")
    void getRecommendation_Fallback_NoKeyword() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(courseMapper.selectList(any())).thenReturn(testCourses);
            when(aiRecommendLogMapper.insert(any())).thenReturn(1);

            // 执行测试 - 无明确关键词
            AiRecommendResponse result = aiRecommendService.getRecommendation("我想学习编程");

            // 验证结果
            assertNotNull(result);
            assertTrue(result.isFallback());
            assertNotNull(result.getCourses());
            // 无关键词时返回按难度排序的热门课程
            assertFalse(result.getCourses().isEmpty());
        }
    }

    @Test
    @DisplayName("推荐失败 - 无可用课程")
    void getRecommendation_NoCourses() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(courseMapper.selectList(any())).thenReturn(List.of()); // 无课程

            // 执行测试并验证异常
            assertThrows(BusinessException.class,
                    () -> aiRecommendService.getRecommendation("我想学习Java"));
        }
    }

    @Test
    @DisplayName("验证推荐结果包含必要字段")
    void getRecommendation_ResponseFields() {
        // 模拟安全上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("student1");

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(sysUserMapper.selectByUsername("student1")).thenReturn(testUser);
            when(courseMapper.selectList(any())).thenReturn(testCourses);
            when(aiRecommendLogMapper.insert(any())).thenReturn(1);

            // 执行测试
            AiRecommendResponse result = aiRecommendService.getRecommendation("我想学习Java开发");

            // 验证响应字段
            assertNotNull(result.getCourses());
            assertNotNull(result.getOverallReason());
            assertNotNull(result.getLearningPath());
            assertTrue(result.isFallback());

            // 验证课程字段
            if (!result.getCourses().isEmpty()) {
                var firstCourse = result.getCourses().get(0);
                assertNotNull(firstCourse.getCourseId());
                assertNotNull(firstCourse.getCourseName());
                assertNotNull(firstCourse.getCategory());
                assertNotNull(firstCourse.getDifficultyName());
                assertTrue(firstCourse.getOrder() > 0);
            }
        }
    }
}
