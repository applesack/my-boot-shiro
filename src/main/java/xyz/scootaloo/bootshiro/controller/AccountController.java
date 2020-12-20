package xyz.scootaloo.bootshiro.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.scootaloo.bootshiro.domain.bo.DVal;
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
import java.util.concurrent.TimeUnit;

/**
 * 管理账户的登陆和注册
 * 对于登陆和注册请求有效性的验证在PasswordFilter中已经完成。
 * 这个controller仅负责将用户数据写入数据库，并将处理的结果反馈给前端
 * @see xyz.scootaloo.bootshiro.security.filter.PasswordFilter
 * 在登陆或者注册过程中出现失败的状态码，状态码的含义请参考:
 * @see xyz.scootaloo.bootshiro.domain.bo.StatusCode
 * -------------------------------------------------
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 11:38
 */
@Slf4j
@Api("这个路径下的接口需要请求两次，第一次获取tokenKey和UserKey进行加密，第二次请求才完成功能")
@RestController
@RequestMapping("/account")
public class AccountController extends BaseHttpServ {
    // 一些常量
    private static final String APP_ID_STR = DVal.appId;
    private static final String JWT_SESSION_PREFIX = DVal.jwtSessionPrefix;
    private static final String TOKEN_KEY_PREFIX   = DVal.tokenKeyPrefix;
    private static final long REFRESH_PERIOD_TIME  = DVal.refreshPeriodTime;
    // 字符串常量
    private static final String USERNAME_STR  = DVal.username;
    private static final String REAL_NAME_STR = DVal.realName;
    private static final String AVATAR_STR    = DVal.avatar;
    private static final String PHONE_STR     = DVal.phone;
    private static final String EMAIL_STR     = DVal.email;
    private static final String SEX_STR       = DVal.sex;
    private static final String WHERE_STR     = DVal.createWhere;

    // services
    private StringRedisTemplate redisTemplate;
    private AccountService accountService;
    private UserService userService;
    private TaskManager taskManager;

    @Value("${bootshiro.enableEncryptPassword}")
    private boolean isEncryptPassword;

    @ApiOperation(value = "测试接口用", notes = "请求参数为 tokenKey=get, 请求不到controller就会被拦截处理")
    @GetMapping("/")
    public Message getTokenKey() {
        return Message.success();
    }

    @GetMapping("/login")
    public Message loginTokenKey() {
        return Message.success();
    }

    @GetMapping("/register")
    public Message registerTokenKey() {
        return Message.success();
    }

    @ApiOperation(value = "用户登陆", notes = "前置请求 GET: /account?tokenKey=get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "用户名", required = true),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true),
            @ApiImplicitParam(name = "userKey", value = "用户标识", required = true),
            @ApiImplicitParam(name = "timestamp", value = "时间戳", dataType = "long", required = true),
            @ApiImplicitParam(name = "methodName", value = "请求方法", defaultValue = "login", required = true),
    })
    @PostMapping("/login")
    public Message accountLogin(HttpServletRequest request) {
        Map<String, String> params = getRequestBody(request);
        String appId = params.get(APP_ID_STR);
        if (appId == null)
            return Message.of(StatusCode.ERROR_JWT);
        String roles = accountService.loadAccountRole(appId);
        String jwt = JwtUtils.issueJWT(appId)
                            .period(REFRESH_PERIOD_TIME >> 1) // 失效时间5小时
                            .roles(roles).create();
        // 存储在redis中，key=JWT-SESSION-{appId}， value=jwt， 过期时间=10小时
        redisTemplate.opsForValue().set(JWT_SESSION_PREFIX + appId, jwt, REFRESH_PERIOD_TIME, TimeUnit.SECONDS);
        // 获取这个用户
        AuthUser user = userService.getUserByAppId(appId);
        user.setPassword(null);
        user.setSalt(null);
        String ipAddress = IpUtils.getIp(request);

        // 提交写入日志任务，将本次操作的结果写入数据库
        log.info("登陆成功:" + ipAddress + "=" + user.getUsername());
        taskManager.executeTask(TaskFactory.loginLog(appId, ipAddress, (short) 1, "登陆成功"));
        return Message.of(StatusCode.ISSUED_JWT_SUCCESS)
                .addData("jwt", jwt)
                .addData("user", user);
    }

    @ApiOperation(value = "用户注册", notes = "前置请求 GET: /account?tokenKey=get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "主用户名", dataType = "string", required = true),
            @ApiImplicitParam(name = "password", value = "用户密码", dataType = "string", required = true),
            @ApiImplicitParam(name = "userKey", value = "用户标识", dataType = "string", required = true),
            @ApiImplicitParam(name = "timestamp", value = "时间戳", dataType = "long", required = true),
            @ApiImplicitParam(name = "methodName", value = "请求方法", defaultValue = "register", required = true),
            @ApiImplicitParam(name = "username", value = "昵称", dataType = "string"),
            @ApiImplicitParam(name = "real_name", value = "真实姓名", dataType = "string"),
            @ApiImplicitParam(name = "avatar", value = "头像路径", dataType = "string"),
            @ApiImplicitParam(name = "email", value = "电子邮箱", dataType = "string"),
            @ApiImplicitParam(name = "sex", value = "性别", dataType = "byte"),
            @ApiImplicitParam(name = "createWhere", value = "创建位置", dataType = "byte")
    })
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
        String ipAddress = IpUtils.getIp(request); // 获取请求中的ip地址

        if (isEncryptPassword) {
            // 从Redis取出密码传输加密解密秘钥
            String tokenKey = redisTemplate.opsForValue()
                    .get(TOKEN_KEY_PREFIX + ipAddress + userKey);
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
            log.info("注册成功:" + ipAddress + "=" + user.getUsername());
            return Message.of(StatusCode.REGISTER_SUCCESS);
        } else {
            taskManager.executeTask(TaskFactory.registerLog(uid, ipAddress, (short) 0, "注册失败"));
            log.debug("注册失败:" + ipAddress + "=" + user.getUsername());
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
