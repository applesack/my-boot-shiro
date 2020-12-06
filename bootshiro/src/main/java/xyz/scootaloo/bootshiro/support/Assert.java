package xyz.scootaloo.bootshiro.support;

import io.jsonwebtoken.lang.Strings;

/**
 * 用于断言
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 20:20
 */
public class Assert {

    public static void expression(boolean flag) {
        expression(flag, null);
    }

    public static void expression(boolean flag, String message) {
        if (flag)
            throw new IllegalArgumentException(message);
    }

    public static void hasText(CharSequence line) {
        hasText(line, null);
    }

    public static void hasText(CharSequence line, String message) {
        if (!Strings.hasText(line)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object obj) {
        notNull(obj, null);
    }

    public static void notNull(Object obj, String message) {
        if (obj == null)
            throw new NullPointerException(message);
    }

}
