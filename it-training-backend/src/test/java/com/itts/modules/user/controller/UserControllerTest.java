package com.itts.modules.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.common.security.JwtTokenProvider;
import com.itts.modules.user.dto.PasswordResetRequest;
import com.itts.modules.user.dto.UserCreateRequest;
import com.itts.modules.user.dto.UserResponse;
import com.itts.modules.user.dto.UserUpdateRequest;
import com.itts.modules.user.service.UserService;
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
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("用户控制器测试")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UserResponse userResponse;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;
    private PasswordResetRequest passwordResetRequest;

    @BeforeEach
    void setUp() {
        // 初始化用户响应
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setRealName("测试用户");
        userResponse.setPhone("13800138000");
        userResponse.setEmail("test@example.com");
        userResponse.setRole("STUDENT");
        userResponse.setStatus(1);
        userResponse.setCreatedAt(LocalDateTime.now());
        userResponse.setUpdatedAt(LocalDateTime.now());

        // 初始化创建请求
        createRequest = new UserCreateRequest();
        createRequest.setUsername("newuser");
        createRequest.setPassword("password123");
        createRequest.setRealName("新用户");
        createRequest.setPhone("13900139000");
        createRequest.setEmail("newuser@example.com");
        createRequest.setRole("STUDENT");

        // 初始化更新请求
        updateRequest = new UserUpdateRequest();
        updateRequest.setRealName("更新后的用户");
        updateRequest.setPhone("13800138001");
        updateRequest.setEmail("updated@example.com");

        // 初始化密码重置请求
        passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setNewPassword("newpassword123");
    }

    @Test
    @DisplayName("分页查询用户列表 - 管理员权限")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listUsers_Success() throws Exception {
        IPage<UserResponse> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(userResponse));
        page.setTotal(1);
        
        when(userService.listUsers(anyInt(), anyInt(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records[0].username").value("testuser"));
    }

    @Test
    @DisplayName("分页查询用户列表 - 带角色过滤")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listUsers_WithRoleFilter() throws Exception {
        IPage<UserResponse> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(userResponse));
        page.setTotal(1);
        
        when(userService.listUsers(1, 10, "STUDENT", null)).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "1")
                        .param("size", "10")
                        .param("role", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("分页查询用户列表 - 带关键词搜索")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listUsers_WithKeyword() throws Exception {
        IPage<UserResponse> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(userResponse));
        page.setTotal(1);
        
        when(userService.listUsers(1, 10, null, "test")).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "1")
                        .param("size", "10")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("根据ID获取用户")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.realName").value("测试用户"));
    }

    @Test
    @DisplayName("根据ID获取用户 - 用户不存在")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUserById_NotFound() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("创建用户 - 管理员权限")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_Success() throws Exception {
        UserResponse newUser = new UserResponse();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setRealName("新用户");
        newUser.setRole("STUDENT");
        newUser.setStatus(1);
        
        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(newUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    @DisplayName("创建用户 - 用户名已存在")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_UsernameExists() throws Exception {
        when(userService.createUser(any(UserCreateRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.USERNAME_EXISTS));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.USERNAME_EXISTS.getCode()));
    }

    @Test
    @DisplayName("创建用户 - 参数校验失败（用户名为空）")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_ValidationFailed_UsernameEmpty() throws Exception {
        createRequest.setUsername("");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("更新用户")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUser_Success() throws Exception {
        userResponse.setRealName("更新后的用户");
        userResponse.setPhone("13800138001");
        
        when(userService.updateUser(anyLong(), any(UserUpdateRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.realName").value("更新后的用户"));
    }

    @Test
    @DisplayName("更新用户 - 用户不存在")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUser_NotFound() throws Exception {
        when(userService.updateUser(anyLong(), any(UserUpdateRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("删除用户")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("删除用户 - 用户不存在")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_NotFound() throws Exception {
        doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("重置用户密码")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void resetPassword_Success() throws Exception {
        doNothing().when(userService).resetPassword(eq(1L), any(PasswordResetRequest.class));

        mockMvc.perform(patch("/api/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(userService).resetPassword(eq(1L), any(PasswordResetRequest.class));
    }

    @Test
    @DisplayName("重置用户密码 - 用户不存在")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void resetPassword_NotFound() throws Exception {
        doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND))
                .when(userService).resetPassword(eq(999L), any(PasswordResetRequest.class));

        mockMvc.perform(patch("/api/users/999/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("更新用户状态 - 启用")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateStatus_Enable() throws Exception {
        doNothing().when(userService).updateStatus(1L, 1);

        mockMvc.perform(patch("/api/users/1/status")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(userService).updateStatus(1L, 1);
    }

    @Test
    @DisplayName("更新用户状态 - 禁用")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateStatus_Disable() throws Exception {
        doNothing().when(userService).updateStatus(1L, 0);

        mockMvc.perform(patch("/api/users/1/status")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(userService).updateStatus(1L, 0);
    }

    @Test
    @DisplayName("学生无权访问用户管理")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void listUsers_StudentForbidden() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("讲师无权访问用户管理")
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    void listUsers_InstructorForbidden() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("未认证用户访问用户列表")
    void listUsers_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("未认证用户访问用户详情")
    void getUserById_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("未认证用户创建用户")
    void createUser_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }
}