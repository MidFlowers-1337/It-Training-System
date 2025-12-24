package com.itts.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * AI 客户端配置
 * 配置 AI 调用的超时时间等参数
 */
@Configuration
public class AiClientConfig {

    @Value("${ai.recommend.timeout:30000}")
    private int aiTimeout;

    /**
     * 配置 RestClient.Builder，设置 AI 调用超时时间
     * 此 Bean 会被 Spring AI 自动使用
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(aiTimeout))
                .withReadTimeout(Duration.ofMillis(aiTimeout));

        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);

        return RestClient.builder()
                .requestFactory(requestFactory);
    }
}
