package xyz.scootaloo.bootshiro.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.bo.StatusCode;
import xyz.scootaloo.bootshiro.security.token.JwtToken;
import xyz.scootaloo.bootshiro.service.AccountService;
import xyz.scootaloo.bootshiro.support.TaskManager;
import xyz.scootaloo.bootshiro.support.factory.TaskFactory;
import xyz.scootaloo.bootshiro.utils.HttpUtils;
import xyz.scootaloo.bootshiro.utils.IpUtils;
import xyz.scootaloo.bootshiro.utils.JwtUtils;
import xyz.scootaloo.bootshiro.utils.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 14:35
 */
@Slf4j
public class BonJwtFilter extends AbstractPathMatchingFilter {
    private static final String STR_EXPIRED = "expiredJwt";
    private static final String JWT_SESSION_PREFIX = "JWT-SESSION-";
    private static final String APP_ID = "appId";
    private static final String AUTHORIZATION = "authorization";
    private static final String DEVICE_INFO = "deviceInfo";

    private StringRedisTemplate redisTemplate;
    private AccountService accountService;
    private TaskManager taskManager;

    /**
     *
     * @param servletRequest just a request
     * @param servletResponse just a response
     * @param mappedValue just a object
     * @return false 拦截; true 进一步处理
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse,
                                      Object mappedValue) {
        Subject subject = getSubject();
        HttpServletRequest request = WebUtils.getHttpRequest(servletRequest);
        HttpServletResponse response = WebUtils.getHttpResponse(servletRequest);

        //记录调用api日志到数据库
        taskManager.executeTask(TaskFactory.businessLog(request.getHeader(APP_ID),
                request.getRequestURI(), request.getMethod(), (short) 1,null));

        // 判断是否为JWT认证请求
        boolean isJwtPost = subject != null && !subject.isAuthenticated() && isJwtSubmission(request);
        if (isJwtPost) {
            AuthenticationToken token = createJwtToken(request);
            try {
                subject.login(token);
                return this.checkRoles(subject, mappedValue);
            } catch (AuthenticationException e) {
                return exceptionHandle(e, request, response);
            } catch (Exception e) {
                // 其他错误
                log.error(IpUtils.getIp(request) + "--JWT认证失败" + e.getMessage(), e);
                // 告知客户端JWT错误1005,需重新登录申请jwt
                HttpUtils.responseWrite(response, Message.of(StatusCode.ERROR_JWT));
                return false;
            }
        } else {
            // 请求未携带jwt 判断为无效请求
            HttpUtils.responseWrite(response, Message.failure());
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject();

        // 未认证的情况上面已经处理  这里处理未授权
        if (subject != null && subject.isAuthenticated()){
            //  已经认证但未授权的情况
            // 告知客户端JWT没有权限访问此资源
            HttpUtils.responseWrite(response, Message.of(StatusCode.NO_PERMISSION));
        }
        // 过滤链终止
        return false;
    }

    private boolean exceptionHandle(AuthenticationException e,
                                    HttpServletRequest request, HttpServletResponse response) {
        // 如果是JWT过期
        if (STR_EXPIRED.equals(e.getMessage())) {
            // 这里初始方案先抛出令牌过期，之后设计为在Redis中查询当前appId对应令牌，其设置的过期时间是JWT的两倍，此作为JWT的refresh时间
            // 当JWT的有效时间过期后，查询其refresh时间，refresh时间有效即重新派发新的JWT给客户端，
            // refresh也过期则告知客户端JWT时间过期重新认证

            // 当存储在redis的JWT没有过期，即refresh time 没有过期
            String appId = request.getHeader("appId");
            String jwt = request.getHeader("authorization");
            String refreshJwt = redisTemplate.opsForValue().get(JWT_SESSION_PREFIX + appId);
            if (null != refreshJwt && refreshJwt.equals(jwt)) {
                // 重新申请新的JWT
                // 根据appId获取其对应所拥有的角色(这里设计为角色对应资源，没有权限对应资源)
                String roles = accountService.loadAccountRole(appId);
                //seconds为单位,10 hours
                long refreshPeriodTime = 36000L;
                String newJwt = JwtUtils.issueJWT(appId)
                        .period(refreshPeriodTime >> 1)
                        .roles(roles)
                        .create();
                // 将签发的JWT存储到Redis： key=JWT-SESSION-{appID}, value={newJwt}
                redisTemplate.opsForValue()
                        .set(JWT_SESSION_PREFIX + appId, newJwt, refreshPeriodTime, TimeUnit.SECONDS);
                HttpUtils.responseWrite(response, Message.of(StatusCode.NEW_JWT).addData("jwt", newJwt));
            } else {
                // jwt时间失效过期,jwt refresh time失效 返回jwt过期客户端重新登录
                HttpUtils.responseWrite(response, Message.of(StatusCode.EXPIRED_JWT));
            }
            return false;
        }
        // 其他的判断为JWT错误无效
        HttpUtils.responseWrite(response, Message.of(StatusCode.ERROR_JWT));
        return false;
    }

    /**
     * 检查请求头中是否有指定的属性
     * @param request just a request
     * @return header必须要有authorization和appId属性
     */
    private boolean isJwtSubmission(ServletRequest request) {
        String jwt = HttpUtils.getHeader(request, AUTHORIZATION);
        String appId = HttpUtils.getHeader(request, APP_ID);
        return !StringUtils.isEmpty(jwt) && !StringUtils.isEmpty(appId);
    }

    // 用请求头中的信息创建一个token对象
    private AuthenticationToken createJwtToken(ServletRequest request) {
        Map<String,String> maps = HttpUtils.getRequestHeader(request);
        String appId = maps.get(APP_ID);
        String ipHost = request.getRemoteAddr();
        String jwt = maps.get(AUTHORIZATION);
        String deviceInfo = maps.get(DEVICE_INFO);

        return new JwtToken(ipHost, deviceInfo, jwt, appId);
    }

    // 验证当前用户是否属于mappedValue任意一个角色
    private boolean checkRoles(Subject subject, Object mappedValue){
        String[] rolesArray = (String[]) mappedValue;
        return rolesArray == null || rolesArray.length == 0
                || Stream.of(rolesArray).anyMatch(role -> subject.hasRole(role.trim()));
    }

    // setter

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

}
