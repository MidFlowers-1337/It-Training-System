# OpenAI Compatible AI 集成指南

## 概述

本系统已集成 OpenAI Compatible API，支持所有符合 OpenAI API 格式的 AI 服务。

## 支持的 AI 服务

- **OpenAI** (GPT-3.5, GPT-4)
- **DeepSeek** (deepseek-chat, deepseek-coder)
- **通义千问** (Qwen)
- **智谱AI** (GLM)
- **Moonshot** (Kimi)
- **其他 OpenAI 兼容服务**

## 配置步骤

### 1. 环境变量配置

复制 `.env.example` 为 `.env`，并填写以下配置：

```bash
# AI API Key (必填)
AI_API_KEY=your-api-key-here

# AI 服务基础 URL (必填)
AI_BASE_URL=https://api.deepseek.com

# AI 模型名称 (必填)
AI_MODEL=deepseek-chat

# AI 推荐功能开关 (可选，默认: true)
AI_RECOMMEND_ENABLED=true

# AI 请求超时时间，单位毫秒 (可选，默认: 10000)
AI_RECOMMEND_TIMEOUT=10000

# AI 服务失败时是否使用降级策略 (可选，默认: true)
AI_RECOMMEND_FALLBACK=true
```

### 2. 不同服务的配置示例

#### OpenAI

```bash
AI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
AI_BASE_URL=https://api.openai.com
AI_MODEL=gpt-3.5-turbo
```

#### DeepSeek

```bash
AI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
AI_BASE_URL=https://api.deepseek.com
AI_MODEL=deepseek-chat
```

#### 通义千问

```bash
AI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
AI_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
AI_MODEL=qwen-turbo
```

#### 智谱AI

```bash
AI_API_KEY=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.xxxxxx
AI_BASE_URL=https://open.bigmodel.cn/api/paas/v4
AI_MODEL=glm-4
```

#### Moonshot

```bash
AI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
AI_BASE_URL=https://api.moonshot.cn/v1
AI_MODEL=moonshot-v1-8k
```

## API 测试

系统提供了完整的测试接口，可以通过 Swagger UI 进行测试：

访问：`http://localhost:8080/swagger-ui.html`

### 测试接口列表

1. **健康检查**
   - `GET /api/v1/ai/test/health`
   - 检查 AI 服务是否可用

2. **简单对话测试**
   - `POST /api/v1/ai/test/chat`
   - 测试基本的 AI 对话功能

3. **带系统提示的对话测试**
   - `POST /api/v1/ai/test/chat-with-system`
   - 测试带系统提示的 AI 对话

4. **流式对话测试**
   - `GET /api/v1/ai/test/stream`
   - 测试流式 AI 对话（SSE）

5. **JSON 格式响应测试**
   - `POST /api/v1/ai/test/chat-json`
   - 测试 AI 返回 JSON 格式数据

6. **自定义参数测试**
   - `POST /api/v1/ai/test/chat-with-options`
   - 测试自定义温度和最大 token 数

### 使用 curl 测试

```bash
# 健康检查
curl -X GET http://localhost:8080/api/v1/ai/test/health

# 简单对话
curl -X POST http://localhost:8080/api/v1/ai/test/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt": "你好，请介绍一下自己"}'

# 带系统提示的对话
curl -X POST http://localhost:8080/api/v1/ai/test/chat-with-system \
  -H "Content-Type: application/json" \
  -d '{
    "systemPrompt": "你是一个IT培训课程推荐助手",
    "userPrompt": "我想学习Java后端开发"
  }'
```

## 代码使用示例

### 注入 OpenAiService

```java
@Service
@RequiredArgsConstructor
public class YourService {

    private final OpenAiService openAiService;

    public void example() {
        // 简单对话
        String response = openAiService.chat("你好");

        // 带系统提示的对话
        String response2 = openAiService.chat(
            "你是一个课程推荐助手",
            "我想学习前端开发"
        );

        // JSON 格式响应
        MyResponse response3 = openAiService.chatForJson(
            "返回 JSON 格式",
            "推荐3门课程",
            MyResponse.class
        );

        // 流式对话
        Flux<String> stream = openAiService.streamChat("讲个故事");
    }
}
```

## 功能特性

### 1. 统一接口

所有 OpenAI 兼容的服务都使用相同的接口，切换服务只需修改配置。

### 2. 降级策略

当 AI 服务不可用时，系统会自动使用基于关键词匹配的降级策略，确保服务可用性。

### 3. 日志记录

所有 AI 推荐请求都会记录到 `ai_recommend_log` 表，包括：
- 用户输入
- 推荐结果
- 使用的模型
- 响应时间

### 4. 流式响应

支持 Server-Sent Events (SSE) 流式响应，适用于长文本生成场景。

### 5. JSON 格式化

自动处理 AI 返回的 JSON 数据，清理 markdown 标记，直接解析为 Java 对象。

## 故障排查

### 1. AI 服务不可用

检查：
- API Key 是否正确
- Base URL 是否正确
- 网络连接是否正常
- API 配额是否用完

### 2. 响应超时

调整超时时间：
```bash
AI_RECOMMEND_TIMEOUT=30000  # 30秒
```

### 3. JSON 解析失败

确保系统提示中明确要求返回 JSON 格式，并且不包含 markdown 标记。

## 安全建议

1. **不要在代码中硬编码 API Key**
2. **使用环境变量管理敏感信息**
3. **定期轮换 API Key**
4. **监控 API 使用量和成本**
5. **在生产环境启用请求限流**

## 性能优化

1. **合理设置 max-tokens**：避免生成过长的响应
2. **使用缓存**：对相同的请求使用缓存结果
3. **异步处理**：对非实时场景使用异步调用
4. **批量处理**：合并多个请求减少 API 调用次数

## 成本控制

1. **选择合适的模型**：根据任务复杂度选择模型
2. **优化提示词**：减少不必要的 token 消耗
3. **设置使用限额**：防止意外的高额费用
4. **监控使用情况**：定期检查 API 使用统计

## 更新日志

### 2025-12-20
- ✅ 重命名 Flyway 脚本 V1.3 为 V9
- ✅ 重命名 Flyway 脚本 V1.4 为 V10
- ✅ 创建 OpenAI Compatible AI 配置类
- ✅ 实现通用 AI 服务接口
- ✅ 创建 AI 测试控制器
- ✅ 更新 .env.example 添加详细配置说明

## 技术支持

如有问题，请查看：
- [Spring AI 文档](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API 文档](https://platform.openai.com/docs/api-reference)
- 项目 Issue 跟踪
