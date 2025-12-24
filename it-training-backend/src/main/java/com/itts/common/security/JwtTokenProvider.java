package com.itts.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token 工具类
 * <p>
 * 支持Token存入Redis实现主动失效功能
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Redis key 前缀
     */
    private static final String TOKEN_KEY_PREFIX = "jwt:token:";

    /**
     * 获取过期时间（毫秒）
     */
    public long getExpirationMs() {
        return jwtExpiration;
    }

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername());
    }

    /**
     * 根据用户名生成 Token
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        // 存入Redis，支持主动失效
        saveTokenToRedis(username, token);

        return token;
    }

    /**
     * 将Token存入Redis
     */
    private void saveTokenToRedis(String username, String token) {
        String key = TOKEN_KEY_PREFIX + username;
        stringRedisTemplate.opsForValue().set(key, token, jwtExpiration, TimeUnit.MILLISECONDS);
        log.debug("Token已存入Redis: {}", username);
    }

    /**
     * 使Token失效（登出、修改密码等场景）
     */
    public void invalidateToken(String username) {
        String key = TOKEN_KEY_PREFIX + username;
        Boolean deleted = stringRedisTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("Token已失效: {}", username);
        }
    }

    /**
     * 检查Token是否在Redis中有效
     */
    private boolean isTokenValidInRedis(String username, String token) {
        String key = TOKEN_KEY_PREFIX + username;
        String cachedToken = stringRedisTemplate.opsForValue().get(key);
        // 如果Redis中没有Token或Token不匹配，则认为已失效
        if (cachedToken == null) {
            log.debug("Token在Redis中不存在: {}", username);
            return false;
        }
        if (!cachedToken.equals(token)) {
            log.debug("Token与Redis中缓存不匹配: {}", username);
            return false;
        }
        return true;
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * 验证 Token（包含签名验证 + Redis有效性验证）
     */
    public boolean validateToken(String token) {
        try {
            // 1. 验证JWT签名和过期时间
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            // 2. 验证Token是否在Redis中有效（支持主动失效）
            String username = getUsernameFromToken(token);
            if (!isTokenValidInRedis(username, token)) {
                log.warn("Token已被主动失效: {}", username);
                return false;
            }

            return true;
        } catch (SignatureException e) {
            log.error("无效的JWT签名: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("无效的JWT格式: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims为空: {}", e.getMessage());
        }
        return false;
    }
}
