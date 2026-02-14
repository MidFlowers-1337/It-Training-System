package com.itts.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流拦截器
 * <p>
 * 基于Redis实现滑动窗口限流，保护高频接口
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 限流窗口时间（秒）
     */
    @Value("${app.rate-limit.window-seconds:60}")
    private int windowSeconds;

    /**
     * 窗口内最大请求数
     */
    @Value("${app.rate-limit.max-requests:100}")
    private int maxRequests;

    /**
     * 认证端点独立限流阈值（请求/分钟）
     */
    @Value("${app.rate-limit.auth-max-requests:10}")
    private int authMaxRequests;

    /**
     * 是否信任代理 X-Forwarded-For 头
     */
    @Value("${app.rate-limit.trust-proxy:false}")
    private boolean trustProxy;

    /**
     * Redis key 前缀
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    /**
     * Redis Lua 限流脚本（原子化 INCR + EXPIRE）
     * KEYS[1] = 限流key
     * ARGV[1] = 窗口过期时间（秒）
     * 返回当前计数值
     */
    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT;

    static {
        RATE_LIMIT_SCRIPT = new DefaultRedisScript<>();
        RATE_LIMIT_SCRIPT.setScriptText(
                "local count = redis.call('incr', KEYS[1]) " +
                "if count == 1 then redis.call('expire', KEYS[1], ARGV[1]) end " +
                "return count"
        );
        RATE_LIMIT_SCRIPT.setResultType(Long.class);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只限制特定接口
        String uri = request.getRequestURI();
        if (!shouldRateLimit(uri)) {
            return true;
        }

        String clientIp = getClientIp(request);
        String key = RATE_LIMIT_KEY_PREFIX + clientIp + ":" + uri;

        // 判断是否为认证端点，使用独立阈值
        boolean isAuthEndpoint = isAuthEndpoint(uri);
        int effectiveMaxRequests = isAuthEndpoint ? authMaxRequests : maxRequests;

        // 使用 Lua 脚本原子化 INCR + EXPIRE，避免竞态条件
        Long count = stringRedisTemplate.execute(
                RATE_LIMIT_SCRIPT,
                Collections.singletonList(key),
                String.valueOf(windowSeconds)
        );

        if (count == null) {
            count = 1L;
        }

        // 添加 Rate Limit 响应头
        long remaining = Math.max(0, effectiveMaxRequests - count);
        response.setHeader("X-RateLimit-Limit", String.valueOf(effectiveMaxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));

        // 检查是否超过限制
        if (count > effectiveMaxRequests) {
            log.warn("接口限流触发: ip={}, uri={}, count={}, limit={}", clientIp, uri, count, effectiveMaxRequests);
            // 获取 key 剩余 TTL 作为 Retry-After
            Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
            response.setHeader("Retry-After", String.valueOf(ttl != null && ttl > 0 ? ttl : windowSeconds));
            writeErrorResponse(response);
            return false;
        }

        return true;
    }

    /**
     * 判断是否为认证端点（使用独立限流阈值）
     */
    private boolean isAuthEndpoint(String uri) {
        return uri.startsWith("/api/v1/auth/login")
                || uri.startsWith("/api/v1/auth/register");
    }

    /**
     * 判断是否需要限流的接口
     */
    private boolean shouldRateLimit(String uri) {
        // 高频敏感接口列表
        return uri.startsWith("/api/v1/auth/login")
                || uri.startsWith("/api/v1/auth/register")
                || uri.startsWith("/api/v1/enrollments")
                || uri.startsWith("/api/v1/ai/recommend");
    }

    /**
     * 获取客户端真实IP
     * 当 trust-proxy=false 时，只使用 request.getRemoteAddr()，防止 IP 伪造
     * 当 trust-proxy=true 时，读取代理头但取最后一个非内网 IP
     */
    private String getClientIp(HttpServletRequest request) {
        if (!trustProxy) {
            return request.getRemoteAddr();
        }

        // 信任代理模式：从 X-Forwarded-For 取最后一个非内网 IP
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            String[] ips = xForwardedFor.split(",");
            // 从右向左找第一个非私有地址（最后添加的、最可信的代理记录）
            for (int i = ips.length - 1; i >= 0; i--) {
                String ip = ips[i].trim();
                if (!isPrivateIp(ip)) {
                    return ip;
                }
            }
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty() && !"unknown".equalsIgnoreCase(realIp)) {
            return realIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * 判断是否为内网/私有 IP 地址
     */
    private boolean isPrivateIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return true;
        }
        return ip.startsWith("10.")
                || ip.startsWith("172.16.") || ip.startsWith("172.17.") || ip.startsWith("172.18.")
                || ip.startsWith("172.19.") || ip.startsWith("172.20.") || ip.startsWith("172.21.")
                || ip.startsWith("172.22.") || ip.startsWith("172.23.") || ip.startsWith("172.24.")
                || ip.startsWith("172.25.") || ip.startsWith("172.26.") || ip.startsWith("172.27.")
                || ip.startsWith("172.28.") || ip.startsWith("172.29.") || ip.startsWith("172.30.")
                || ip.startsWith("172.31.")
                || ip.startsWith("192.168.")
                || ip.equals("127.0.0.1")
                || ip.equals("0:0:0:0:0:0:0:1")
                || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 写入限流错误响应
     */
    private void writeErrorResponse(HttpServletResponse response) throws Exception {
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("code", ErrorCode.RATE_LIMIT_EXCEEDED.getCode());
        result.put("message", ErrorCode.RATE_LIMIT_EXCEEDED.getMessage());
        result.put("data", null);

        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
    }
}
