package com.itts.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API 版本控制配置（已弃用）
 * <p>
 * 注意：当前项目所有 Controller 已在 @RequestMapping 中硬编码 /api/v1 前缀，
 * 因此本配置类即使启用也不会生效。保留此类仅作为未来版本控制的参考。
 * <p>
 * 当前 API 路径规范：
 * - 基础路径：/api/v1
 * - 示例：/api/v1/auth/login, /api/v1/users, /api/v1/courses
 * <p>
 * 如需启用动态版本控制，需要：
 * 1. 移除所有 Controller 中 @RequestMapping 的 /api/v1 前缀
 * 2. 在 application.yaml 中设置 api.versioning.enabled=true
 * 3. 同步修改前端的 baseURL 配置
 *
 * @deprecated 当前不使用，Controller 已硬编码路径前缀
 */
@Deprecated
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
