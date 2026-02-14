package com.itts.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.response.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义访问拒绝处理器
 * <p>
 * 当已认证用户访问无权限资源时，返回 JSON 格式的 403 响应，
 * 而非 Spring Security 默认的 HTML 错误页面。
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("访问被拒绝: {} {}, 原因: {}", request.getMethod(), request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        R<Void> body = R.fail(403, "没有权限访问该资源");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
