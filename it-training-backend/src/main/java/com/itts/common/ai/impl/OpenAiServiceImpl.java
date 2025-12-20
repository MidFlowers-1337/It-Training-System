package com.itts.common.ai.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itts.common.ai.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OpenAI Compatible AI 服务实现
 * 支持所有符合 OpenAI API 格式的服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final ChatClient.Builder chatClientBuilder;
    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.openai.chat.options.model:gpt-3.5-turbo}")
    private String modelName;

    @Value("${spring.ai.openai.chat.options.temperature:0.7}")
    private Double defaultTemperature;

    @Value("${spring.ai.openai.chat.options.max-tokens:2000}")
    private Integer defaultMaxTokens;

    @Override
    public String chat(String prompt) {
        try {
            log.debug("AI 对话请求: {}", prompt);

            ChatClient chatClient = chatClientBuilder.build();
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.debug("AI 响应: {}", response);
            return response;
        } catch (Exception e) {
            log.error("AI 对话失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        try {
            log.debug("AI 对话请求 - System: {}, User: {}", systemPrompt, userPrompt);

            ChatClient chatClient = chatClientBuilder.build();
            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            log.debug("AI 响应: {}", response);
            return response;
        } catch (Exception e) {
            log.error("AI 对话失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String chat(List<Map<String, String>> messages) {
        try {
            log.debug("AI 多轮对话请求，消息数: {}", messages.size());

            List<Message> messageList = new ArrayList<>();
            for (Map<String, String> msg : messages) {
                String role = msg.get("role");
                String content = msg.get("content");

                if ("system".equals(role)) {
                    messageList.add(new SystemMessage(content));
                } else if ("user".equals(role)) {
                    messageList.add(new UserMessage(content));
                }
            }

            Prompt prompt = new Prompt(messageList);
            String response = chatModel.call(prompt).getResult().getOutput().getContent();

            log.debug("AI 响应: {}", response);
            return response;
        } catch (Exception e) {
            log.error("AI 多轮对话失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Flux<String> streamChat(String prompt) {
        try {
            log.debug("AI 流式对话请求: {}", prompt);

            ChatClient chatClient = chatClientBuilder.build();
            return chatClient.prompt()
                    .user(prompt)
                    .stream()
                    .content();
        } catch (Exception e) {
            log.error("AI 流式对话失败: {}", e.getMessage(), e);
            return Flux.error(new RuntimeException("AI 服务调用失败: " + e.getMessage(), e));
        }
    }

    @Override
    public String chatWithOptions(String systemPrompt, String userPrompt, Double temperature, Integer maxTokens) {
        try {
            log.debug("AI 对话请求（自定义参数） - Temperature: {}, MaxTokens: {}", temperature, maxTokens);

            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .withModel(modelName)
                    .withTemperature(temperature != null ? temperature : defaultTemperature)
                    .withMaxTokens(maxTokens != null ? maxTokens : defaultMaxTokens)
                    .build();

            List<Message> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.add(new SystemMessage(systemPrompt));
            }
            messages.add(new UserMessage(userPrompt));

            Prompt prompt = new Prompt(messages, options);
            String response = chatModel.call(prompt).getResult().getOutput().getContent();

            log.debug("AI 响应: {}", response);
            return response;
        } catch (Exception e) {
            log.error("AI 对话失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> T chatForJson(String systemPrompt, String userPrompt, Class<T> responseClass) {
        try {
            log.debug("AI JSON 对话请求 - ResponseClass: {}", responseClass.getSimpleName());

            // 增强系统提示，要求返回 JSON
            String enhancedSystemPrompt = systemPrompt +
                    "\n\n请严格按照 JSON 格式返回结果，不要包含任何 markdown 代码块标记（如 ```json）。";

            String response = chat(enhancedSystemPrompt, userPrompt);

            // 清理可能的 markdown 标记
            String cleanResponse = response
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            // 解析 JSON
            T result = objectMapper.readValue(cleanResponse, responseClass);
            log.debug("JSON 解析成功");

            return result;
        } catch (Exception e) {
            log.error("AI JSON 对话失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 服务调用或 JSON 解析失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            // 发送简单测试请求
            String response = chat("测试");
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            log.warn("AI 服务不可用: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getModelName() {
        return modelName;
    }
}
