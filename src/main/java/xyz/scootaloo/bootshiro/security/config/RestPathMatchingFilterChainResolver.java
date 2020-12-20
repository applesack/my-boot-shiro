package xyz.scootaloo.bootshiro.security.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * 用于给每个request分配一个过滤链
 * 重写getChain()方法后，支持对rest风格的url权限处理
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 11:21
 */
@Slf4j
public class RestPathMatchingFilterChainResolver extends PathMatchingFilterChainResolver {

    private static final int NUM_2 = 2;
    private static final String METHOD_SEPARATOR = "==";
    private static final String DEFAULT_PATH_SEPARATOR = "/";

    public RestPathMatchingFilterChainResolver() {
        super();
    }

    /**
     * description TODO 重写filterChain匹配
     * @param servletRequest 1
     * @param servletResponse 2
     * @param originalChain 3
     * @return 配置好的过滤链
     */
    @Override
    public FilterChain getChain(ServletRequest servletRequest, ServletResponse servletResponse,
                                FilterChain originalChain) {
        // 获取过滤链，并将servletReq 转换成 httpReq
        FilterChainManager filterChainManager = this.getFilterChainManager();
        HttpServletRequest request = WebUtils.toHttp(servletRequest);

        // 假如没有配置过滤链，则返回null
        if (!filterChainManager.hasChains())
            return null;

        String uri = this.getPathWithinApplication(request);
        uri = subEnd(uri);

        String pattern;
        boolean flag;
        String[] segments;
        Iterator<String> filterIterator = filterChainManager.getChainNames().iterator();

        // 遍历过滤链，检查每个
        do {
            if (!filterIterator.hasNext()) {
                return null;
            }

            pattern = filterIterator.next();

            segments = pattern.split(METHOD_SEPARATOR);
            if (segments.length == NUM_2) {
                // 分割出url+httpMethod,判断httpMethod和request请求的method是否一致,不一致直接false
                flag = !request.getMethod().equalsIgnoreCase(segments[1]);
            } else {
                flag = false;
            }
            pattern = segments[0];
            if (pattern != null && pattern.endsWith(DEFAULT_PATH_SEPARATOR)) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }
        } while(!this.pathMatches(pattern, uri) || flag);

        if (log.isTraceEnabled()) {
            log.trace("Matched path pattern [" + pattern + "] for uri [" + uri + "].  Utilizing corresponding filter chain...");
        }
        if (segments.length == NUM_2) {
            pattern = pattern.concat(METHOD_SEPARATOR).concat(request.getMethod().toUpperCase());
        }

        log.info("pattern: " + pattern);
        return filterChainManager.proxy(originalChain, pattern);
    }

    // 假如path以分隔符"/"结尾，则去除这个分隔符并返回
    private String subEnd(String path) {
        if (path == null)
            return null;
        if (path.endsWith(DEFAULT_PATH_SEPARATOR)) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

}
