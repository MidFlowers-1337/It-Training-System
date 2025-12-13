# IT技能培训智能选课系统 - 部署文档

> 版本: v1.0.0
> 最后更新: 2025-12-14

---

## 目录

1. [系统要求](#1-系统要求)
2. [Docker 部署（推荐）](#2-docker-部署推荐)
3. [本地开发部署](#3-本地开发部署)
4. [生产环境配置](#4-生产环境配置)
5. [常见问题](#5-常见问题)

---

## 1. 系统要求

### Docker 部署要求

- Docker 20.10+
- Docker Compose 2.0+
- 最小配置：2核 CPU，4GB 内存，20GB 存储

### 本地开发要求

- JDK 17+
- Node.js 18+
- pnpm 8+
- MySQL 8.0+ 或 TiDB
- Maven 3.8+（或使用项目自带的 mvnw）

---

## 2. Docker 部署（推荐）

### 2.1 快速启动

```bash
# 1. 克隆项目
git clone <repository-url>
cd It-Training-System

# 2. 复制环境变量配置
cp .env.example .env

# 3. 编辑 .env 文件，配置必要的环境变量
# 特别是：JWT_SECRET、AI_API_KEY

# 4. 一键启动所有服务
docker-compose up -d

# 5. 查看服务状态
docker-compose ps

# 6. 查看日志
docker-compose logs -f
```

### 2.2 服务访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端 | http://localhost | Web 应用入口 |
| 后端 API | http://localhost:8080 | REST API |
| Swagger | http://localhost:8080/swagger-ui.html | API 文档 |
| MySQL | localhost:3306 | 数据库（仅内部访问） |

### 2.3 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 讲师 | teacher1 | 123456 |
| 学员 | student1 | 123456 |

### 2.4 常用命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 重新构建镜像
docker-compose build --no-cache

# 进入容器
docker exec -it it-training-backend sh
docker exec -it it-training-mysql mysql -u itts -p
```

---

## 3. 本地开发部署

### 3.1 数据库准备

```bash
# 1. 启动 MySQL
# 2. 创建数据库
mysql -u root -p
CREATE DATABASE it_training CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 执行初始化脚本
mysql -u root -p it_training < docs/04-DB/schema.sql
```

### 3.2 后端启动

```bash
cd it-training-backend

# 1. 配置数据库连接
# 编辑 src/main/resources/application.yaml

# 2. 安装依赖并启动
./mvnw spring-boot:run

# 或使用 Maven
mvn spring-boot:run
```

### 3.3 前端启动

```bash
cd it-training-frontend

# 1. 安装依赖
pnpm install

# 2. 启动开发服务器
pnpm dev

# 3. 访问 http://localhost:5173
```

### 3.4 构建生产包

```bash
# 后端构建
cd it-training-backend
./mvnw clean package -DskipTests
# 生成：target/it-training-system-1.0.0-SNAPSHOT.jar

# 前端构建
cd it-training-frontend
pnpm build
# 生成：dist/
```

---

## 4. 生产环境配置

### 4.1 环境变量说明

| 变量 | 说明 | 默认值 | 必填 |
|------|------|--------|------|
| MYSQL_ROOT_PASSWORD | MySQL root 密码 | root123456 | 是 |
| MYSQL_USER | MySQL 用户名 | itts | 是 |
| MYSQL_PASSWORD | MySQL 密码 | itts123456 | 是 |
| JWT_SECRET | JWT 签名密钥 | - | 是 |
| JWT_EXPIRATION | Token 过期时间(ms) | 86400000 | 否 |
| AI_API_KEY | AI 服务 API Key | - | 是 |
| AI_BASE_URL | AI 服务地址 | https://api.deepseek.com | 否 |
| AI_MODEL | AI 模型名称 | deepseek-chat | 否 |

### 4.2 安全建议

1. **JWT 密钥**：生产环境必须使用强密码，至少 32 字符
2. **数据库密码**：使用复杂密码，定期更换
3. **HTTPS**：生产环境应配置 SSL 证书
4. **防火墙**：只开放必要端口（80/443）

### 4.3 性能优化

```yaml
# JVM 参数调整（Dockerfile 中的 JAVA_OPTS）
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# MySQL 配置优化
innodb_buffer_pool_size = 1G
max_connections = 500
```

### 4.4 监控与日志

- 健康检查：`GET /actuator/health`
- 日志位置：容器内 `/app/logs/`
- 建议接入：Prometheus + Grafana 监控

---

## 5. 常见问题

### Q1: Docker 构建失败

```bash
# 清理 Docker 缓存后重试
docker system prune -f
docker-compose build --no-cache
```

### Q2: 数据库连接失败

```bash
# 检查 MySQL 容器状态
docker-compose logs mysql

# 等待 MySQL 完全启动（约 30 秒）
docker-compose restart backend
```

### Q3: 前端无法访问后端 API

```bash
# 检查后端服务状态
curl http://localhost:8080/actuator/health

# 检查 Nginx 代理配置
docker exec -it it-training-frontend cat /etc/nginx/conf.d/default.conf
```

### Q4: AI 推荐功能不可用

1. 检查 AI_API_KEY 是否正确配置
2. 查看后端日志：`docker-compose logs -f backend | grep -i ai`
3. AI 服务不可用时会自动降级返回热门课程

### Q5: 如何备份数据

```bash
# 备份数据库
docker exec it-training-mysql mysqldump -u itts -p it_training > backup.sql

# 恢复数据库
docker exec -i it-training-mysql mysql -u itts -p it_training < backup.sql
```

---

## 技术支持

如有问题，请提交 Issue 或联系开发团队。

---

## 修订记录

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0.0 | 2025-12-14 | 开发团队 | 初始版本 |
