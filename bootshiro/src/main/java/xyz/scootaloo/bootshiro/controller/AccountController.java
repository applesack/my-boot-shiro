package xyz.scootaloo.bootshiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.bo.StatusCode;
import xyz.scootaloo.bootshiro.service.AccountService;
import xyz.scootaloo.bootshiro.service.UserService;
import xyz.scootaloo.bootshiro.support.TaskManager;
import xyz.scootaloo.bootshiro.support.factory.TaskFactory;
import xyz.scootaloo.bootshiro.utils.IpUtils;
import xyz.scootaloo.bootshiro.utils.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 11:38
 */
@RestController
@RequestMapping("/account")
public class AccountController extends BaseHttpServ {

    // 一些常量
    private static final String APP_ID_STR = "appId";
    private static final String JWT_SESSION_PREFIX = "JWT-SESSION-";
    private static final long REFRESH_PERIOD_TIME = 36000L;

    // services
    private StringRedisTemplate redisTemplate;
    private AccountService accountService;
    private UserService userService;
    private TaskManager taskManager;

    @Value("${bootshiro.enableEncryptPassword}")
    private boolean isEncryptPassword;

    @PostMapping("/login")
    public Message accountLogin(HttpServletRequest request) {
        Map<String, String> params = getRequestParameter(request);
        String appId = params.get(APP_ID_STR);
        if (appId == null)
            return Message.of(StatusCode.ERROR_JWT);
        String roles = accountService.loadAccountRole(APP_ID_STR);
        String jwt = JwtUtils.issueJWT(appId)
                            .id(UUID.randomUUID().toString())
                            .period(REFRESH_PERIOD_TIME >> 1) // 失效时间5小时
                            .roles(roles)
                            .permissions(null)
                            .create();
        // 存储在redis中，key=JWT-SESSION-{appId}， value =jwt， 过期时间=10小时
        redisTemplate.opsForValue().set(JWT_SESSION_PREFIX + appId, jwt, REFRESH_PERIOD_TIME, TimeUnit.SECONDS);
        // 提交写入日志任务，将本次操作的结果写入数据库
        taskManager.executeTask(TaskFactory.loginLog(appId, IpUtils.getIp(request), (short) 1, "登陆成功"));
        return Message.success();
    }

    // setter

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

}
