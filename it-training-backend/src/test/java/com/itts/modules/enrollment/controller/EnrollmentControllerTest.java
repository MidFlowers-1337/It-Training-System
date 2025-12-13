package com.itts.modules.enrollment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.common.security.JwtTokenProvider;
import com.itts.enums.EnrollmentStatus;
import com.itts.modules.enrollment.dto.EnrollmentResponse;
import com.itts.modules.enrollment.service.EnrollmentService;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 报名控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("报名控制器测试")
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnrollmentService enrollmentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private EnrollmentResponse enrollmentResponse;

    @BeforeEach
    void setUp() {
        // 初始化报名响应
        enrollmentResponse = new EnrollmentResponse();
        enrollmentResponse.setId(1L);
        enrollmentResponse.setUserId(1L);
        enrollmentResponse.setSessionId(1L);
        enrollmentResponse.setStatus(EnrollmentStatus.ENROLLED.getCode());
        enrollmentResponse.setStatusName("已报名");
        enrollmentResponse.setEnrolledAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("报名成功")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void enroll_Success() throws Exception {
        // 准备模拟数据
        when(enrollmentService.enroll(1L)).thenReturn(enrollmentResponse);

        // 准备请求体
        Map<String, Long> request = new HashMap<>();
        request.put("sessionId", 1L);

        // 执行测试
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value(EnrollmentStatus.ENROLLED.getCode()));
    }

    @Test
    @DisplayName("报名失败 - 班期不存在")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void enroll_SessionNotFound() throws Exception {
        // 准备模拟数据
        when(enrollmentService.enroll(999L))
                .thenThrow(new BusinessException(ErrorCode.SESSION_NOT_FOUND));

        // 准备请求体
        Map<String, Long> request = new HashMap<>();
        request.put("sessionId", 999L);

        // 执行测试
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.SESSION_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("报名失败 - 班期未开放报名")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void enroll_SessionNotEnrollable() throws Exception {
        // 准备模拟数据
        when(enrollmentService.enroll(1L))
                .thenThrow(new BusinessException(ErrorCode.SESSION_NOT_ENROLLABLE));

        // 准备请求体
        Map<String, Long> request = new HashMap<>();
        request.put("sessionId", 1L);

        // 执行测试
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.SESSION_NOT_ENROLLABLE.getCode()));
    }

    @Test
    @DisplayName("报名失败 - 重复报名")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void enroll_Duplicate() throws Exception {
        // 准备模拟数据
        when(enrollmentService.enroll(1L))
                .thenThrow(new BusinessException(ErrorCode.ENROLLMENT_DUPLICATE));

        // 准备请求体
        Map<String, Long> request = new HashMap<>();
        request.put("sessionId", 1L);

        // 执行测试
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.ENROLLMENT_DUPLICATE.getCode()));
    }

    @Test
    @DisplayName("报名失败 - 名额已满")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void enroll_QuotaFull() throws Exception {
        // 准备模拟数据
        when(enrollmentService.enroll(1L))
                .thenThrow(new BusinessException(ErrorCode.ENROLLMENT_QUOTA_FULL));

        // 准备请求体
        Map<String, Long> request = new HashMap<>();
        request.put("sessionId", 1L);

        // 执行测试
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.ENROLLMENT_QUOTA_FULL.getCode()));
    }

    @Test
    @DisplayName("取消报名成功")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void cancelEnrollment_Success() throws Exception {
        // 准备模拟数据
        doNothing().when(enrollmentService).cancelEnrollment(eq(1L), anyString());

        // 准备请求体
        Map<String, String> request = new HashMap<>();
        request.put("reason", "个人原因");

        // 执行测试
        mockMvc.perform(put("/api/enrollments/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(enrollmentService).cancelEnrollment(eq(1L), anyString());
    }

    @Test
    @DisplayName("取消报名失败 - 报名记录不存在")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void cancelEnrollment_NotFound() throws Exception {
        // 准备模拟数据
        doThrow(new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND))
                .when(enrollmentService).cancelEnrollment(eq(999L), anyString());

        // 准备请求体
        Map<String, String> request = new HashMap<>();
        request.put("reason", "个人原因");

        // 执行测试
        mockMvc.perform(put("/api/enrollments/999/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.ENROLLMENT_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("取消报名失败 - 非本人报名")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void cancelEnrollment_Forbidden() throws Exception {
        // 准备模拟数据
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(enrollmentService).cancelEnrollment(eq(1L), anyString());

        // 准备请求体
        Map<String, String> request = new HashMap<>();
        request.put("reason", "个人原因");

        // 执行测试
        mockMvc.perform(put("/api/enrollments/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN.getCode()));
    }

    @Test
    @DisplayName("取消报名失败 - 班期已开始")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void cancelEnrollment_SessionStarted() throws Exception {
        // 准备模拟数据
        doThrow(new BusinessException(ErrorCode.ENROLLMENT_CANNOT_CANCEL))
                .when(enrollmentService).cancelEnrollment(eq(1L), anyString());

        // 准备请求体
        Map<String, String> request = new HashMap<>();
        request.put("reason", "个人原因");

        // 执行测试
        mockMvc.perform(put("/api/enrollments/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.ENROLLMENT_CANNOT_CANCEL.getCode()));
    }

    @Test
    @DisplayName("未认证用户访问")
    void accessWithoutAuth() throws Exception {
        // 准备请求体
        Map<String, Long> request = new HashMap<>();
        request.put("sessionId", 1L);

        // 执行测试
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
