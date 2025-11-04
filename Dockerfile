# 使用 JDK 17 作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制 Maven 包
COPY target/*.jar app.jar

# 设置环境变量
ENV TZ=Asia/Shanghai
ENV JAVA_OPTS=""

# 暴露端口（根据您的应用配置调整）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]