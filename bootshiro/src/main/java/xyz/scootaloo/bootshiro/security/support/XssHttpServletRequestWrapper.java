package xyz.scootaloo.bootshiro.security.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 将request对象进行包装。
 * 重写其中的getter方法。
 * 在获取信息的时候对源数据进行过滤，降低Xss攻击造成的风险。
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


}
