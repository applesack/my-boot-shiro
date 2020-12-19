package xyz.scootaloo.bootshiro.utils;

import org.springframework.lang.Nullable;
import xyz.scootaloo.bootshiro.support.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 字符串工具
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 20:18
 */
public abstract class StringUtils extends org.springframework.util.StringUtils {

    public static Set<String> splitAndToSet(String line, char separator) {
        Set<String> res = new HashSet<>();
        if (isEmpty(line))
            return res;
        List<String> segments = splitBy(line, separator);
        if (segments.size() > 0) {
            res.addAll(segments);
        }
        return res;
    }

    public static List<String> splitBy(String line, char separator) {
        Assert.notNull(line, "被分隔的字符串不能为空");
        StringBuilder sb = new StringBuilder(128);
        List<String> segments = new ArrayList<>();
        for (char c : line.toCharArray()) {
            if (c == separator) {
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

    public static boolean isStartWithAndEndWith(String line, String prefix, String postfix) {
        Assert.notNull(postfix);
        Assert.notNull(postfix);
        int preLen = prefix.length();
        int postLen = postfix.length();
        int lineLen = line.length();
        if (lineLen < (preLen + postLen))
            return false;

        // 检查是否以prefix开头
        for (int i = 0; i<preLen; i++) {
            if (line.charAt(i) != prefix.charAt(i))
                return false;
        }

        // 检查是否以postfix结尾
        int pad = lineLen - postLen;
        for (int i = 0; i<postLen; i++) {
            if (line.charAt(i + pad) != postfix.charAt(i))
                return false;
        }

        return true;
    }

    /**
     * 判断文本内是否有有效的内容
     * @param text 文本
     * @return 是否含有有效的文本: 对象不为空，并且不全是空格
     */
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

    /**
     * 文本是否有长度
     * @param text 文本
     * @return 文本不为空，并且内容长度大于0
     */
    public static boolean hasLength(CharSequence text) {
        return (text != null && text.length() > 0);
    }

    public static boolean isEmpty(@Nullable Object str) {
        return str == null || "".equals(str);
    }

    public static void main(String[] args) {
        String path = "/account==";
        System.out.println(splitBy(path, '='));

        String line = "jwt{1}2";
        String pre = "jwt{";
        String post = "1}2";
        System.out.println(isStartWithAndEndWith(line, pre, post));
    }

}
