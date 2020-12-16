package xyz.scootaloo.bootshiro.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.bo.Role;
import xyz.scootaloo.bootshiro.domain.bo.StatusCode;
import xyz.scootaloo.bootshiro.domain.po.AuthUser;
import xyz.scootaloo.bootshiro.service.UserService;
import xyz.scootaloo.bootshiro.support.TaskManager;
import xyz.scootaloo.bootshiro.support.factory.TaskFactory;
import xyz.scootaloo.bootshiro.utils.IpUtils;
import xyz.scootaloo.bootshiro.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月14日 10:10
 */
@Slf4j
@Api("用户管理")
@RestController
@RequestMapping("/user")
public class UserController extends BaseHttpServ {
    private static final String JWT_SESSION_PREFIX = "JWT-SESSION-";

    private UserService userService;
    private TaskManager taskManager;
    private StringRedisTemplate redisTemplate;

    @ApiOperation(value = "获取对应用户角色", notes = "GET根据用户的appId获取对应用户的角色")
    @GetMapping("/role/{appId}")
    public Message getUserRoleList(@PathVariable String appId) {
        String roles = userService.loadAccountRole(appId);
        Set<String> roleSet = StringUtils.splitAndToSet(roles, ',');
        return Message.success()
                .addData("roles", roleSet);
    }

    @ApiOperation(value = "获取用户列表", notes = "GET获取所有注册用户的信息列表")
    @GetMapping("/list/{start}/{limit}")
    public Message getUserList(@PathVariable Integer start, @PathVariable Integer limit) {
        PageHelper.startPage(start, limit);
        List<AuthUser> userList = userService.getUserList();
        userList.forEach(user -> user.setPassword(null));
        return Message.success()
                .addData("pageInfo", new PageInfo<>(userList));
    }

    @ApiOperation(value = "给用户授权添加角色")
    @PostMapping("/authority/role")
    public Message authorityUserRole(HttpServletRequest request) {
        Map<String, String> bodyMap = getRequestBody(request);
        String uid = bodyMap.get("uid");
        int roleId = Integer.parseInt(bodyMap.get("roleId"));
        boolean flag = userService.authorityUserRole(uid, Role.of(roleId));
        return Message.expression(flag);
    }

    @ApiOperation(value = "删除已经授权的用户角色")
    @DeleteMapping("/authority/role/{uid}/{roleId}")
    public Message deleteAuthorityUserRole(@PathVariable String uid, @PathVariable Integer roleId) {
        boolean flag = userService.deleteAuthorityUserRole(uid, Role.of(roleId));
        return Message.expression(flag);
    }

    @ApiOperation(value = "用户登出")
    @PostMapping("/exit")
    public Message accountExit(HttpServletRequest request) {
        SecurityUtils.getSubject().logout();
        Map<String, String> bodyMap = getRequestBody(request);
        String appId = bodyMap.get("appId");
        if (StringUtils.isEmpty(appId)) {
            return Message.of(StatusCode.LOGOUT_ERROR);
        }
        String jwt = redisTemplate.opsForValue().get(JWT_SESSION_PREFIX + appId);
        if (StringUtils.isEmpty(jwt)) {
            return Message.of(StatusCode.LOGOUT_ERROR);
        }

        // 将用户数据从缓存中删除，将结果写入日志
        redisTemplate.opsForValue().getOperations().delete(JWT_SESSION_PREFIX + appId);
        taskManager.executeTask(TaskFactory.logoutLog(appId, IpUtils.getIp(request), (short) 1, ""));
        return Message.of(StatusCode.LOGOUT_SUCCESS);
    }

    // setter

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
