package com.itts.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
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
     * Redis key 前缀
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只限制特定接口
        String uri = request.getRequestURI();
        if (!shouldRateLimit(uri)) {
            return true;
        }

        String clientIp = getClientIp(request);
        String key = RATE_LIMIT_KEY_PREFIX + clientIp + ":" + uri;

        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count == null) {
            count = 1L;
        }

        // 第一次请求，设置过期时间
        if (count == 1) {
            stringRedisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }

        // 检查是否超过限制
        if (count > maxRequests) {
            log.warn("接口限流触发: ip={}, uri={}, count={}", clientIp, uri, count);
            writeErrorResponse(response);
            return false;
        }

        return true;
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
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 写入限流错误响应
     */
    private void writeErrorResponse(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
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
