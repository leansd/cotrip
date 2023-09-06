# 使用官方的Java 17基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven构建的jar文件到容器中
COPY target/cotrip-0.0.1-SNAPSHOT.jar /app/cotrip.jar
# 如果需要，复制target/classes目录中的资源
# COPY target/classes /app/classes

# 设置默认的环境变量，可以在运行容器时覆盖
ENV spring.profiles.active=integration

# 暴露8080端口
EXPOSE 8080

# 设置容器启动时的命令
CMD ["java", "-jar", "cotrip.jar"]
