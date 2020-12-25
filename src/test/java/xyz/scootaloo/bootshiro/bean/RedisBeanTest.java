package xyz.scootaloo.bootshiro.bean;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import xyz.scootaloo.bootshiro.utils.Commons;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 15:10
 */
@SpringBootTest
public class RedisBeanTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testBean() {
        System.out.println(redisTemplate);
    }

    @Test
    public void testRedisFunc() {
        String appId = "FAST";
        Commons.genJwtSession(appId, "1234");
        String jwt = Commons.getJwtSession(appId);
        System.out.println(jwt);
    }

}
