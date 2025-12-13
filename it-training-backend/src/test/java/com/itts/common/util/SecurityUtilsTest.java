package com.itts.common.util;

import com.itts.common.exception.BusinessException;
import com.itts.common.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SecurityUtils 工具类单元测试
 */
@DisplayName("SecurityUtils 工具类测试")
class SecurityUtilsTest {

    private SecurityContext originalContext;

    @BeforeEach
    void setUp() {
        // 保存原始的安全上下文
        originalContext = SecurityContextHolder.getContext();
    }

    @AfterEach
    void tearDown() {
        // 恢复原始的安全上下文
        SecurityContextHolder.setContext(originalContext);
    }

    @Test
    @DisplayName("获取当前用户名 - UserDetails 类型")
    void getCurrentUsername_WithUserDetails() {
        // 准备模拟数据
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContextHolder.setContext(securityContext);

        // 执行测试
        String username = SecurityUtils.getCurrentUsername();

        // 验证结果
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("获取当前用户名 - String 类型")
    void getCurrentUsername_WithString() {
        // 准备模拟数据
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("stringuser");

        SecurityContextHolder.setContext(securityContext);

        // 执行测试
        String username = SecurityUtils.getCurrentUsername();

        // 验证结果
        assertEquals("stringuser", username);
    }

    @Test
    @DisplayName("获取当前用户名 - 认证对象为空")
    void getCurrentUsername_NullAuthentication() {
        // 准备模拟数据
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                SecurityUtils::getCurrentUsername);
        assertEquals(ErrorCode.UNAUTHORIZED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("获取当前用户名 - 未认证")
    void getCurrentUsername_NotAuthenticated() {
        // 准备模拟数据
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.setContext(securityContext);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                SecurityUtils::getCurrentUsername);
        assertEquals(ErrorCode.UNAUTHORIZED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("获取当前用户名 - 匿名用户")
    void getCurrentUsername_AnonymousUser() {
        // 准备模拟数据
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContextHolder.setContext(securityContext);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                SecurityUtils::getCurrentUsername);
        assertEquals(ErrorCode.UNAUTHORIZED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("获取当前用户名（可选）- 已认证")
    void getCurrentUsernameOptional_Authenticated() {
        // 准备模拟数据
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContextHolder.setContext(securityContext);

        // 执行测试
        Optional<String> result = SecurityUtils.getCurrentUsernameOptional();

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get());
    }

    @Test
    @DisplayName("获取当前用户名（可选）- 未认证")
    void getCurrentUsernameOptional_NotAuthenticated() {
        // 准备模拟数据
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // 执行测试
        Optional<String> result = SecurityUtils.getCurrentUsernameOptional();

        // 验证结果
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("获取当前用户名（可选）- 匿名用户")
    void getCurrentUsernameOptional_AnonymousUser() {
        // 准备模拟数据
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContextHolder.setContext(securityContext);

        // 执行测试
        Optional<String> result = SecurityUtils.getCurrentUsernameOptional();

        // 验证结果
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("检查是否已认证 - 已认证")
    void isAuthenticated_True() {
        // 准备模拟数据
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContextHolder.setContext(securityContext);

        // 执行测试
        boolean result = SecurityUtils.isAuthenticated();

        // 验证结果
        assertTrue(result);
    }

    @Test
    @DisplayName("检查是否已认证 - 未认证（认证对象为空）")
    void isAuthenticated_NullAuthentication() {
        // 准备模拟数据
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        // 执行测试
        boolean result = SecurityUtils.isAuthenticated();

        // 验证结果
        assertFalse(result);
    }

    @Test
    @DisplayName("检查是否已认证 - 未认证（isAuthenticated=false）")
    void isAuthenticated_NotAuthenticated() {
        // 准备模拟数据
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.setContext(securityContext);

        // 执行测试
        boolean result = SecurityUtils.isAuthenticated();

        // 验证结果
        assertFalse(result);
    }

    @Test
    @DisplayName("检查是否已认证 - 匿名用户")
    void isAuthenticated_AnonymousUser() {
        // 准备模拟数据
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContextHolder.setContext(securityContext);

        // 执行测试
        boolean result = SecurityUtils.isAuthenticated();

        // 验证结果
        assertFalse(result);
    }

    @Test
    @DisplayName("工具类不可实例化")
    void cannotInstantiate() {
        // SecurityUtils 是工具类，构造函数是私有的
        // 通过反射尝试实例化应该失败
        var constructors = SecurityUtils.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertFalse(constructors[0].canAccess(null));
    }
}
