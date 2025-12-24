package com.itts.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis缓存配置
 * 用于配置Spring Cache与Redis的集成
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    /**
     * 缓存名称常量
     */
    public static final String CACHE_COURSES = "courses";
    public static final String CACHE_COURSE_LIST = "courseList";
    public static final String CACHE_SESSIONS = "sessions";
    public static final String CACHE_SESSION_LIST = "sessionList";
    public static final String CACHE_STATS = "stats";
    public static final String CACHE_HOT_RANKING = "hotRanking";

    /**
     * 缓存TTL配置（分钟）
     */
    private static final long TTL_COURSE = 30;          // 课程详情：30分钟
    private static final long TTL_COURSE_LIST = 10;     // 课程列表：10分钟
    private static final long TTL_SESSION = 15;         // 班期详情：15分钟
    private static final long TTL_SESSION_LIST = 10;    // 班期列表：10分钟
    private static final long TTL_STATS = 5;            // 统计数据：5分钟
    private static final long TTL_HOT_RANKING = 10;     // 热门排行：10分钟
    private static final long TTL_DEFAULT = 30;         // 默认：30分钟

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(TTL_DEFAULT))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // 各缓存自定义TTL配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CACHE_COURSES, defaultConfig.entryTtl(Duration.ofMinutes(TTL_COURSE)));
        cacheConfigurations.put(CACHE_COURSE_LIST, defaultConfig.entryTtl(Duration.ofMinutes(TTL_COURSE_LIST)));
        cacheConfigurations.put(CACHE_SESSIONS, defaultConfig.entryTtl(Duration.ofMinutes(TTL_SESSION)));
        cacheConfigurations.put(CACHE_SESSION_LIST, defaultConfig.entryTtl(Duration.ofMinutes(TTL_SESSION_LIST)));
        cacheConfigurations.put(CACHE_STATS, defaultConfig.entryTtl(Duration.ofMinutes(TTL_STATS)));
        cacheConfigurations.put(CACHE_HOT_RANKING, defaultConfig.entryTtl(Duration.ofMinutes(TTL_HOT_RANKING)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
