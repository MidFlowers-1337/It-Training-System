package com.itts.common.config;

import org.springframework.context.annotation.Configuration;

/**
 * OpenAI Compatible API 配置
 * 支持所有符合 OpenAI API 格式的 AI 服务，包括：
 * - OpenAI (GPT-3.5, GPT-4)
 * - DeepSeek
 * - 通义千问 (Qwen)
 * - 智谱AI (GLM)
 * - Moonshot (Kimi)
 * - 其他 OpenAI 兼容服务
 *
 * 配置说明：
 * Spring AI 会自动根据 application.yaml 中的配置创建所需的 Bean：
 * - OpenAiApi: OpenAI API 客户端
 * - OpenAiChatModel: 聊天模型
 * - ChatClient.Builder: 聊天客户端构建器
 *
 * 只需在 application.yaml 或 .env 中配置以下参数：
 * - spring.ai.openai.api-key: API 密钥
 * - spring.ai.openai.base-url: 服务基础 URL
 * - spring.ai.openai.chat.options.model: 模型名称
 * - spring.ai.openai.chat.options.temperature: 温度参数
 * - spring.ai.openai.chat.options.max-tokens: 最大 token 数
 */
@Configuration
public class OpenAiConfig {
    // Spring AI 自动配置会创建所有必要的 Bean
    // 无需手动配置
}
