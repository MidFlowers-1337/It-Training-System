package com.itts.modules.ai.controller;

import com.itts.common.ai.OpenAiService;
import com.itts.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 服务测试控制器
 * 用于测试 OpenAI Compatible API 集成
 */
@Slf4j
@Tag(name = "AI服务测试", description = "测试 OpenAI Compatible API 集成")
@RestController
@RequestMapping("/api/v1/ai/test")
@RequiredArgsConstructor
public class AiTestController {

    private final OpenAiService openAiService;

    @Operation(summary = "健康检查", description = "检查 AI 服务是否可用")
    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("available", openAiService.isAvailable());
        result.put("model", openAiService.getModelName());
        return R.ok(result);
    }

    @Operation(summary = "简单对话测试", description = "测试基本的 AI 对话功能")
    @PostMapping("/chat")
    public R<String> chat(@RequestBody ChatRequest request) {
        log.info("AI 对话测试: {}", request.getPrompt());
        String response = openAiService.chat(request.getPrompt());
        return R.ok(response);
    }

    @Operation(summary = "带系统提示的对话测试", description = "测试带系统提示的 AI 对话")
    @PostMapping("/chat-with-system")
    public R<String> chatWithSystem(@RequestBody ChatWithSystemRequest request) {
        log.info("AI 对话测试 (带系统提示): {}", request.getUserPrompt());
        String response = openAiService.chat(request.getSystemPrompt(), request.getUserPrompt());
        return R.ok(response);
    }

    @Operation(summary = "流式对话测试", description = "测试流式 AI 对话（SSE）")
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<String> streamChat(@RequestParam String prompt) {
        log.info("AI 流式对话测试: {}", prompt);
        return openAiService.streamChat(prompt);
    }

    @Operation(summary = "JSON 格式响应测试", description = "测试 AI 返回 JSON 格式数据")
    @PostMapping("/chat-json")
    public R<TestJsonResponse> chatJson(@RequestBody ChatRequest request) {
        log.info("AI JSON 对话测试: {}", request.getPrompt());

        String systemPrompt = """
                你是一个课程推荐助手。请根据用户的学习目标，推荐3门课程。
                返回 JSON 格式，包含以下字段：
                - courses: 课程名称数组
                - reason: 推荐理由
                """;

        TestJsonResponse response = openAiService.chatForJson(
                systemPrompt,
                request.getPrompt(),
                TestJsonResponse.class
        );

        return R.ok(response);
    }

    @Operation(summary = "自定义参数测试", description = "测试自定义温度和最大 token 数")
    @PostMapping("/chat-with-options")
    public R<String> chatWithOptions(@RequestBody ChatWithOptionsRequest request) {
        log.info("AI 对话测试 (自定义参数): temperature={}, maxTokens={}",
                request.getTemperature(), request.getMaxTokens());

        String response = openAiService.chatWithOptions(
                request.getSystemPrompt(),
                request.getUserPrompt(),
                request.getTemperature(),
                request.getMaxTokens()
        );

        return R.ok(response);
    }

    // ==================== DTO 类 ====================

    @Data
    public static class ChatRequest {
        private String prompt;
    }

    @Data
    public static class ChatWithSystemRequest {
        private String systemPrompt;
        private String userPrompt;
    }

    @Data
    public static class ChatWithOptionsRequest {
        private String systemPrompt;
        private String userPrompt;
        private Double temperature;
        private Integer maxTokens;
    }

    @Data
    public static class TestJsonResponse {
        private String[] courses;
        private String reason;
    }
}
