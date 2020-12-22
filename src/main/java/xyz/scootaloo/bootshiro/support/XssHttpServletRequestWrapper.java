package xyz.scootaloo.bootshiro.support;

import xyz.scootaloo.bootshiro.utils.XssUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/**
 * 将request对象进行包装。
 * 重写其中的getter方法。
 * 在获取信息的时候对源数据进行过滤，降低Xss攻击造成的风险。
 *
 * 过滤方式:
 * @see XssUtils#stripSqlXss(String)
 * @see XssUtils#stripXss(String)
 *
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 20:37
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request 被包装的请求
     * @throws IllegalArgumentException if the request is null
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int len = values.length;
        String[] encodedValues = new String[len];
        for (int i = 0; i < len; i++ ) {
            encodedValues[i] = filterParamString(values[i]);
        }
        return encodedValues;
    }

    @Override
    public Map<String,String[]> getParameterMap() {
        Map<String, String[]> primary = super.getParameterMap();
        Map<String, String[]> result = new HashMap<>(16);
        for (Map.Entry<String, String[]> entry : primary.entrySet()) {
            result.put(entry.getKey(), filterEntryString(entry.getValue()));
        }
        return result;
    }

    @Override
    public String getParameter(String parameter) {
        return filterParamString(super.getParameter(parameter));
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] cookies = super.getCookies();
        if (cookies == null)
            return null;
        for (var cookie : cookies) {
            cookie.setValue(filterParamString(cookie.getValue()));
        }
        return cookies;
    }

    private String[] filterEntryString(String[] value) {
        for (int i = 0; i < value.length; i++) {
            value[i] = filterParamString(value[i]);
        }
        return value;
    }

    // 过滤 XSS 和 SQL 注入
    private String filterParamString(String value) {
        if (null == value) {
            return null;
        }
        return XssUtils.stripSqlXss(value);
    }

}
