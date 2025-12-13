package com.itts.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.modules.auth.dto.LoginRequest;
import com.itts.modules.auth.dto.RegisterRequest;
import com.itts.modules.auth.dto.TokenResponse;
import com.itts.modules.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("认证控制器测试")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private TokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        // 初始化登录请求
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // 初始化注册请求
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        registerRequest.setRealName("新用户");
        registerRequest.setPhone("13800138000");

        // 初始化 Token 响应
        tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("test-jwt-token");
        tokenResponse.setTokenType("Bearer");
        tokenResponse.setExpiresIn(86400000L);
        tokenResponse.setUserId(1L);
        tokenResponse.setUsername("testuser");
        tokenResponse.setRole("STUDENT");
    }

    @Test
    @DisplayName("登录成功")
    void login_Success() throws Exception {
        // 准备模拟数据
        when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

        // 执行测试
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.accessToken").value("test-jwt-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("登录失败 - 用户名密码错误")
    void login_BadCredentials() throws Exception {
        // 准备模拟数据
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("用户名或密码错误"));

        // 执行测试
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("登录失败 - 参数校验失败（用户名为空）")
    void login_ValidationFailed_UsernameEmpty() throws Exception {
        // 准备无效请求
        loginRequest.setUsername("");

        // 执行测试
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("登录失败 - 参数校验失败（密码为空）")
    void login_ValidationFailed_PasswordEmpty() throws Exception {
        // 准备无效请求
        loginRequest.setPassword("");

        // 执行测试
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("注册成功")
    void register_Success() throws Exception {
        // 准备模拟数据
        tokenResponse.setUsername("newuser");
        when(authService.register(any(RegisterRequest.class))).thenReturn(tokenResponse);

        // 执行测试
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.accessToken").value("test-jwt-token"))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void register_UsernameExists() throws Exception {
        // 准备模拟数据
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.USERNAME_EXISTS));

        // 执行测试
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.USERNAME_EXISTS.getCode()));
    }

    @Test
    @DisplayName("注册失败 - 两次密码不一致")
    void register_PasswordNotMatch() throws Exception {
        // 准备模拟数据
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.PASSWORD_NOT_MATCH));

        // 执行测试
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.PASSWORD_NOT_MATCH.getCode()));
    }

    @Test
    @DisplayName("注册失败 - 参数校验失败（用户名为空）")
    void register_ValidationFailed_UsernameEmpty() throws Exception {
        // 准备无效请求
        registerRequest.setUsername("");

        // 执行测试
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("注册失败 - 参数校验失败（手机号格式错误）")
    void register_ValidationFailed_InvalidPhone() throws Exception {
        // 准备无效请求
        registerRequest.setPhone("invalid");

        // 执行测试
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }
}
