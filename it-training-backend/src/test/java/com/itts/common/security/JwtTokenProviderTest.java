package com.itts.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JwtTokenProvider 单元测试
 */
@DisplayName("JWT Token 提供者测试")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    // 测试用的密钥（至少256位）
    private static final String TEST_SECRET = "YourSuperSecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong123456789";
    private static final long TEST_EXPIRATION = 86400000L; // 24小时

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // 使用反射设置私有字段
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", TEST_EXPIRATION);
    }

    @Test
    @DisplayName("获取过期时间")
    void getExpirationMs() {
        // 执行测试
        long expiration = jwtTokenProvider.getExpirationMs();

        // 验证结果
        assertEquals(TEST_EXPIRATION, expiration);
    }

    @Test
    @DisplayName("根据用户名生成 Token")
    void generateToken_WithUsername() {
        // 执行测试
        String token = jwtTokenProvider.generateToken("testuser");

        // 验证结果
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // JWT 格式: header.payload.signature
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("根据 Authentication 生成 Token")
    void generateToken_WithAuthentication() {
        // 准备模拟数据
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 执行测试
        String token = jwtTokenProvider.generateToken(authentication);

        // 验证结果
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("从 Token 中获取用户名")
    void getUsernameFromToken() {
        // 生成 Token
        String token = jwtTokenProvider.generateToken("testuser");

        // 执行测试
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // 验证结果
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("验证有效 Token")
    void validateToken_Valid() {
        // 生成 Token
        String token = jwtTokenProvider.generateToken("testuser");

        // 执行测试
        boolean isValid = jwtTokenProvider.validateToken(token);

        // 验证结果
        assertTrue(isValid);
    }

    @Test
    @DisplayName("验证无效签名的 Token")
    void validateToken_InvalidSignature() {
        // 生成 Token
        String token = jwtTokenProvider.generateToken("testuser");
        // 篡改签名
        String tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "invalid_signature";

        // 执行测试
        boolean isValid = jwtTokenProvider.validateToken(tamperedToken);

        // 验证结果
        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证格式错误的 Token")
    void validateToken_MalformedToken() {
        // 执行测试
        boolean isValid = jwtTokenProvider.validateToken("not.a.valid.jwt");

        // 验证结果
        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证空 Token")
    void validateToken_EmptyToken() {
        // 执行测试
        boolean isValid = jwtTokenProvider.validateToken("");

        // 验证结果
        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证过期 Token")
    void validateToken_ExpiredToken() {
        // 创建一个过期时间为0的 Provider
        JwtTokenProvider expiredProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(expiredProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(expiredProvider, "jwtExpiration", 0L); // 立即过期

        // 生成 Token
        String token = expiredProvider.generateToken("testuser");

        // 验证 Token（应该已过期）
        boolean isValid = jwtTokenProvider.validateToken(token);

        // 验证结果
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Token 生成的一致性")
    void generateToken_Consistency() {
        // 生成两个 Token
        String token1 = jwtTokenProvider.generateToken("testuser");
        String token2 = jwtTokenProvider.generateToken("testuser");

        // 两个 Token 可能相同（如果在同一毫秒内生成）或不同
        // 但都应该可以正确解析用户名
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token1));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token2));

        // 两个 Token 都应该是有效的
        assertTrue(jwtTokenProvider.validateToken(token1));
        assertTrue(jwtTokenProvider.validateToken(token2));
    }

    @Test
    @DisplayName("不同用户生成不同 Token")
    void generateToken_DifferentUsers() {
        // 生成两个用户的 Token
        String token1 = jwtTokenProvider.generateToken("user1");
        String token2 = jwtTokenProvider.generateToken("user2");

        // Token 应该不同
        assertNotEquals(token1, token2);

        // 用户名应该正确
        assertEquals("user1", jwtTokenProvider.getUsernameFromToken(token1));
        assertEquals("user2", jwtTokenProvider.getUsernameFromToken(token2));
    }

    @Test
    @DisplayName("验证随机字符串 Token")
    void validateToken_RandomString() {
        // 执行测试
        boolean isValid = jwtTokenProvider.validateToken("random_string");

        // 验证结果
        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证 null Token")
    void validateToken_NullToken() {
        // validateToken 方法捕获 IllegalArgumentException 并返回 false
        boolean isValid = jwtTokenProvider.validateToken(null);

        // 验证结果
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Token 包含特殊字符用户名")
    void generateToken_SpecialCharacters() {
        // 生成包含特殊字符的用户名 Token
        String username = "user@example.com";
        String token = jwtTokenProvider.generateToken(username);

        // 验证结果
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(username, jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Token 包含中文用户名")
    void generateToken_ChineseCharacters() {
        // 生成包含中文的用户名 Token
        String username = "测试用户";
        String token = jwtTokenProvider.generateToken(username);

        // 验证结果
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(username, jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("验证使用不同密钥的 Token")
    void validateToken_DifferentSecret() {
        // 生成 Token
        String token = jwtTokenProvider.generateToken("testuser");

        // 创建使用不同密钥的 Provider
        JwtTokenProvider differentProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(differentProvider, "jwtSecret",
                "AnotherSecretKeyThatIsAtLeast256BitsLongForSecurityPurposes12345");
        ReflectionTestUtils.setField(differentProvider, "jwtExpiration", TEST_EXPIRATION);

        // 使用不同密钥验证 Token
        boolean isValid = differentProvider.validateToken(token);

        // 验证结果
        assertFalse(isValid);
    }
}
