package com.itts.common.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
 * - 生产环境：通过环境变量 CORS_ALLOWED_ORIGINS 配置允许的来源（必须显式配置）
 *
 * 环境变量配置示例：
 * CORS_ALLOWED_ORIGINS=https://your-domain.com,https://www.your-domain.com
 */
@Slf4j
@Configuration
public class CorsConfig {

    /**
     * 开发环境默认允许的来源
     */
    private static final String DEV_DEFAULT_ORIGINS = "http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173,http://127.0.0.1:3000";

    /**
     * 允许的来源列表，通过环境变量配置
     * 生产环境不设置默认值，强制必须配置
     */
    @Value("${CORS_ALLOWED_ORIGINS:}")
    private String allowedOrigins;

    /**
     * 当前环境配置，用于区分开发和生产环境
     */
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * 启动时校验CORS配置
     */
    @PostConstruct
    public void validateCorsConfig() {
        boolean isProduction = isProductionProfile();

        if (isProduction && (allowedOrigins == null || allowedOrigins.trim().isEmpty())) {
            log.error("===========================================");
            log.error("CORS配置错误: 生产环境必须设置 CORS_ALLOWED_ORIGINS 环境变量！");
            log.error("示例: CORS_ALLOWED_ORIGINS=https://your-domain.com");
            log.error("===========================================");
            throw new IllegalStateException("生产环境必须配置 CORS_ALLOWED_ORIGINS 环境变量");
        }

        if (isProduction) {
            log.info("CORS配置已加载: {}", allowedOrigins);
        } else {
            String effectiveOrigins = (allowedOrigins == null || allowedOrigins.trim().isEmpty())
                    ? DEV_DEFAULT_ORIGINS : allowedOrigins;
            log.info("开发环境CORS配置: {}", effectiveOrigins);
        }
    }

    /**
     * 判断是否为生产环境
     */
    private boolean isProductionProfile() {
        if (activeProfile == null) {
            return false;
        }
        String profile = activeProfile.toLowerCase();
        return profile.contains("prod") || profile.contains("production");
    }

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
            HttpMethod.PATCH.name(),
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
        String effectiveOrigins;

        // 如果未配置且是开发环境，使用开发环境默认值
        if (allowedOrigins == null || allowedOrigins.trim().isEmpty()) {
            if (!isProductionProfile()) {
                effectiveOrigins = DEV_DEFAULT_ORIGINS;
            } else {
                // 生产环境在@PostConstruct已经校验过，这里不会执行
                return;
            }
        } else {
            effectiveOrigins = allowedOrigins;
        }

        // 安全校验：allowCredentials=true 时，origins 不能为 "*"
        if ("*".equals(effectiveOrigins.trim())) {
            log.error("CORS安全校验失败: allowCredentials=true 时，origins 不能为 '*'");
            throw new IllegalStateException(
                    "CORS配置错误: 当 allowCredentials=true 时，不允许使用通配符 '*' 作为 origin。" +
                    "请明确指定允许的域名列表。");
        }

        // 解析来源列表
        List<String> origins = Arrays.asList(effectiveOrigins.split(","));

        for (String origin : origins) {
            String trimmedOrigin = origin.trim();
            if (!trimmedOrigin.isEmpty()) {
                // 逐项校验不能为 "*"
                if ("*".equals(trimmedOrigin)) {
                    log.error("CORS安全校验失败: origins 列表中包含通配符 '*'");
                    throw new IllegalStateException(
                            "CORS配置错误: 当 allowCredentials=true 时，不允许使用通配符 '*' 作为 origin。");
                }
                config.addAllowedOrigin(trimmedOrigin);
            }
        }
    }
}
