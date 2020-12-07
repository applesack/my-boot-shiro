package xyz.scootaloo.bootshiro.utils;

import org.apache.commons.lang.text.StrTokenizer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * ip工具
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 11:37
 */
public class IpUtils {

    /**
     * ip地址由以字符串表示的时候分为4个部分，每个部分都用小数点分隔
     * 源码中这段正则表达式的含义是:
     * N255 : 250~255 或 200~249 或 0~199 // 基本上覆盖了0~255这个区间
     * PATTERN : 这个正则表达式是前面的0~255.重复3次再加上一次0~255完成匹配
     * 但是由这种复杂的正则表达式完成匹配效率不高，所以放弃使用这种方式，改为用算法实现
     * @see IpUtils#isIPv4Valid(ResultWrapper)
     * @see ResultWrapper
     */
//    private static final String N255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
//    private static final Pattern PATTERN = Pattern.compile("^(?:" + N255 + "\\.){3}" + N255 + "$");
    private static final String X_FORWARDED_FOR = "x-forwarded-for";

    /**
     * 获取request对象的真实ip
     * @param request 请求
     * @return 真实ip
     */
    public static String getIp(HttpServletRequest request) {
        String ip;
        boolean found = false;
        // 假如这个请求是由代理服务器访问，那么X_FORWARDED_FOR会包含这些代理服务器的地址
        if ((ip = request.getHeader(X_FORWARDED_FOR)) != null) {
            StrTokenizer tokenizer = new StrTokenizer(ip, ",");
            // 找到第一个有效的ip
            while (tokenizer.hasNext()) {
                ip = tokenizer.nextToken().trim();
                ResultWrapper wrapper = new ResultWrapper(ip);
                if (isIPv4Valid(wrapper)) {
                    if (isIPv4Private(wrapper)) {
                        found = true;
                        break;
                    }
                }
            }
        }
        if (!found) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // 判断这个ip地址是否是私有的
    private static boolean isIPv4Private(ResultWrapper wrapper) {
        long longIp = wrapper.toLong();
        return
                    (longIp >= 167772160L && longIp <= 184549375L)    // 10.0.0.0 ~ 10.255.255.255
                ||  (longIp >= 2886729728L && longIp <= 2887778303L)  // 172.16.0.0 ~ 172.31.255.255
                ||  (longIp >= 3232235520L && longIp <= 3232301055L); // 192.168.0.0 ~ 192.168.255.255
    }

    // 算法实现，代替正则表达式匹配
    private static boolean isIPv4Valid(ResultWrapper wrapper) {
        String ip = wrapper.ipAddress;
        List<String> segments = StringUtils.splitBy(ip, '.');
        if (segments.size() != 4)
            return false;
        for (String segment : segments) {
            if (!isBetween0And255(wrapper, segment))
                return false;
        }
        return true;
    }

    // 字符串代表的数字是否存在于0到255之间
    private static boolean isBetween0And255(ResultWrapper wrapper, String  segment) {
        int len = segment.length();
        if (len > 3 || len == 0)
            return false;
        char c;
        int[] nums = {1, 10, 100};
        int sum = 0;
        for (int i = (len - 1); i>=0; i--) {
            c = segment.charAt(i);
            if (!Character.isDigit(c))
                return false;
            sum += nums[len - i - 1] * (c - '0');
        }
        boolean flag = sum <= 255;
        if (flag)
            wrapper.addSegment(sum);
        return flag;
    }

    private IpUtils() {
    }

    // 将计算的结果保存起来，减少计算量
    private static class ResultWrapper {
        private final String ipAddress;
        private final List<Integer> segments;

        private static final int OFFSET = 24;

        public ResultWrapper(String ip) {
            this.ipAddress = ip;
            segments = new ArrayList<>();
        }

        public void addSegment(int segment) {
            segments.add(segment);
        }

        // 将ip地址转为其2进制状态时对应的long数值
        public long toLong() {
            int offset = OFFSET;
            long res = 0;
            for (int segment : segments) {
                res += ((long) segment) << offset;
                offset -= 8;
            }
            return res;
        }

    }

}
