package com.itts.modules.stats.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itts.enums.EnrollmentStatus;
import com.itts.enums.SessionStatus;
import com.itts.modules.course.entity.Course;
import com.itts.modules.course.mapper.CourseMapper;
import com.itts.modules.enrollment.entity.Enrollment;
import com.itts.modules.enrollment.mapper.EnrollmentMapper;
import com.itts.modules.session.entity.ClassSession;
import com.itts.modules.session.mapper.ClassSessionMapper;
import com.itts.modules.stats.dto.CourseHotItem;
import com.itts.modules.stats.dto.EnrollmentTrendItem;
import com.itts.modules.stats.dto.StatsOverviewResponse;
import com.itts.modules.stats.service.impl.StatsServiceImpl;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 统计服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("统计服务测试")
class StatsServiceTest {

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private ClassSessionMapper classSessionMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @InjectMocks
    private StatsServiceImpl statsService;

    private List<Course> testCourses;
    private List<ClassSession> testSessions;
    private List<Enrollment> testEnrollments;

    @BeforeEach
    void setUp() {
        // 初始化测试课程
        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Java 基础入门");
        course1.setCategory("BACKEND");
        course1.setStatus(1);
        course1.setDeleted(0);

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Vue 3 前端开发");
        course2.setCategory("FRONTEND");
        course2.setStatus(1);
        course2.setDeleted(0);

        testCourses = List.of(course1, course2);

        // 初始化测试班期
        ClassSession session1 = new ClassSession();
        session1.setId(1L);
        session1.setCourseId(1L);
        session1.setStatus(SessionStatus.ENROLLING.getCode());
        session1.setDeleted(0);

        ClassSession session2 = new ClassSession();
        session2.setId(2L);
        session2.setCourseId(2L);
        session2.setStatus(SessionStatus.IN_PROGRESS.getCode());
        session2.setDeleted(0);

        testSessions = List.of(session1, session2);

        // 初始化测试报名
        Enrollment enrollment1 = new Enrollment();
        enrollment1.setId(1L);
        enrollment1.setUserId(1L);
        enrollment1.setSessionId(1L);
        enrollment1.setStatus(EnrollmentStatus.ENROLLED.getCode());
        enrollment1.setEnrolledAt(LocalDateTime.now());

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setId(2L);
        enrollment2.setUserId(2L);
        enrollment2.setSessionId(1L);
        enrollment2.setStatus(EnrollmentStatus.ENROLLED.getCode());
        enrollment2.setEnrolledAt(LocalDateTime.now().minusDays(1));

        Enrollment enrollment3 = new Enrollment();
        enrollment3.setId(3L);
        enrollment3.setUserId(3L);
        enrollment3.setSessionId(2L);
        enrollment3.setStatus(EnrollmentStatus.ENROLLED.getCode());
        enrollment3.setEnrolledAt(LocalDateTime.now().minusDays(2));

        testEnrollments = List.of(enrollment1, enrollment2, enrollment3);
    }

    @Test
    @DisplayName("获取统计概览")
    void getOverview_Success() {
        // 准备模拟数据
        when(courseMapper.selectCount(any())).thenReturn(10L);
        when(classSessionMapper.selectCount(any())).thenReturn(20L, 5L);
        when(sysUserMapper.selectCount(any())).thenReturn(100L, 10L);
        when(enrollmentMapper.selectCount(any())).thenReturn(500L, 50L);

        // 执行测试
        StatsOverviewResponse result = statsService.getOverview();

        // 验证结果
        assertNotNull(result);
        assertEquals(10L, result.getCourseCount());
        assertEquals(20L, result.getSessionCount());
        assertEquals(5L, result.getActiveSessionCount());
        assertEquals(100L, result.getStudentCount());
        assertEquals(10L, result.getInstructorCount());
        assertEquals(500L, result.getEnrollmentCount());
        assertEquals(50L, result.getMonthlyEnrollmentCount());
    }

    @Test
    @DisplayName("获取统计概览 - 无数据")
    void getOverview_NoData() {
        // 准备模拟数据
        when(courseMapper.selectCount(any())).thenReturn(0L);
        when(classSessionMapper.selectCount(any())).thenReturn(0L, 0L);
        when(sysUserMapper.selectCount(any())).thenReturn(0L, 0L);
        when(enrollmentMapper.selectCount(any())).thenReturn(0L, 0L);

        // 执行测试
        StatsOverviewResponse result = statsService.getOverview();

        // 验证结果
        assertNotNull(result);
        assertEquals(0L, result.getCourseCount());
        assertEquals(0L, result.getSessionCount());
        assertEquals(0L, result.getActiveSessionCount());
        assertEquals(0L, result.getStudentCount());
        assertEquals(0L, result.getInstructorCount());
        assertEquals(0L, result.getEnrollmentCount());
        assertEquals(0L, result.getMonthlyEnrollmentCount());
    }

    @Test
    @DisplayName("获取课程热度排行")
    void getCourseHotRanking_Success() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(testEnrollments);
        when(classSessionMapper.selectList(any())).thenReturn(testSessions);
        when(courseMapper.selectList(any())).thenReturn(testCourses);

        // 执行测试
        List<CourseHotItem> result = statsService.getCourseHotRanking(10);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        // Java课程有2个报名，排在第一
        assertEquals("Java 基础入门", result.get(0).getCourseName());
        assertEquals(2L, result.get(0).getEnrollmentCount());
        // Vue课程有1个报名，排在第二
        assertEquals("Vue 3 前端开发", result.get(1).getCourseName());
        assertEquals(1L, result.get(1).getEnrollmentCount());
    }

    @Test
    @DisplayName("获取课程热度排行 - 限制数量")
    void getCourseHotRanking_WithLimit() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(testEnrollments);
        when(classSessionMapper.selectList(any())).thenReturn(testSessions);
        when(courseMapper.selectList(any())).thenReturn(testCourses);

        // 执行测试 - 限制只返回1个
        List<CourseHotItem> result = statsService.getCourseHotRanking(1);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java 基础入门", result.get(0).getCourseName());
    }

    @Test
    @DisplayName("获取课程热度排行 - 无报名数据")
    void getCourseHotRanking_NoEnrollments() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(new ArrayList<>());

        // 执行测试
        List<CourseHotItem> result = statsService.getCourseHotRanking(10);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取课程热度排行 - 无班期数据")
    void getCourseHotRanking_NoSessions() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(testEnrollments);
        when(classSessionMapper.selectList(any())).thenReturn(new ArrayList<>());

        // 执行测试
        List<CourseHotItem> result = statsService.getCourseHotRanking(10);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取课程热度排行 - 边界值测试")
    void getCourseHotRanking_BoundaryValues() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(testEnrollments);
        when(classSessionMapper.selectList(any())).thenReturn(testSessions);
        when(courseMapper.selectList(any())).thenReturn(testCourses);

        // 测试 limit <= 0 时使用默认值 10
        List<CourseHotItem> result1 = statsService.getCourseHotRanking(0);
        assertNotNull(result1);

        // 测试 limit > 50 时限制为 50
        List<CourseHotItem> result2 = statsService.getCourseHotRanking(100);
        assertNotNull(result2);
    }

    @Test
    @DisplayName("获取报名趋势")
    void getEnrollmentTrend_Success() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(testEnrollments);

        // 执行测试
        List<EnrollmentTrendItem> result = statsService.getEnrollmentTrend(7);

        // 验证结果
        assertNotNull(result);
        assertEquals(7, result.size());

        // 验证按日期排序
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getDate().compareTo(result.get(i + 1).getDate()) <= 0);
        }
    }

    @Test
    @DisplayName("获取报名趋势 - 无数据")
    void getEnrollmentTrend_NoData() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(new ArrayList<>());

        // 执行测试
        List<EnrollmentTrendItem> result = statsService.getEnrollmentTrend(7);

        // 验证结果
        assertNotNull(result);
        assertEquals(7, result.size());
        // 所有日期的报名数都应为0
        assertTrue(result.stream().allMatch(item -> item.getCount() == 0));
    }

    @Test
    @DisplayName("获取报名趋势 - 边界值测试")
    void getEnrollmentTrend_BoundaryValues() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(testEnrollments);

        // 测试 days <= 0 时使用默认值 7
        List<EnrollmentTrendItem> result1 = statsService.getEnrollmentTrend(0);
        assertNotNull(result1);
        assertEquals(7, result1.size());

        // 测试 days > 90 时限制为 90
        List<EnrollmentTrendItem> result2 = statsService.getEnrollmentTrend(100);
        assertNotNull(result2);
        assertEquals(90, result2.size());
    }

    @Test
    @DisplayName("获取报名趋势 - 30天")
    void getEnrollmentTrend_30Days() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(testEnrollments);

        // 执行测试
        List<EnrollmentTrendItem> result = statsService.getEnrollmentTrend(30);

        // 验证结果
        assertNotNull(result);
        assertEquals(30, result.size());
    }

    @Test
    @DisplayName("报名趋势日期格式正确")
    void getEnrollmentTrend_DateFormat() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(testEnrollments);

        // 执行测试
        List<EnrollmentTrendItem> result = statsService.getEnrollmentTrend(7);

        // 验证日期格式为 yyyy-MM-dd
        for (EnrollmentTrendItem item : result) {
            assertNotNull(item.getDate());
            assertTrue(item.getDate().matches("\\d{4}-\\d{2}-\\d{2}"));
        }
    }

    @Test
    @DisplayName("课程热度排行包含必要字段")
    void getCourseHotRanking_RequiredFields() {
        // 准备模拟数据
        when(enrollmentMapper.selectList(any())).thenReturn(testEnrollments);
        when(classSessionMapper.selectList(any())).thenReturn(testSessions);
        when(courseMapper.selectList(any())).thenReturn(testCourses);

        // 执行测试
        List<CourseHotItem> result = statsService.getCourseHotRanking(10);

        // 验证每个项目都包含必要字段
        for (CourseHotItem item : result) {
            assertNotNull(item.getCourseId());
            assertNotNull(item.getCourseName());
            assertNotNull(item.getCategory());
            assertTrue(item.getEnrollmentCount() >= 0);
        }
    }
}
