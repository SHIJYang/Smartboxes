# Smartboxes 部署指南

## 环境要求
- JDK 17 或更高版本
- Maven 3.6+
- Docker（可选，如果使用容器部署）
- MySQL 8.0+

## 1. 传统部署方式

### 1.1 编译打包
```bash
# 清理并打包
mvn clean package -DskipTests
```

### 1.2 运行应用
```bash
# 运行 jar 包
java -jar target/boxes-1.0.0.jar
```

## 2. Docker 部署方式

### 2.1 构建 Docker 镜像
```bash
# 首先编译项目
mvn clean package -DskipTests

# 构建 Docker 镜像
docker build -t smartboxes:latest .
```

### 2.2 运行 Docker 容器
```bash
# 运行容器
docker run -d \
  --name smartboxes \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:3306/smartboxes \
  -e SPRING_DATASOURCE_USERNAME=your-username \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  smartboxes:latest
```

## 3. 配置说明

### 3.1 数据库配置
部署前请确保已正确配置数据库连接信息，可以通过环境变量或修改 application.yml 文件进行配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smartboxes?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
```

### 3.2 应用配置
可以通过环境变量覆盖配置文件中的设置，例如：
- `SERVER_PORT`: 应用运行端口
- `SPRING_PROFILES_ACTIVE`: 运行环境（dev/prod）

## 4. 健康检查
部署完成后，可以通过访问以下端点检查应用状态：
- 应用健康检查：`http://your-host:8080/actuator/health`
- Swagger API 文档：`http://your-host:8080/swagger-ui/index.html`

## 5. 日志查看
- 传统部署：日志文件位于 `logs/` 目录下
- Docker 部署：使用 `docker logs smartboxes` 查看容器日志

## 6. 注意事项
1. 部署前请确保数据库已正确初始化（执行 sql 目录下的所有 SQL 文件）
2. 确保服务器防火墙已开放相应端口
3. 生产环境建议配置 HTTPS
4. 定期备份数据库

## 7. 问题排查
如果遇到问题，请检查：
1. 日志文件中的错误信息
2. 数据库连接是否正常
3. 端口是否被占用
4. JVM 内存设置是否合适