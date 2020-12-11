package xyz.scootaloo.bootshiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.bo.StatusCode;
import xyz.scootaloo.bootshiro.domain.po.AuthUser;
import xyz.scootaloo.bootshiro.service.AccountService;
import xyz.scootaloo.bootshiro.service.UserService;
import xyz.scootaloo.bootshiro.support.TaskManager;
import xyz.scootaloo.bootshiro.support.VarInspector;
import xyz.scootaloo.bootshiro.support.factory.TaskFactory;
import xyz.scootaloo.bootshiro.utils.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 管理账户的登陆和注册
 * 在登陆或者注册过程中出现失败的状态码，状态码的含义请参考:
 * @see xyz.scootaloo.bootshiro.domain.bo.StatusCode
 * -------------------------------------------------
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
    // 字符串常量
    private static final String USERNAME_STR  = "username";
    private static final String REAL_NAME_STR = "realName";
    private static final String AVATAR_STR    = "avatar";
    private static final String PHONE_STR     = "phone";
    private static final String EMAIL_STR     = "email";
    private static final String SEX_STR       = "sex";
    private static final String WHERE_STR     = "createWhere";

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
        // 存储在redis中，key=JWT-SESSION-{appId}， value=jwt， 过期时间=10小时
        redisTemplate.opsForValue().set(JWT_SESSION_PREFIX + appId, jwt, REFRESH_PERIOD_TIME, TimeUnit.SECONDS);
        // 获取这个用户
        AuthUser user = userService.getUserByAppId(appId);
        user.setPassword(null);
        user.setSalt(null);
        // 提交写入日志任务，将本次操作的结果写入数据库
        taskManager.executeTask(TaskFactory.loginLog(appId, IpUtils.getIp(request), (short) 1, "登陆成功"));
        return Message.of(StatusCode.ISSUED_JWT_SUCCESS)
                .addData("jwt", jwt)
                .addData("user", user);
    }

    @PostMapping("/register")
    public Message accountRegister(HttpServletRequest request) {
        Map<String, String> params = getRequestBody(request);
        AuthUser user = new AuthUser();

        String uid = params.get("uid");
        String password = params.get("password");
        String userKey = params.get("userKey");

        // 用户信息不完整
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(params)) {
            return Message.of(StatusCode.LACK_ACCOUNT_INFO);
        }
        // 账号已存在
        if (accountService.isAccountExistByUid(uid)) {
            return Message.of(StatusCode.ACCOUNT_EXIST);
        }

        user.setUid(uid);
        String ipAddress = IpUtils.getIp(request).toUpperCase(); // 获取请求中的ip地址

        if (isEncryptPassword) {
            // 从Redis取出密码传输加密解密秘钥
            String tokenKey = redisTemplate.opsForValue()
                    .get("TOKEN_KEY_" + ipAddress + userKey);
            password = AesUtils.aesDecode(password, tokenKey);
        }
        // 存储到数据库的密码为 MD5(原密码+盐值)
        String salt = Commons.getRandomStr(6);
        user.setPassword(Md5Utils.md5(password + salt));
        user.setSalt(salt);
        user.setCreateTime(new Date());

        // 将params中不为空的属性设置入user对象
        VarInspector<AuthUser> checker = new VarInspector<>(user);
        checker.ifNotEmptyThenSet(params.get(USERNAME_STR), user::setUsername);
        checker.ifNotEmptyThenSet(params.get(REAL_NAME_STR), user::setRealName);
        checker.ifNotEmptyThenSet(params.get(AVATAR_STR), user::setAvatar);
        checker.ifNotEmptyThenSet(params.get(PHONE_STR), user::setPhone);
        checker.ifNotEmptyThenSet(params.get(EMAIL_STR), user::setEmail);
        checker.ifNotEmptyThenSet(params.get(SEX_STR), (value) -> user.setSex(Byte.valueOf(value)));
        checker.ifNotEmptyThenSet(params.get(WHERE_STR), (value) -> user.setCreateWhere(Byte.valueOf(value)));
        user.setStatus((byte) 1);

        if (accountService.registerAccount(user)) {
            taskManager.executeTask(TaskFactory.registerLog(uid, ipAddress, (short) 1, "注册成功"));
            return Message.of(StatusCode.REGISTER_SUCCESS);
        } else {
            taskManager.executeTask(TaskFactory.registerLog(uid, ipAddress, (short) 0, "注册失败"));
            return Message.of(StatusCode.REGISTER_FAILURE);
        }
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
