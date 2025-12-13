# 后端服务 Dockerfile
# 基于 Eclipse Temurin JDK 17
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# 复制 Maven 配置和依赖
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 下载依赖（利用 Docker 缓存）
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# 复制源代码
COPY src src

# 构建应用（跳过测试以加快构建）
RUN ./mvnw package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 安装必要工具
RUN apk add --no-cache curl

# 创建非 root 用户
RUN addgroup -g 1000 spring && adduser -u 1000 -G spring -s /bin/sh -D spring
USER spring:spring

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# JVM 优化参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
