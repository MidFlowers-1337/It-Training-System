package com.itts.modules.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.common.security.JwtTokenProvider;
import com.itts.modules.course.dto.CourseCreateRequest;
import com.itts.modules.course.dto.CourseResponse;
import com.itts.modules.course.dto.CourseUpdateRequest;
import com.itts.modules.course.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 课程控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("课程控制器测试")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private CourseResponse courseResponse;
    private CourseCreateRequest createRequest;
    private CourseUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // 初始化课程响应
        courseResponse = new CourseResponse();
        courseResponse.setId(1L);
        courseResponse.setCode("JAVA-001");
        courseResponse.setName("Java 基础入门");
        courseResponse.setCategory("BACKEND");
        courseResponse.setCategoryName("后端开发");
        courseResponse.setDifficulty(1);
        courseResponse.setDifficultyName("入门");
        courseResponse.setDurationHours(40);
        courseResponse.setStatus(1);

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
    @DisplayName("查询课程详情")
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void getCourseById_Success() throws Exception {
        // 准备模拟数据
        when(courseService.getCourseById(1L)).thenReturn(courseResponse);

        // 执行测试
        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.code").value("JAVA-001"))
                .andExpect(jsonPath("$.data.name").value("Java 基础入门"));
    }

    @Test
    @DisplayName("查询课程详情 - 课程不存在")
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void getCourseById_NotFound() throws Exception {
        // 准备模拟数据
        when(courseService.getCourseById(999L))
                .thenThrow(new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        // 执行测试
        mockMvc.perform(get("/api/courses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.COURSE_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("创建课程 - 管理员权限")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCourse_Success() throws Exception {
        // 准备模拟数据
        CourseResponse newCourse = new CourseResponse();
        newCourse.setId(2L);
        newCourse.setCode("VUE-001");
        newCourse.setName("Vue 3 前端开发");
        when(courseService.createCourse(any(CourseCreateRequest.class))).thenReturn(newCourse);

        // 执行测试
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.code").value("VUE-001"));
    }

    @Test
    @DisplayName("创建课程 - 学生无权限")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void createCourse_Forbidden() throws Exception {
        // 执行测试
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("创建课程 - 课程编码已存在")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCourse_CodeExists() throws Exception {
        // 准备模拟数据
        when(courseService.createCourse(any(CourseCreateRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.COURSE_CODE_EXISTS));

        // 执行测试
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.COURSE_CODE_EXISTS.getCode()));
    }

    @Test
    @DisplayName("更新课程")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCourse_Success() throws Exception {
        // 准备模拟数据
        courseResponse.setName("Java 基础入门（更新版）");
        courseResponse.setDurationHours(45);
        when(courseService.updateCourse(anyLong(), any(CourseUpdateRequest.class)))
                .thenReturn(courseResponse);

        // 执行测试
        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("Java 基础入门（更新版）"))
                .andExpect(jsonPath("$.data.durationHours").value(45));
    }

    @Test
    @DisplayName("删除课程")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCourse_Success() throws Exception {
        // 准备模拟数据
        doNothing().when(courseService).deleteCourse(1L);

        // 执行测试
        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(courseService).deleteCourse(1L);
    }

    @Test
    @DisplayName("删除课程 - 存在关联班期")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCourse_HasSession() throws Exception {
        // 准备模拟数据
        doThrow(new BusinessException(ErrorCode.COURSE_HAS_SESSION))
                .when(courseService).deleteCourse(1L);

        // 执行测试
        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.COURSE_HAS_SESSION.getCode()));
    }

    @Test
    @DisplayName("发布课程")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void publishCourse_Success() throws Exception {
        // 准备模拟数据
        doNothing().when(courseService).publishCourse(1L);

        // 执行测试
        mockMvc.perform(put("/api/courses/1/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(courseService).publishCourse(1L);
    }

    @Test
    @DisplayName("下架课程")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void unpublishCourse_Success() throws Exception {
        // 准备模拟数据
        doNothing().when(courseService).unpublishCourse(1L);

        // 执行测试
        mockMvc.perform(put("/api/courses/1/unpublish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(courseService).unpublishCourse(1L);
    }

    @Test
    @DisplayName("未认证用户访问")
    void accessWithoutAuth() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isUnauthorized());
    }
}
