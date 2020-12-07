package xyz.scootaloo.bootshiro.utils;

import xyz.scootaloo.bootshiro.support.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 20:18
 */
public class StringUtils {

    public static List<String> splitBy(String line, char point) {
        Assert.notNull(line, "被分隔的字符串不能为空");
        StringBuilder sb = new StringBuilder(128);
        List<String> segments = new ArrayList<>();
        for (char c : line.toCharArray()) {
            if (c == point) {
                if (sb.length() > 0) {
                    segments.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0)
            segments.add(sb.toString());
        return segments;
    }

    public static boolean hasText(CharSequence text) {
        if (!hasLength(text)) {
            return false;
        }
        int strLen = text.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    private StringUtils() {
    }

}
