# AI 服务配置诊断指南

## 问题：AI 服务暂不可用

### 可能的原因和解决方案：

## 1. 环境变量未设置 ⚠️

**检查方法：**
```bash
# Windows PowerShell
echo $env:AI_API_KEY
echo $env:AI_BASE_URL
echo $env:AI_MODEL

# Windows CMD
echo %AI_API_KEY%
echo %AI_BASE_URL%
echo %AI_MODEL%
```

**解决方案：**

### 方法 A：创建 .env 文件（推荐）

在 `it-training-backend` 目录下创建 `.env` 文件：

```bash
# 复制示例文件
cp .env.example .env
```

然后编辑 `.env` 文件，填入您的配置：

```properties
# AI Service Configuration
AI_API_KEY=sk-your-actual-api-key-here
AI_BASE_URL=https://api.deepseek.com
AI_MODEL=deepseek-chat
```

### 方法 B：设置系统环境变量

**Windows PowerShell（临时，仅当前会话）：**
```powershell
$env:AI_API_KEY="sk-your-actual-api-key-here"
$env:AI_BASE_URL="https://api.deepseek.com"
$env:AI_MODEL="deepseek-chat"
```

**Windows 系统环境变量（永久）：**
1. 右键"此电脑" → 属性 → 高级系统设置
2. 环境变量 → 新建
3. 添加以下变量：
   - `AI_API_KEY` = `sk-your-actual-api-key-here`
   - `AI_BASE_URL` = `https://api.deepseek.com`
   - `AI_MODEL` = `deepseek-chat`

## 2. API Key 无效 🔑

**症状：**
- 返回 401 Unauthorized
- 返回 403 Forbidden

**解决方案：**
1. 检查 API Key 是否正确
2. 检查 API Key 是否过期
3. 检查 API Key 是否有足够的配额

**测试 API Key：**
```bash
# 使用 curl 测试（替换 YOUR_API_KEY）
curl https://api.deepseek.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -d '{
    "model": "deepseek-chat",
    "messages": [{"role": "user", "content": "Hello"}]
  }'
```

## 3. 网络连接问题 🌐

**症状：**
- Connection timeout
- Connection refused

**解决方案：**
1. 检查网络连接
2. 检查防火墙设置
3. 如果在国内，可能需要使用代理或中转服务

**测试网络连接：**
```bash
# 测试能否访问 API 服务器
curl -I https://api.deepseek.com
```

## 4. Base URL 配置错误 🔗

**常见错误：**
- ❌ `https://api.deepseek.com/v1` （多了 /v1）
- ✅ `https://api.deepseek.com` （正确）

**不同服务的正确 Base URL：**
```properties
# DeepSeek
AI_BASE_URL=https://api.deepseek.com

# OpenAI
AI_BASE_URL=https://api.openai.com

# 通义千问
AI_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1

# 智谱AI
AI_BASE_URL=https://open.bigmodel.cn/api/paas/v4

# Moonshot
AI_BASE_URL=https://api.moonshot.cn/v1
```

## 5. 应用未正确加载配置 ⚙️

**检查方法：**
查看应用启动日志，确认配置是否加载：

```
# 应该看到类似的日志
Auto-configuration 'OpenAiAutoConfiguration' enabled
```

**解决方案：**
1. 确保 `.env` 文件在正确的位置
2. 重启应用
3. 清理并重新编译：
   ```bash
   mvn clean package -DskipTests
   ```

## 6. Spring AI 版本问题 📦

**当前版本：** `1.0.0-M4`

**可能的问题：**
- Milestone 版本可能不稳定
- API 可能有变化

**解决方案：**
如果问题持续，可以尝试升级到最新版本。

## 快速诊断步骤 🔍

### 步骤 1：检查环境变量
```bash
# PowerShell
echo $env:AI_API_KEY
```
如果输出为空，说明环境变量未设置。

### 步骤 2：检查 .env 文件
```bash
# 查看 .env 文件内容
cat .env
```
确保文件存在且包含正确的配置。

### 步骤 3：测试 API 连接
使用 Swagger UI 测试：
1. 访问 `http://localhost:8080/swagger-ui.html`
2. 找到 "AI服务测试" 分组
3. 调用 `GET /api/v1/ai/test/health` 接口
4. 查看返回结果

### 步骤 4：查看应用日志
查看后端日志中是否有错误信息：
```
AI 服务不可用: xxx
```

## 推荐配置（DeepSeek） 🌟

```properties
# .env 文件
AI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
AI_BASE_URL=https://api.deepseek.com
AI_MODEL=deepseek-chat
AI_RECOMMEND_ENABLED=true
AI_RECOMMEND_TIMEOUT=10000
AI_RECOMMEND_FALLBACK=true
```

## 测试 AI 服务 ✅

### 使用 Swagger UI：
1. 访问：`http://localhost:8080/swagger-ui.html`
2. 展开 "AI服务测试" 分组
3. 测试 `/api/v1/ai/test/health` 接口

**期望返回：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "available": true,
    "model": "deepseek-chat"
  }
}
```

### 使用 curl：
```bash
curl http://localhost:8080/api/v1/ai/test/health
```

## 常见错误信息 ❌

### 错误 1：API Key 未设置
```
Could not resolve placeholder 'AI_API_KEY'
```
**解决：** 设置 `AI_API_KEY` 环境变量

### 错误 2：连接超时
```
Connection timeout
```
**解决：** 检查网络连接和防火墙

### 错误 3：401 Unauthorized
```
401 Unauthorized
```
**解决：** 检查 API Key 是否正确

### 错误 4：403 Forbidden
```
403 Forbidden
```
**解决：** 检查 API Key 权限和配额

## 获取 API Key 🔑

### DeepSeek：
1. 访问：https://platform.deepseek.com
2. 注册/登录账号
3. 进入 API Keys 页面
4. 创建新的 API Key

### OpenAI：
1. 访问：https://platform.openai.com
2. 注册/登录账号
3. 进入 API Keys 页面
4. 创建新的 API Key

## 需要帮助？ 💬

如果以上方法都无法解决问题，请提供以下信息：

1. 环境变量设置情况
2. 应用启动日志
3. API 测试返回的错误信息
4. 使用的 AI 服务提供商

---

**提示：** 最常见的问题是忘记设置 `AI_API_KEY` 环境变量！
