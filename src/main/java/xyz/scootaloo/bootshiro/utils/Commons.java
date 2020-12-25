package xyz.scootaloo.bootshiro.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.scootaloo.bootshiro.domain.bo.DVal;
import xyz.scootaloo.bootshiro.support.Assert;

import javax.servlet.ServletRequest;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 包含一些常用的方法
 * 管理一些操作redis的通用方法
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 19:13
 */
@Slf4j
@Component
public class Commons {
    // 创建随机字符串所用的常量
    private static final char[] WORDS = "ABCDEFGHIJKLMNOPQRST1234567890".toCharArray();
    private static final int WORD_LEN = WORDS.length;
    private static final Random random = new Random();

    // 创建tokenKey, userKey, jwt所用的常量
    private static final String TOKEN_KEY_PREFIX = DVal.tokenKeyPrefix;
    private static final String JWT_SESSION_PREFIX = DVal.jwtSessionPrefix;
    private static final long REFRESH_PERIOD_TIME = DVal.refreshPeriodTime;
    private static final int INVALID_TIME = 5;

    private static StringRedisTemplate redisTemplate;

    /**
     * 生成随机字符串
     * 生成的字符串只包含26个英文大写字母和数字
     * @param len 字符串的长度
     * @return 随机字符串
     */
    public static String getRandomStr(int len) {
        Assert.expression(len >= 0, "长度过小");
        StringBuilder sb = new StringBuilder(len);
        while (len > 0) {
            sb.append(WORDS[random.nextInt(WORD_LEN)]);
            len--;
        }
        return sb.toString();
    }

    /**
     * 动态生成秘钥，redis存储秘钥供之后秘钥验证使用，设置有效期5秒用完即丢弃
     * key=TOKEN_KEY_127.0.0.1${usrKey}, value=${tokenKey}
     * @param request 执行注册或登陆的用户请求
     */
    public static SimpleToken genTokenKey(ServletRequest request) {
        String tokenKey = Commons.getRandomStr(16);
        String userKey = Commons.getRandomStr(6);
        redisTemplate.opsForValue().set(getRedisTokenKey(request, userKey), tokenKey,
                INVALID_TIME, TimeUnit.SECONDS);
        return new SimpleToken(tokenKey, userKey);
    }

    /**
     * 从redis中获取tokenKey
     * @param request 执行注册或者登陆操作的请求
     * @param userKey 此用户的标识，由{@link #getRandomStr(int)} 生成
     * @return 从redis中拿到的tokenKey，可能是null(当key不一致或者key过期时)
     */
    public static String getTokenKey(ServletRequest request, String userKey) {
        return redisTemplate.opsForValue().get(getRedisTokenKey(request, userKey));
    }

    /**
     * 将用户登陆信息的信息，以jwt的格式存储在redis中
     * @param appId 用户标识
     * @param jwt 此用户的jwt
     */
    public static void genJwtSession(String appId, String jwt) {
        redisTemplate.opsForValue().set(getRedisJwtSessionKey(appId), jwt, REFRESH_PERIOD_TIME, TimeUnit.SECONDS);
    }

    /**
     * 获取redis中存储的此用户的jwt信息
     * @param appId 用户标识
     * @return 此用户的jwt，可能是null(当key不一致或者key过期时)
     */
    public static String getJwtSession(String appId) {
        return redisTemplate.opsForValue().get(getRedisJwtSessionKey(appId));
    }

    /**
     * 当用户登出时，也同时移除该用户在redis中的数据
     * @param appId 该用户的标识
     */
    public static void delJwtSession(String appId) {
        redisTemplate.opsForValue().getOperations().delete(JWT_SESSION_PREFIX + appId);
    }

    // 统一的方式生成redis的key，用于存储tokenKey用
    private static String getRedisTokenKey(ServletRequest request, String userKey) {
        return TOKEN_KEY_PREFIX + IpUtils.getIp(request) + userKey.toUpperCase();
    }

    // 统一的方式生成redis的key，用于存储jwtSession用
    private static String getRedisJwtSessionKey(String appId) {
        return JWT_SESSION_PREFIX + appId.toUpperCase();
    }

    @Autowired
    public void setRedisTemplate(StringRedisTemplate strRedisTemplate) {
        redisTemplate = strRedisTemplate;
    }

    public static class SimpleToken {
        public final String tokenKey;
        public final String userKey;
        public SimpleToken(String tokenKey, String userKey) {
            this.tokenKey = tokenKey;
            this.userKey = userKey;
        }
    }

}
