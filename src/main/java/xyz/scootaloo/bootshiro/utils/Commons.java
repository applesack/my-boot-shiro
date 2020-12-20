package xyz.scootaloo.bootshiro.utils;

import xyz.scootaloo.bootshiro.support.Assert;

import java.util.Random;

/**
 * 包含一些常用的方法
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 19:13
 */
public abstract class Commons {

    private static final char[] WORDS = "ABCDEFGHIJKLMNOPQRST1234567890".toCharArray();
    private static final int WORD_LEN = WORDS.length;
    private static final Random random = new Random();

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

}
