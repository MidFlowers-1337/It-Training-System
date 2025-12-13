package com.itts.modules.auth.service;

import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import com.itts.common.security.JwtTokenProvider;
import com.itts.modules.auth.dto.LoginRequest;
import com.itts.modules.auth.dto.RegisterRequest;
import com.itts.modules.auth.dto.TokenResponse;
import com.itts.modules.auth.service.impl.AuthServiceImpl;
import com.itts.modules.user.entity.SysUser;
import com.itts.modules.user.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 认证服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务测试")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private SysUser testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encoded_password");
        testUser.setRealName("测试用户");
        testUser.setRole("STUDENT");
        testUser.setStatus(1);

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
    }

    @Test
    @DisplayName("登录成功")
    void login_Success() {
        // 准备模拟数据
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(sysUserMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("test-jwt-token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(86400000L);

        // 执行测试
        TokenResponse response = authService.login(loginRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getRole(), response.getRole());

        // 验证调用
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(sysUserMapper).selectByUsername("testuser");
        verify(jwtTokenProvider).generateToken(authentication);
    }

    @Test
    @DisplayName("登录失败 - 用户名或密码错误")
    void login_BadCredentials() {
        // 模拟认证失败
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // 执行测试并验证异常
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void login_UserNotFound() {
        // 准备模拟数据
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(sysUserMapper.selectByUsername("testuser")).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.login(loginRequest));
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("登录失败 - 账号被禁用")
    void login_AccountDisabled() {
        // 准备模拟数据
        testUser.setStatus(0); // 禁用状态
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(sysUserMapper.selectByUsername("testuser")).thenReturn(testUser);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.login(loginRequest));
        assertEquals(ErrorCode.ACCOUNT_DISABLED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("注册成功")
    void register_Success() {
        // 准备模拟数据
        when(sysUserMapper.selectByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded_password");
        when(sysUserMapper.insert(any(SysUser.class))).thenAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(2L);
            return 1;
        });

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("test-jwt-token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(86400000L);

        // 执行测试
        TokenResponse response = authService.register(registerRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getAccessToken());
        assertEquals("newuser", response.getUsername());
        assertEquals("STUDENT", response.getRole());

        // 验证调用
        verify(sysUserMapper).selectByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(sysUserMapper).insert(any(SysUser.class));
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void register_UsernameExists() {
        // 模拟用户名已存在
        when(sysUserMapper.selectByUsername("newuser")).thenReturn(testUser);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.register(registerRequest));
        assertEquals(ErrorCode.USERNAME_EXISTS.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("注册失败 - 两次密码不一致")
    void register_PasswordNotMatch() {
        // 设置不一致的密码
        registerRequest.setConfirmPassword("different_password");

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.register(registerRequest));
        assertEquals(ErrorCode.PASSWORD_NOT_MATCH.getCode(), exception.getCode());
    }
}
