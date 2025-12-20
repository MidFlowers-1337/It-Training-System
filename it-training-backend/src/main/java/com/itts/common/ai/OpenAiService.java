package com.itts.common.ai;

import java.util.List;
import java.util.Map;

/**
 * OpenAI Compatible AI 服务接口
 * 提供统一的 AI 调用接口，支持所有 OpenAI 兼容的服务
 */
public interface OpenAiService {

    /**
     * 简单对话
     *
     * @param prompt 用户输入
     * @return AI 响应
     */
    String chat(String prompt);

    /**
     * 带系统提示的对话
     *
     * @param systemPrompt 系统提示
     * @param userPrompt 用户输入
     * @return AI 响应
     */
    String chat(String systemPrompt, String userPrompt);

    /**
     * 多轮对话
     *
     * @param messages 消息列表，每个消息包含 role 和 content
     * @return AI 响应
     */
    String chat(List<Map<String, String>> messages);

    /**
     * 流式对话
     *
     * @param prompt 用户输入
     * @return 流式响应
     */
    reactor.core.publisher.Flux<String> streamChat(String prompt);

    /**
     * 带参数的对话
     *
     * @param systemPrompt 系统提示
     * @param userPrompt 用户输入
     * @param temperature 温度参数 (0.0-2.0)
     * @param maxTokens 最大 token 数
     * @return AI 响应
     */
    String chatWithOptions(String systemPrompt, String userPrompt, Double temperature, Integer maxTokens);

    /**
     * JSON 格式响应
     * 要求 AI 返回 JSON 格式的数据
     *
     * @param systemPrompt 系统提示
     * @param userPrompt 用户输入
     * @param responseClass 响应类型
     * @return 解析后的对象
     */
    <T> T chatForJson(String systemPrompt, String userPrompt, Class<T> responseClass);

    /**
     * 检查 AI 服务是否可用
     *
     * @return true 如果服务可用
     */
    boolean isAvailable();

    /**
     * 获取当前使用的模型名称
     *
     * @return 模型名称
     */
    String getModelName();
}
