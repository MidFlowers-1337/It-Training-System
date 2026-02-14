package com.itts.common.config;

import com.itts.common.security.CustomAccessDeniedHandler;
import com.itts.common.security.JwtAuthenticationEntryPoint;
import com.itts.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（使用 JWT 不需要）
                .csrf(AbstractHttpConfigurer::disable)
                // 无状态会话
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置权限
                .authorizeHttpRequests(auth -> auth
                        // 公开接口 - 精确匹配认证端点，不再使用通配符
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()

                        // 讲师专属接口（路径必须与 SessionController @GetMapping("/my") 一致）
                        .requestMatchers("/api/v1/sessions/my").hasRole("INSTRUCTOR")

                        // 学生可访问的课程相关接口（必须在管理员规则之前）
                        .requestMatchers(HttpMethod.POST, "/api/v1/courses/chapters/*/progress").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/courses/chapters/*/complete").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/courses/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/**").authenticated()

                        // 管理员专属接口
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/sessions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/sessions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/sessions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/sessions/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/stats/**").hasRole("ADMIN")

                        // 学员专属接口
                        .requestMatchers("/api/v1/ai/recommend").hasRole("STUDENT")

                        // AI 测试接口只允许管理员访问
                        .requestMatchers("/api/v1/ai/test/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/enrollments/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/enrollments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/enrollments").hasRole("STUDENT")

                        // 学生专属业务接口（学习中心、打卡、计划、成就、推荐、报告、画像）
                        .requestMatchers("/api/v1/learning/**").hasRole("STUDENT")
                        .requestMatchers("/api/v1/student/**").hasRole("STUDENT")

                        // 其他需要认证
                        .anyRequest().authenticated()
                )
                // 异常处理：返回 JSON 而非默认 HTML 页面
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
