package xyz.scootaloo.bootshiro.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.util.WebUtils;
import xyz.scootaloo.bootshiro.support.XssHttpServletRequestWrapper;
import xyz.scootaloo.bootshiro.support.factory.EmptyCollections;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 22:17
 */
@Slf4j
public class HttpUtils {

    private static final String JSON_CONTENT_TYPE = "application/json;charset=utf-8";
    private static final String STR_BODY          = "body";

    /**
     * 获取http-request-header中过滤后的内容，用map进行封装后返回
     * @param request 提供header内容
     * @return 过滤Xss后用map封装的header
     */
    public static Map<String, String> getRequestHeader(ServletRequest request) {
        if (request == null)
            return EmptyCollections.STRING_STRING_MAP;
        HttpServletRequest wrapper = getWrapperRequest(request);
        Map<String, String> rsl = new HashMap<>(16);

        Enumeration<String> enums = wrapper.getHeaderNames();
        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            String value = wrapper.getHeader(key);
            if (value != null) {
                rsl.put(key, value);
            }
        }
        return rsl;
    }

    // 同上
    public static Map<String, String> getRequestParameters(ServletRequest request) {
        if (request == null)
            return EmptyCollections.STRING_STRING_MAP;
        HttpServletRequest wrapper = getWrapperRequest(request);
        Map<String, String> rsl = new HashMap<>(16);

        Enumeration<String> enums = wrapper.getParameterNames();
        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            String value = wrapper.getParameter(key);
            rsl.put(key, value);
        }
        return rsl;
    }

    /**
     * 以键值对的格式返回body中的内容
     * @param request 提供body
     * @return body -> map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getRequestBodyMap(ServletRequest request) {
        if (request == null)
            return EmptyCollections.STRING_STRING_MAP;
        if (request.getAttribute(STR_BODY) != null)
            return (Map<String, String>) request.getAttribute(STR_BODY);

        try {
            Map<String, String> metaMap = new HashMap<>(16);
            Map<String, Object> tmpMap = JSON.parseObject(request.getInputStream(), Map.class);
            tmpMap.forEach((k, v) -> metaMap.put(k, v.toString()));
            request.setAttribute(STR_BODY, metaMap);
            return metaMap;
        } catch (IOException ioException) {
            log.debug(ioException.getMessage());
            return EmptyCollections.STRING_STRING_MAP;
        }
    }

    public static String getParameter(ServletRequest request, String key) {
        return getWrapperRequest(request).getParameter(key);
    }

    public static String getHeader(ServletRequest request, String key) {
        return getWrapperRequest(request).getHeader(key);
    }

    /**
     * 将servlet请求进行包装
     * @param servletRequest servletRequest object
     * @return XssHttpServletRequestWrapper
     */
    public static HttpServletRequest getWrapperRequest(ServletRequest servletRequest) {
        return new XssHttpServletRequestWrapper((HttpServletRequest) servletRequest);
    }

    public static void responseWrite(ServletResponse response, Object data) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(JSON_CONTENT_TYPE);
        try (PrintWriter writer = WebUtils.toHttp(response).getWriter()) {
            writer.write(JSON.toJSONString(data));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
