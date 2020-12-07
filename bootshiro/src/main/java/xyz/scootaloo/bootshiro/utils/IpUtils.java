package xyz.scootaloo.bootshiro.utils;

import org.apache.commons.lang.text.StrTokenizer;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 11:37
 */
public class IpUtils {

    private static final String N255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static final Pattern PATTERN = Pattern.compile("^(?:" + N255 + "\\.){3}" + N255 + "$");
    private static final String X_FORWARDED_FOR = "x-forwarded-for";

    public static String getIp(HttpServletRequest request) {
        String ip;
        boolean found = false;
        if ((ip = request.getHeader(X_FORWARDED_FOR)) != null) {
            StrTokenizer tokenizer = new StrTokenizer(ip, ",");
            while (tokenizer.hasNext()) {
                ip = tokenizer.nextToken().trim();
                if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static boolean isIPv4Private(String ip) {
        long longIp = ipV4ToLong(ip);
        return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255"))
                || (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255"))
                || longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255");
    }

    private static long ipV4ToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16)
                + (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
    }

    private static boolean isIPv4Valid(String ip) {
        return PATTERN.matcher(ip).matches();
    }

    private IpUtils() {
    }

}
