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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 11:38
 */
@RestController
@RequestMapping("/account")
public class AccountController extends BaseHttpServ {

    // 一些常量
    private static final String APP_ID_STR = "appId";
    private static final long REFRESH_PERIOD_TIME = 36000L;

    // services
    private StringRedisTemplate redisTemplate;
    private AccountService accountService;
    private UserService userService;

    @Value("${bootshiro.enableEncryptPassword}")
    private boolean isEncryptPassword;

    @PostMapping("/login")
    public Message accountLogin(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = getRequestParameter(request);
        String appId = params.get(APP_ID_STR);
        if (appId == null)
            return Message.of(StatusCode.ERROR_JWT);
        String roles = accountService.loadAccountRole(APP_ID_STR);
        String jwt = null;
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

}
