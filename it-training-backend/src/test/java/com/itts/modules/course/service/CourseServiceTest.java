package com.itts.modules.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.modules.course.dto.CourseCreateRequest;
import com.itts.modules.course.dto.CourseResponse;
import com.itts.modules.course.dto.CourseUpdateRequest;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.course.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 课程服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("课程管理服务测试")
class CourseServiceTest {

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course testCourse;
    private CourseCreateRequest createRequest;
    private CourseUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试课程
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setCode("JAVA-001");
        testCourse.setName("Java 基础入门");
        testCourse.setCategory("BACKEND");
        testCourse.setDifficulty(1);
        testCourse.setDurationHours(40);
        testCourse.setTags("Java,入门");
        testCourse.setStatus(0);
        testCourse.setDeleted(0);

        // 初始化创建请求
        createRequest = new CourseCreateRequest();
        createRequest.setCode("VUE-001");
        createRequest.setName("Vue 3 前端开发");
        createRequest.setCategory("FRONTEND");
        createRequest.setDifficulty(2);
        createRequest.setDurationHours(50);

        // 初始化更新请求
        updateRequest = new CourseUpdateRequest();
        updateRequest.setName("Java 基础入门（更新版）");
        updateRequest.setDurationHours(45);
    }

    @Test
    @DisplayName("根据ID查询课程成功")
    void getCourseById_Success() {
        // 准备模拟数据
        when(courseMapper.selectById(1L)).thenReturn(testCourse);

        // 执行测试
        CourseResponse result = courseService.getCourseById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(testCourse.getCode(), result.getCode());
        assertEquals(testCourse.getName(), result.getName());
        assertEquals("后端开发", result.getCategoryName());

        verify(courseMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID查询课程失败 - 课程不存在")
    void getCourseById_NotFound() {
        // 准备模拟数据
        when(courseMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> courseService.getCourseById(999L));
        assertEquals(ErrorCode.COURSE_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("创建课程成功")
    void createCourse_Success() {
        // 准备模拟数据
        when(courseMapper.countByCode("VUE-001")).thenReturn(0);
        when(courseMapper.insert(any(Course.class))).thenAnswer(invocation -> {
            Course course = invocation.getArgument(0);
            course.setId(2L);
            return 1;
        });

        // 执行测试
        CourseResponse result = courseService.createCourse(createRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("VUE-001", result.getCode());
        assertEquals("Vue 3 前端开发", result.getName());

        verify(courseMapper).countByCode("VUE-001");
        verify(courseMapper).insert(any(Course.class));
    }

    @Test
    @DisplayName("创建课程失败 - 课程编码已存在")
    void createCourse_CodeExists() {
        // 准备模拟数据
        when(courseMapper.countByCode("VUE-001")).thenReturn(1);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> courseService.createCourse(createRequest));
        assertEquals(ErrorCode.COURSE_CODE_EXISTS.getCode(), exception.getCode());

        verify(courseMapper, never()).insert(any(Course.class));
    }

    @Test
    @DisplayName("更新课程成功")
    void updateCourse_Success() {
        // 准备模拟数据
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(courseMapper.updateById(any(Course.class))).thenReturn(1);

        // 执行测试
        CourseResponse result = courseService.updateCourse(1L, updateRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("Java 基础入门（更新版）", result.getName());
        assertEquals(45, result.getDurationHours());

        verify(courseMapper).selectById(1L);
        verify(courseMapper).updateById(any(Course.class));
    }

    @Test
    @DisplayName("更新课程失败 - 课程不存在")
    void updateCourse_NotFound() {
        // 准备模拟数据
        when(courseMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> courseService.updateCourse(999L, updateRequest));
        assertEquals(ErrorCode.COURSE_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("删除课程成功")
    void deleteCourse_Success() {
        // 准备模拟数据
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(courseMapper.countSessionsByCourseId(1L)).thenReturn(0);
        when(courseMapper.updateById(any(Course.class))).thenReturn(1);

        // 执行测试
        courseService.deleteCourse(1L);

        // 验证调用
        verify(courseMapper).selectById(1L);
        verify(courseMapper).countSessionsByCourseId(1L);
        verify(courseMapper).updateById(argThat(course -> course.getDeleted() == 1));
    }

    @Test
    @DisplayName("删除课程失败 - 存在关联班期")
    void deleteCourse_HasSession() {
        // 准备模拟数据
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(courseMapper.countSessionsByCourseId(1L)).thenReturn(2);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> courseService.deleteCourse(1L));
        assertEquals(ErrorCode.COURSE_HAS_SESSION.getCode(), exception.getCode());

        verify(courseMapper, never()).updateById(any(Course.class));
    }

    @Test
    @DisplayName("发布课程成功")
    void publishCourse_Success() {
        // 准备模拟数据
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(courseMapper.updateById(any(Course.class))).thenReturn(1);

        // 执行测试
        courseService.publishCourse(1L);

        // 验证调用
        verify(courseMapper).selectById(1L);
        verify(courseMapper).updateById(argThat(course -> course.getStatus() == 1));
    }

    @Test
    @DisplayName("下架课程成功")
    void unpublishCourse_Success() {
        // 准备模拟数据
        testCourse.setStatus(1); // 已发布状态
        when(courseMapper.selectById(1L)).thenReturn(testCourse);
        when(courseMapper.updateById(any(Course.class))).thenReturn(1);

        // 执行测试
        courseService.unpublishCourse(1L);

        // 验证调用
        verify(courseMapper).selectById(1L);
        verify(courseMapper).updateById(argThat(course -> course.getStatus() == 0));
    }
}
