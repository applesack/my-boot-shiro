package xyz.scootaloo.bootshiro.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.bo.StatusCode;
import xyz.scootaloo.bootshiro.security.token.PasswordToken;
import xyz.scootaloo.bootshiro.utils.AesUtils;
import xyz.scootaloo.bootshiro.utils.Commons;
import xyz.scootaloo.bootshiro.utils.HttpUtils;
import xyz.scootaloo.bootshiro.utils.IpUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 做为<code>/account/**</code>这个路径的前置过滤
 * 负责检查请求这个路径的报文中是否包含正确的信息，假如请求不符合规范，
 * 则会在这里被拦截，不会进入到controller层。
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 11:36
 */
@Slf4j
public class PasswordFilter extends AccessControlFilter {
    // 字符串常量
    private static final String TOKEN_KEY_PREFIX = "TOKEN_KEY_";

    // 由使用者注入依赖
    private boolean isEncryptPassword;
    private StringRedisTemplate redisTemplate;

    /**
     * 是否允许访问
     * @param request 请求
     * @param response 响应
     * @param mappedValue 拦截器参数
     * @return 用户已登陆，则进一步处理；用户未登陆，则放行到登陆注册接口
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        // 如果其已经登录，再此发送登录请求
        // 拒绝，统一交给 onAccessDenied 处理
        return subject != null && subject.isAuthenticated();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        // 判断若为获取登录注册加密动态秘钥请求，则将密匙写入响应，发给客户端
        if (isPasswordTokenGet(request)) {
            //动态生成秘钥，redis存储秘钥供之后秘钥验证使用，设置有效期5秒用完即丢弃
            String tokenKey = Commons.getRandomStr(16);
            String userKey = Commons.getRandomStr(6);
            try {
                // key=TOKEN_KEY_127.0.0.1${usrKey}, value=${tokenKey}
                redisTemplate.opsForValue().set(TOKEN_KEY_PREFIX + IpUtils.getIp(request)
                        + userKey.toUpperCase(), tokenKey,5, TimeUnit.SECONDS);
                // 动态秘钥response返回给前端
                HttpUtils.responseWrite(response, Message.of(StatusCode.ISSUED_TOKEN_KEY_SUCCESS)
                        .addData("tokenKey", tokenKey)
                        .addData("userKey", userKey.toUpperCase()));
            } catch (Exception e) {
                log.warn("签发动态秘钥失败" + e.getMessage(), e);
                HttpUtils.responseWrite(response, Message.of(StatusCode.ISSUED_TOKEN_KEY_FAIL));
            }
            return false;
        }

        // 判断是否是登录请求
        if (isPasswordLoginPost(request)) {
            AuthenticationToken authenticationToken;
            try {
                authenticationToken = createPasswordToken(request);
            } catch (Exception e) {
                // response 告知无效请求
                HttpUtils.responseWrite(response, Message.failure());
                return false;
            }

            Subject subject = getSubject(request, response);
            try {
                subject.login(authenticationToken);
                //登录认证成功, 进入请求派发json web token url资源内
                return true;
            } catch (AuthenticationException e) {
                log.warn(authenticationToken.getPrincipal() + "::" + e.getMessage());
                // 返回response告诉客户端认证失败
                HttpUtils.responseWrite(response, Message.of(StatusCode.LOGIN_FAIL));
                return false;
            } catch (Exception e) {
                log.error(authenticationToken.getPrincipal() + "::认证异常::" + e.getMessage(), e);
                // 返回response告诉客户端认证失败
                HttpUtils.responseWrite(response, Message.of(StatusCode.LOGIN_FAIL));
                return false;
            }
        }

        // 判断是否为注册请求,若是通过过滤链进入controller注册
        if (isAccountRegisterPost(request)) {
            return true;
        }

        // 之后添加对账户的找回等
        // response 告知无效请求
        HttpUtils.responseWrite(response, Message.failure());
        return false;
    }

    /**
     * 例，当这样的请求则为true:
     * <code>GET /account/register?tokenKey=get</code>
     * @param request 注册登陆请求
     * @return 是否带有参数tokenKey且值为get，并且请求方式为get
     */
    private boolean isPasswordTokenGet(ServletRequest request) {
        String tokenKey = HttpUtils.getParameter(request, "tokenKey");
        return (request instanceof HttpServletRequest)
                && "GET".equalsIgnoreCase(((HttpServletRequest) request).getMethod())
                && "get".equals(tokenKey);
    }

    /**
     * 请求必须含有必要字段，password，timestamp，methodName，appId
     * 请求方式为POST，methodName为login
     * @param request 请求
     * @return 是否符合条件
     */
    private boolean isPasswordLoginPost(ServletRequest request) {
        Map<String ,String> map = HttpUtils.getRequestBodyMap(request);
        String password = map.get("password");
        String timestamp = map.get("timestamp");
        String methodName = map.get("methodName");
        String appId = map.get("appId");
        return (request instanceof HttpServletRequest)
                && "POST".equalsIgnoreCase(((HttpServletRequest) request).getMethod())
                && password != null
                && timestamp != null
                && appId != null
                && "login".equals(methodName);
    }

    private boolean isAccountRegisterPost(ServletRequest request) {
        Map<String ,String> map = HttpUtils.getRequestBodyMap(request);
        String uid = map.get("uid");
        String username = map.get("username");
        String methodName = map.get("methodName");
        String password = map.get("password");
        return (request instanceof HttpServletRequest)
                && "POST".equalsIgnoreCase(((HttpServletRequest) request).getMethod())
                && username != null
                && password != null
                && uid != null
                && "register".equals(methodName);
    }

    private AuthenticationToken createPasswordToken(ServletRequest request) {
        Map<String, String> map = HttpUtils.getRequestBodyMap(request);
        String appId = map.get("appId");
        String timestamp = map.get("timestamp");
        String password = map.get("password");
        String userKey = map.get("userKey");
        String host = IpUtils.getIp(request);

        if (isEncryptPassword) {
            String tokenKey = redisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + host + userKey);
            password = AesUtils.aesDecode(password, tokenKey);
        }
        return new PasswordToken(appId, password, timestamp, host);
    }

    public PasswordFilter() {
    }

    // setter

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setEncryptPassword(boolean flag) {
        this.isEncryptPassword = flag;
    }

}
