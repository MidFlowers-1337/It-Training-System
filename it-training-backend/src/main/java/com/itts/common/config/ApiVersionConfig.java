package com.itts.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API 版本控制配置
 * <p>
 * 为所有 API 添加版本前缀，例如: /api/users -> /api/v1/users
 * <p>
 * 启用方式: 在 application.yaml 中设置 api.versioning.enabled=true
 * <p>
 * 注意：启用后需要同步修改前端的 API 调用路径
 */
@Configuration
@ConditionalOnProperty(name = "api.versioning.enabled", havingValue = "true")
public class ApiVersionConfig implements WebMvcConfigurer {

    /**
     * API 版本号
     */
    private static final String API_VERSION = "v1";

    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        // 为所有 Controller 添加 /api/v1 前缀
        configurer.addPathPrefix("/api/" + API_VERSION,
            c -> c.getPackage().getName().startsWith("com.itts.modules"));
    }
}
