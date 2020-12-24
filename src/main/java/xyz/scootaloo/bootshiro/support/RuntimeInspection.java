package xyz.scootaloo.bootshiro.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 在项目启动后检查运行环境
 * 主要检查此项目依赖的一些外部的中间件的连接情况
 * @author : flutterdash@qq.com
 * @since : 2020年12月24日 19:09
 */
@Slf4j
@Component
public class RuntimeInspection implements ApplicationRunner {

    private StringRedisTemplate redisTemplate;

    // redis
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;

    @Override
    public void run(ApplicationArguments args) {
        checkRedis();
    }

    private void checkRedis() {
        String key = "testConnection";
        redisTemplate.opsForValue().set(key, "连接成功");
        String res = redisTemplate.opsForValue().get(key);
        log.info("检查redis环境，"+ redisHost + ":" + redisPort + "，返回值: " + res);
    }

    public RuntimeInspection() {
    }

    // setter

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}
