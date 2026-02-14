# 前端服务 Dockerfile
# 多阶段构建: Node.js 构建 + Nginx 运行

# ---- 构建阶段 ----
FROM node:20-alpine AS build
WORKDIR /app

# 先复制依赖文件，利用 Docker 缓存
COPY package*.json ./
RUN npm ci --no-audit --no-fund

# 复制源代码并构建
COPY . .
RUN npm run build

# ---- 运行阶段 ----
FROM nginx:alpine

# 从构建阶段复制产物
COPY --from=build /app/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
