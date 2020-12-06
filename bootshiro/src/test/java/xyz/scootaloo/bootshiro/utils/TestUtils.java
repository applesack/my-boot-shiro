package xyz.scootaloo.bootshiro.utils;

import org.junit.jupiter.api.Test;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 21:33
 */
public class TestUtils {

    @Test
    public void testStringUtils() {
        String[] rawStrings = {
                "xxxxx.yyyy.zzzzz",
                ".xxx.",
                ".",
                "........xxxxxxx"
        };
        for (String line : rawStrings) {
            System.out.println(line + " => " + StringUtils.splitBy(line, '.'));
        }
    }

    @Test
    public void testJWTUtils() {
        String jwtString = JwtUtils
                .issueJWT("mike")
                .id("id12")
                .permissions("p1,p2")
                .roles("r1,r2")
                .create();
        System.out.println(JwtUtils.parseJwtPayload(jwtString));
    }

}
