package xyz.scootaloo.bootshiro.security.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.util.WebUtils;
import xyz.scootaloo.bootshiro.security.filter.ShiroFilterChainManager;
import xyz.scootaloo.bootshiro.utils.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用于给每个request分配一个过滤链
 * 重写getChain()方法后，支持对rest风格的url权限处理
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 11:21
 */
@Slf4j
public class RestPathMatchingFilterChainResolver extends PathMatchingFilterChainResolver {
    // 路径分隔符
    private static final String DEFAULT_PATH_SEPARATOR = "/";

    public RestPathMatchingFilterChainResolver() {
        super();
    }

    @Override // 给每个request分配一个过滤链
    public FilterChain getChain(ServletRequest request, ServletResponse response,
                                FilterChain originalChain) {
        Result result = match(request);
        if (!result.isFind)
            return null;
        return getFilterChainManager().proxy(originalChain, result.pattern);
    }

    /**
     * getChain()方法的具体实现，实现流程:
     * <p>遍历所有的资源访问条件，getChainNames()方法返回一个LinkedHashMap集合对象，来自{@link ShiroFilterChainManager#initGetFilterChain()},
     * 这个集合的元素的格式都是{path}=={method}，其中path使用的是ant的格式，也就是说<code>/account/**</code>和<code>/account/login</code>相匹配，
     * 这里要做的就是获取这条请求的 路径及方法 对应 的资源描述，确定其可被映射，或者不可被映射。
     *      假如可被映射，这条请求将被指定的过滤器处理。
     *      假如不可被映射，这条请求将被直接放行。</p>
     * @param servletRequest 前端传来的请求
     * @return 一个结果的包装，这个对象有一个isFind属性。
     *          假如为true，则表示找到了与这条路径相匹配的过滤条件，此时pattern属性可用。
     *          假如为false，则表示这是一个没有被记录的资源，不被shiro保护，直接放行到controller层。
     */
    private Result match(ServletRequest servletRequest) {
        HttpServletRequest request = WebUtils.toHttp(servletRequest);
        String uri = this.getPathWithinApplication(request);
        uri = subEnd(uri);
        for (String pattern : getFilterChainManager().getChainNames()) {
            List<String> segments = StringUtils.splitBy(pattern, '=', 2);
            if (segments.size() == 2) {
                if (pathMatches(segments.get(0), uri) && sameMethod(request, segments.get(1)))
                    return new Result(true, pattern);
            } else if (segments.size() == 1 && pathMatches(segments.get(0), uri)) {
                return new Result(true, pattern);
            }
        }
        return Result.FAIL_RES;
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

    // 判断一个请求是否是指定的请求方式
    private boolean sameMethod(HttpServletRequest request, String methodName) {
        return request.getMethod().equalsIgnoreCase(methodName);
    }

    // 将处理的结果封装成对象
    private static class Result {
        private static final Result FAIL_RES = new Result(false, null);

        private final boolean isFind;
        private final String pattern;

        public Result(boolean isFind, String pattern) {
            this.pattern = pattern;
            this.isFind = isFind;
        }

    }

}
