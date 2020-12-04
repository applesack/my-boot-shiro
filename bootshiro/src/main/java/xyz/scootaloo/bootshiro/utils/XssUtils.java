package xyz.scootaloo.bootshiro.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Xss定义参考: <a href="https://baike.baidu.com/item/XSS%E6%94%BB%E5%87%BB">Xss百度百科</a>
 * 资料参考: <a href="https://www.bilibili.com/video/BV1s5411s7qd">XSS原理和攻防 / Web安全常识</a>
 * <p>Xss主要攻击方式是篡改用户发送的请求或者是服务端发送的响应，在报文中特定的位置插入了攻击者的恶意代码，
 * 避免这些恶意代码被执行，所以一种有效的手段就是在需要获取请求报文内容的时候对其进行一定的过滤</p
 *
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 17:04
 */
public class XssUtils {

    // 脚本过滤链
    private static final List<Pattern> FILTER_CHAIN = getBlackList();
    // sql注入的过滤条件
    private static final Pattern SQL_FILTER = Pattern.compile("('.+--)|(--)|(%7C)");

    /**
     * 过滤掉包含在文本中的脚本内容
     * @param text 待过滤的文本
     * @return 去除脚本后的内容
     */
    public static String stripXss(String text) {
        if (text == null)
            return null;
        if (text.equals(""))
            return text;
        for (Pattern pattern : FILTER_CHAIN) {
            text = pattern.matcher(text).replaceAll("");
        }
        return text;
    }

    public static String stripSqlXss(String value) {
        return stripXss(stripSqlInjection(value));
    }

    /**
     * 参考源码做了修改，但是由于对正则表达式不是很熟练，所以只是对代码进行小幅度的优化。
     * 经过观察源码发现对于一段文本的过滤会多次生成Pattern对象，出于优化性能的考虑，这些对象可以一次性生成然后多次使用。
     * 所以把这些Pattern对象的生成逻辑封装到这个方法里。{@link #FILTER_CHAIN}
     * @return 过滤链
     */
    private static List<Pattern> getBlackList() {
        List<Pattern> blackList = new ArrayList<>(8);
        // js脚本
        blackList.add(compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE));
        // js脚本闭标签
        blackList.add(compile("</script>", Pattern.CASE_INSENSITIVE));
        // js脚本开标签
        blackList.add(compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        // js解释执行代码
        blackList.add(compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        // 好像也是解释执行的代码
        blackList.add(compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        // 元素标签中的js代码
        blackList.add(compile("javascript:", Pattern.CASE_INSENSITIVE));
        // vb代码
        blackList.add(compile("vbscript:", Pattern.CASE_INSENSITIVE));
        // js页面加载完成时执行的代码
        blackList.add(compile("onload(.*?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        return blackList;
    }

    private static Pattern compile(String regex, int flags) {
        return Pattern.compile(regex, flags);
    }

    private static String stripSqlInjection(String sql) {
        if (sql == null)
            return null;
        return SQL_FILTER.matcher(sql).replaceAll("");
    }

    // 测试
    public static void main(String[] args) {
        String[] texts = {
                "1. ",
                "2. <script>alert('abc')</script>",
                "3. eval('{'a' = true}')",
                "4. onload(alert('abc'))"
        };
        for (String text : texts) {
            System.out.println(stripXss(text));
        }
    }

}
