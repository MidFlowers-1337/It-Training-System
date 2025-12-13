package com.itts.modules.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.security.JwtTokenProvider;
import com.itts.modules.stats.dto.CourseHotItem;
import com.itts.modules.stats.dto.EnrollmentTrendItem;
import com.itts.modules.stats.dto.StatsOverviewResponse;
import com.itts.modules.stats.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 统计控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("统计控制器测试")
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatsService statsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private StatsOverviewResponse overviewResponse;
    private List<CourseHotItem> courseHotItems;
    private List<EnrollmentTrendItem> enrollmentTrendItems;

    @BeforeEach
    void setUp() {
        // 初始化统计概览响应
        overviewResponse = new StatsOverviewResponse();
        overviewResponse.setCourseCount(50L);
        overviewResponse.setSessionCount(120L);
        overviewResponse.setEnrollmentCount(1500L);
        overviewResponse.setStudentCount(600L);
        overviewResponse.setInstructorCount(20L);
        overviewResponse.setMonthlyEnrollmentCount(100L);
        overviewResponse.setActiveSessionCount(15L);

        // 初始化课程热度排行
        CourseHotItem hotItem1 = new CourseHotItem();
        hotItem1.setCourseId(1L);
        hotItem1.setCourseName("Java 基础入门");
        hotItem1.setCategory("BACKEND");
        hotItem1.setEnrollmentCount(150L);

        CourseHotItem hotItem2 = new CourseHotItem();
        hotItem2.setCourseId(2L);
        hotItem2.setCourseName("Python 数据分析");
        hotItem2.setCategory("DATA");
        hotItem2.setEnrollmentCount(120L);

        courseHotItems = Arrays.asList(hotItem1, hotItem2);

        // 初始化报名趋势
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        EnrollmentTrendItem trendItem1 = new EnrollmentTrendItem();
        trendItem1.setDate(LocalDate.now().minusDays(1).format(formatter));
        trendItem1.setCount(25L);

        EnrollmentTrendItem trendItem2 = new EnrollmentTrendItem();
        trendItem2.setDate(LocalDate.now().format(formatter));
        trendItem2.setCount(30L);

        enrollmentTrendItems = Arrays.asList(trendItem1, trendItem2);
    }

    @Test
    @DisplayName("获取统计概览 - 管理员权限")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getOverview_Success() throws Exception {
        when(statsService.getOverview()).thenReturn(overviewResponse);

        mockMvc.perform(get("/api/stats/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.courseCount").value(50))
                .andExpect(jsonPath("$.data.sessionCount").value(120))
                .andExpect(jsonPath("$.data.enrollmentCount").value(1500))
                .andExpect(jsonPath("$.data.studentCount").value(600));
    }

    @Test
    @DisplayName("获取课程热度排行 - 管理员权限")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCourseHotRanking_Success() throws Exception {
        when(statsService.getCourseHotRanking(anyInt())).thenReturn(courseHotItems);

        mockMvc.perform(get("/api/stats/course-hot").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1))
                .andExpect(jsonPath("$.data[0].courseName").value("Java 基础入门"))
                .andExpect(jsonPath("$.data[0].enrollmentCount").value(150));
    }

    @Test
    @DisplayName("获取课程热度排行 - 使用默认limit")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCourseHotRanking_DefaultLimit() throws Exception {
        when(statsService.getCourseHotRanking(10)).thenReturn(courseHotItems);

        mockMvc.perform(get("/api/stats/course-hot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("获取报名趋势 - 管理员权限")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getEnrollmentTrend_Success() throws Exception {
        when(statsService.getEnrollmentTrend(anyInt())).thenReturn(enrollmentTrendItems);

        mockMvc.perform(get("/api/stats/enrollment-trend").param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].count").value(25))
                .andExpect(jsonPath("$.data[1].count").value(30));
    }

    @Test
    @DisplayName("获取报名趋势 - 使用默认天数")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getEnrollmentTrend_DefaultDays() throws Exception {
        when(statsService.getEnrollmentTrend(7)).thenReturn(enrollmentTrendItems);

        mockMvc.perform(get("/api/stats/enrollment-trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("学生无权访问统计概览")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void getOverview_StudentForbidden() throws Exception {
        mockMvc.perform(get("/api/stats/overview"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("学生无权访问课程热度排行")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void getCourseHotRanking_StudentForbidden() throws Exception {
        mockMvc.perform(get("/api/stats/course-hot"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("学生无权访问报名趋势")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void getEnrollmentTrend_StudentForbidden() throws Exception {
        mockMvc.perform(get("/api/stats/enrollment-trend"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("未认证用户访问统计概览")
    void getOverview_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/stats/overview"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("未认证用户访问课程热度排行")
    void getCourseHotRanking_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/stats/course-hot"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("未认证用户访问报名趋势")
    void getEnrollmentTrend_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/stats/enrollment-trend"))
                .andExpect(status().isUnauthorized());
    }
}