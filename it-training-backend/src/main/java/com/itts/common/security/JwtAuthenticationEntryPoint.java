package com.itts.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.response.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 认证入口点
 * <p>
 * 当未认证的请求访问受保护资源时，返回 JSON 格式的 401 响应，
 * 而非 Spring Security 默认的 HTML 错误页面。
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("未认证访问: {} {}, 原因: {}", request.getMethod(), request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        R<Void> body = R.fail(401, "未登录或登录已过期，请重新登录");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
