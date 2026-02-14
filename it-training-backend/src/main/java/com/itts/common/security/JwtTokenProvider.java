package com.itts.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
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

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    /** 缓存的签名密钥，避免每次调用 getSigningKey() 都重新创建 */
    private SecretKey cachedSigningKey;

    /**
     * 启动时校验 JWT Secret 长度，并缓存 SecretKey 实例
     * HMAC-SHA256 要求密钥至少 32 字节
     */
    @PostConstruct
    public void validateAndCacheSecret() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT Secret 长度不足！当前 " + keyBytes.length + " 字节，至少需要 32 字节。" +
                    "请通过环境变量 JWT_SECRET 设置一个安全的密钥。");
        }
        cachedSigningKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT Secret 校验通过 ({}字节)，签名密钥已缓存", keyBytes.length);
    }

    /**
     * Redis key 前缀
     */
    private static final String TOKEN_KEY_PREFIX = "jwt:token:";
    private static final String REFRESH_TOKEN_KEY_PREFIX = "jwt:refresh:";

    /**
     * 获取过期时间（毫秒）
     */
    public long getExpirationMs() {
        return jwtExpiration;
    }

    /**
     * 获取签名密钥（返回缓存实例）
     */
    private SecretKey getSigningKey() {
        return cachedSigningKey;
    }

    /**
     * 生成 Token（兼容旧调用方式，不携带 userId）
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername());
    }

    /**
     * 生成 Token（携带 userId）
     */
    public String generateToken(Authentication authentication, Long userId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername(), userId);
    }

    /**
     * 根据用户名生成 Token（不携带 userId，向后兼容）
     */
    public String generateToken(String username) {
        return generateToken(username, null);
    }

    /**
     * 根据用户名和用户ID生成 Token
     */
    public String generateToken(String username, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        var builder = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate);

        // 将 userId 写入 claims（如果提供）
        if (userId != null) {
            builder.claim("userId", userId);
        }

        String token = builder
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
     * 获取 Refresh Token 过期时间（毫秒）
     */
    public long getRefreshExpirationMs() {
        return refreshExpiration;
    }

    /**
     * 生成 Refresh Token
     */
    public String generateRefreshToken(String username, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        var builder = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("type", "refresh");

        if (userId != null) {
            builder.claim("userId", userId);
        }

        String refreshToken = builder
                .signWith(getSigningKey())
                .compact();

        // 存入 Redis
        String key = REFRESH_TOKEN_KEY_PREFIX + username;
        stringRedisTemplate.opsForValue().set(key, refreshToken, refreshExpiration, TimeUnit.MILLISECONDS);
        log.debug("Refresh Token已存入Redis: {}", username);

        return refreshToken;
    }

    /**
     * 验证 Refresh Token
     */
    public Claims validateRefreshToken(String refreshToken) {
        try {
            Claims claims = parseClaims(refreshToken);

            // 校验是否为 refresh token
            String type = claims.get("type", String.class);
            if (!"refresh".equals(type)) {
                log.warn("非Refresh Token类型");
                return null;
            }

            // 校验 Redis 中的 refresh token
            String username = claims.getSubject();
            String key = REFRESH_TOKEN_KEY_PREFIX + username;
            String cachedToken = stringRedisTemplate.opsForValue().get(key);
            if (cachedToken == null || !cachedToken.equals(refreshToken)) {
                log.warn("Refresh Token已失效或不匹配: {}", username);
                return null;
            }

            return claims;
        } catch (Exception e) {
            log.error("Refresh Token验证失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 使用 Refresh Token 刷新 Access Token
     *
     * @return 新的 Access Token，验证失败返回 null
     */
    public String refreshAccessToken(String refreshToken) {
        Claims claims = validateRefreshToken(refreshToken);
        if (claims == null) {
            return null;
        }

        String username = claims.getSubject();
        Long userId = claims.get("userId", Long.class);

        // 生成新的 Access Token
        return generateToken(username, userId);
    }

    /**
     * 使Token失效（登出、修改密码等场景）— 同时清除 Access Token 和 Refresh Token
     */
    public void invalidateToken(String username) {
        String accessKey = TOKEN_KEY_PREFIX + username;
        String refreshKey = REFRESH_TOKEN_KEY_PREFIX + username;

        stringRedisTemplate.delete(accessKey);
        stringRedisTemplate.delete(refreshKey);

        log.info("Token已全部失效: {}", username);
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
     * 解析 Token 获取 Claims（单次解析，供其他方法复用）
     *
     * @param token JWT token
     * @return Claims 对象
     * @throws JwtException 解析失败时抛出异常
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @return 用户ID，若 Token 中不含 userId claim 则返回 null（兼容旧 Token）
     */
    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * 从已解析的 Claims 中获取用户名（避免重复解析）
     */
    public String getUsernameFromClaims(Claims claims) {
        return claims.getSubject();
    }

    /**
     * 从已解析的 Claims 中获取用户ID（避免重复解析）
     */
    public Long getUserIdFromClaims(Claims claims) {
        return claims.get("userId", Long.class);
    }

    /**
     * 验证 Token（包含签名验证 + Redis有效性验证）
     */
    public boolean validateToken(String token) {
        try {
            // 1. 验证JWT签名和过期时间（一次解析）
            Claims claims = parseClaims(token);

            // 2. 验证Token是否在Redis中有效（支持主动失效）
            String username = claims.getSubject();
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

    /**
     * 验证 Token 并返回 Claims（Filter 专用，一次解析完成验证+提取）
     *
     * @return Claims 对象（验证通过时），null（验证失败时）
     */
    public Claims validateTokenAndGetClaims(String token) {
        try {
            // 一次解析完成签名验证和 Claims 提取
            Claims claims = parseClaims(token);

            // 验证Token是否在Redis中有效（支持主动失效）
            String username = claims.getSubject();
            if (!isTokenValidInRedis(username, token)) {
                log.warn("Token已被主动失效: {}", username);
                return null;
            }

            return claims;
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
        return null;
    }
}
