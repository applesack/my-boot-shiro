# 基础镜像JDK14
FROM openjdk:11

# 维护者签名
MAINTAINER applesack "flutterdash@qq.com"

# 暴露端口
EXPOSE 8080

# 创建容器到宿主机的映射目录
VOLUME /data/bootshiro/bootshiro /data/bootshiro

# 镜像内执行命令，将工作目录下的内容清除（有进程占用，这个还算手动删除算了）
#RUN rm -rf /data/bootshiro*

# 将主机的文件拷贝到容器
ADD ./target/MyBootShiro.jar /data/bootshiro/MyBootShiro.jar

# 切换工作目录
WORKDIR /data/bootshiro

# 运行BootShiro应用
CMD ["java", "-jar", "MyBootShiro.jar","--spring.profiles.active=prod"]




