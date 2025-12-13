package com.itts.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * 跨域配置
 *
 * 安全说明：
 * - 开发环境：允许 localhost 的常用端口
 * - 生产环境：通过环境变量 CORS_ALLOWED_ORIGINS 配置允许的来源
 *
 * 环境变量配置示例：
 * CORS_ALLOWED_ORIGINS=https://your-domain.com,https://www.your-domain.com
 */
@Configuration
public class CorsConfig {

    /**
     * 允许的来源列表，通过环境变量配置
     * 开发环境默认允许 localhost 的常用端口
     */
    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173,http://127.0.0.1:3000}")
    private String allowedOrigins;

    /**
     * 当前环境配置，用于区分开发和生产环境
     */
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 配置允许的来源
        configureAllowedOrigins(config);
        
        // 允许携带 Cookie 和认证信息
        config.setAllowCredentials(true);
        
        // 限制允许的请求头（只允许必要的头）
        config.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Cache-Control"
        ));
        
        // 限制允许的请求方法
        config.setAllowedMethods(Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()
        ));
        
        // 暴露响应头（允许前端读取）
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition"
        ));
        
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    /**
     * 配置允许的来源
     *
     * @param config CORS 配置对象
     */
    private void configureAllowedOrigins(CorsConfiguration config) {
        // 解析环境变量中的来源列表
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        
        for (String origin : origins) {
            String trimmedOrigin = origin.trim();
            if (!trimmedOrigin.isEmpty()) {
                config.addAllowedOrigin(trimmedOrigin);
            }
        }
        
        // 如果是开发环境且没有配置任何来源，添加默认的开发环境来源
        if (config.getAllowedOrigins() == null || config.getAllowedOrigins().isEmpty()) {
            if ("dev".equals(activeProfile) || "development".equals(activeProfile)) {
                config.addAllowedOrigin("http://localhost:5173");
                config.addAllowedOrigin("http://localhost:3000");
            }
        }
    }
}
