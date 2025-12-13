package com.itts.modules.session.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.common.security.JwtTokenProvider;
import com.itts.modules.session.dto.SessionCreateRequest;
import com.itts.modules.session.dto.SessionResponse;
import com.itts.modules.session.dto.SessionUpdateRequest;
import com.itts.modules.session.service.SessionService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 班期控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("班期控制器测试")
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private SessionResponse sessionResponse;
    private SessionCreateRequest createRequest;
    private SessionUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // 初始化班期响应
        sessionResponse = new SessionResponse();
        sessionResponse.setId(1L);
        sessionResponse.setCourseId(1L);
        sessionResponse.setCourseName("Java 基础入门");
        sessionResponse.setInstructorId(2L);
        sessionResponse.setInstructorName("张老师");
        sessionResponse.setSessionCode("JAVA-001-2025S1");
        sessionResponse.setStartDate(LocalDate.of(2025, 1, 15));
        sessionResponse.setEndDate(LocalDate.of(2025, 3, 15));
        sessionResponse.setSchedule("每周六 9:00-12:00");
        sessionResponse.setLocation("线上直播");
        sessionResponse.setMaxCapacity(30);
        sessionResponse.setCurrentEnrollment(10);
        sessionResponse.setStatus(1);
        sessionResponse.setStatusName("报名中");

        // 初始化创建请求
        createRequest = new SessionCreateRequest();
        createRequest.setCourseId(1L);
        createRequest.setInstructorId(2L);
        createRequest.setSessionCode("JAVA-001-2025S2");
        createRequest.setStartDate(LocalDate.of(2025, 2, 1));
        createRequest.setEndDate(LocalDate.of(2025, 4, 1));
        createRequest.setSchedule("每周日 14:00-17:00");
        createRequest.setLocation("线上直播");
        createRequest.setMaxCapacity(25);

        // 初始化更新请求
        updateRequest = new SessionUpdateRequest();
        updateRequest.setSchedule("每周六 14:00-17:00");
        updateRequest.setMaxCapacity(35);
    }

    @Test
    @DisplayName("查询班期详情")
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void getSessionById_Success() throws Exception {
        when(sessionService.getSessionById(1L)).thenReturn(sessionResponse);

        mockMvc.perform(get("/api/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.sessionCode").value("JAVA-001-2025S1"))
                .andExpect(jsonPath("$.data.courseName").value("Java 基础入门"));
    }

    @Test
    @DisplayName("查询班期详情 - 班期不存在")
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void getSessionById_NotFound() throws Exception {
        when(sessionService.getSessionById(999L))
                .thenThrow(new BusinessException(ErrorCode.SESSION_NOT_FOUND));

        mockMvc.perform(get("/api/sessions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.SESSION_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("查询可报名班期列表")
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void listEnrollableSessions_Success() throws Exception {
        List<SessionResponse> sessions = Arrays.asList(sessionResponse);
        when(sessionService.listEnrollableSessions(1L)).thenReturn(sessions);

        mockMvc.perform(get("/api/sessions/enrollable").param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].sessionCode").value("JAVA-001-2025S1"));
    }

    @Test
    @DisplayName("创建班期 - 管理员权限")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createSession_Success() throws Exception {
        SessionResponse newSession = new SessionResponse();
        newSession.setId(2L);
        newSession.setSessionCode("JAVA-001-2025S2");
        when(sessionService.createSession(any(SessionCreateRequest.class))).thenReturn(newSession);

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.sessionCode").value("JAVA-001-2025S2"));
    }

    @Test
    @DisplayName("创建班期 - 学生无权限")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void createSession_Forbidden() throws Exception {
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("创建班期 - 班期编码已存在")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createSession_CodeExists() throws Exception {
        when(sessionService.createSession(any(SessionCreateRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.SESSION_CODE_EXISTS));

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.SESSION_CODE_EXISTS.getCode()));
    }

    @Test
    @DisplayName("更新班期")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateSession_Success() throws Exception {
        sessionResponse.setSchedule("每周六 14:00-17:00");
        sessionResponse.setMaxCapacity(35);
        when(sessionService.updateSession(anyLong(), any(SessionUpdateRequest.class)))
                .thenReturn(sessionResponse);

        mockMvc.perform(put("/api/sessions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.schedule").value("每周六 14:00-17:00"))
                .andExpect(jsonPath("$.data.maxCapacity").value(35));
    }

    @Test
    @DisplayName("删除班期")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteSession_Success() throws Exception {
        doNothing().when(sessionService).deleteSession(1L);

        mockMvc.perform(delete("/api/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(sessionService).deleteSession(1L);
    }

    @Test
    @DisplayName("删除班期 - 存在报名记录")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteSession_HasEnrollment() throws Exception {
        doThrow(new BusinessException(ErrorCode.SESSION_HAS_ENROLLMENT))
                .when(sessionService).deleteSession(1L);

        mockMvc.perform(delete("/api/sessions/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.SESSION_HAS_ENROLLMENT.getCode()));
    }

    @Test
    @DisplayName("开放报名")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void openEnrollment_Success() throws Exception {
        doNothing().when(sessionService).openEnrollment(1L);

        mockMvc.perform(patch("/api/sessions/1/open"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(sessionService).openEnrollment(1L);
    }

    @Test
    @DisplayName("关闭报名")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void closeEnrollment_Success() throws Exception {
        doNothing().when(sessionService).closeEnrollment(1L);

        mockMvc.perform(patch("/api/sessions/1/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(sessionService).closeEnrollment(1L);
    }

    @Test
    @DisplayName("未认证用户访问")
    void accessWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/sessions/1"))
                .andExpect(status().isUnauthorized());
    }
}
