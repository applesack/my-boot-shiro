package xyz.scootaloo.bootshiro.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.bo.Role;
import xyz.scootaloo.bootshiro.domain.po.AuthResource;
import xyz.scootaloo.bootshiro.domain.po.AuthRole;
import xyz.scootaloo.bootshiro.domain.po.AuthUser;
import xyz.scootaloo.bootshiro.security.filter.ShiroFilterChainManager;
import xyz.scootaloo.bootshiro.service.ResourceService;
import xyz.scootaloo.bootshiro.service.RoleService;
import xyz.scootaloo.bootshiro.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月14日 18:11
 */
@Api("角色操作")
@RestController
@RequestMapping("/role")
public class RoleController extends BaseHttpServ {
    // services
    private RoleService roleService;
    private UserService userService;
    private ResourceService resourceService;

    // 过滤链管理器
    private ShiroFilterChainManager filterChainManager;

    @ApiOperation(value = "获取角色关联的(roleId)对应用户列表")
    @GetMapping("/user/{roleId}/{currentPage}/{pageSize}")
    public Message getUserListByRoleId(@PathVariable Integer roleId,
                                       @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthUser> users = userService.getUserListByRoleId(Role.of(roleId));
        users.forEach(user -> user.setPassword(null));
        return Message.success()
                .addData("data", new PageInfo<>(users));
    }

    @ApiOperation(value = "获取角色未关联的用户列表")
    @GetMapping("/user/-/{roleId}/{currentPage}/{pageSize}")
    public Message getUserListExtendByRoleId(@PathVariable Integer roleId,
                                             @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthUser> users = userService.getNotAuthorityUserListByRoleId(Role.of(roleId));
        users.forEach(user -> user.setPassword(null));
        return Message.success()
                .addData("data", new PageInfo<>(users));
    }

    @ApiOperation(value = "获取角色(roleId)所被授权的API资源")
    @GetMapping("/api/{roleId}/{currentPage}/{pageSize}")
    public Message getRestApiExtendByRoleId(@PathVariable Integer roleId,
                                            @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthResource> authResources = resourceService.getAuthorityApisByRoleId(roleId);
        return Message.success()
                .addData("data", new PageInfo<>(authResources));
    }

    @ApiOperation(value = "获取角色(roleId)未被授权的API资源")
    @GetMapping("/api/-/{roleId}/{currentPage}/{pageSize}")
    public Message getRestApiByRoleId(@PathVariable Integer roleId,
                                      @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthResource> authResources = resourceService.getNotAuthorityApisByRoleId(roleId);
        return Message.success()
                .addData("data", new PageInfo<>(authResources));
    }

    @ApiOperation(value = "获取角色(roleId)所被授权的menu资源")
    @GetMapping("/menu/{roleId}/{currentPage}/{pageSize}")
    public Message getMenusByRoleId(@PathVariable Integer roleId,
                                    @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthResource> authResources = resourceService.getAuthorityMenusByRoleId(roleId);
        return Message.success()
                .addData("data", new PageInfo<>(authResources));
    }

    @ApiOperation(value = "获取角色(roleId)未被授权的menu资源")
    @GetMapping("/menu/-/{roleId}/{currentPage}/{pageSize}")
    public Message getMenusExtendByRoleId(@PathVariable Integer roleId,
                                          @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthResource> authResources = resourceService.getNotAuthorityMenusByRoleId(roleId);
        return Message.success()
                .addData("data", new PageInfo<>(authResources));
    }

    @ApiOperation(value = "授权资源给角色")
    @PostMapping("/authority/resource")
    public Message authorityRoleResource(HttpServletRequest request) {
        Map<String, String> map = getRequestBody(request);
        int roleId = Integer.parseInt(map.get("roleId"));
        int resourceId = Integer.parseInt(map.get("resourceId"));
        boolean flag = roleService.authorityRoleResource(roleId, resourceId);
        filterChainManager.reloadFilterChain();
        return Message.expression(flag);
    }

    @ApiOperation(value = "删除对应的角色的授权资源")
    @DeleteMapping("/authority/resource/{roleId}/{resourceId}")
    public Message deleteAuthorityRoleResource(@PathVariable Integer roleId, @PathVariable Integer resourceId ) {
        boolean flag = roleService.deleteAuthorityRoleResource(roleId,resourceId);
        filterChainManager.reloadFilterChain();
        return Message.expression(flag);
    }

    @ApiOperation(value = "获取角色LIST")
    @GetMapping("/{currentPage}/{pageSize}")
    public Message getRoles(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthRole> roles = roleService.getRoleList();
        return Message.success()
                .addData("data", new PageInfo<>(roles));
    }

    @ApiOperation(value = "添加角色")
    @PostMapping("/")
    public Message addRole(@RequestBody AuthRole role) {
        boolean flag = roleService.addRole(role);
        return Message.expression(flag);
    }

    @ApiOperation(value = "更新角色")
    @PutMapping("/")
    public Message updateRole(@RequestBody AuthRole role) {
        boolean flag = roleService.updateRole(role);
        return Message.expression(flag);
    }

    @ApiOperation(value = "根据角色ID删除角色")
    @DeleteMapping("/{roleId}")
    public Message deleteRoleByRoleId(@PathVariable Integer roleId) {
        boolean flag = roleService.deleteRoleByRoleId(roleId);
        return Message.expression(flag);
    }

    // setter

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setFilterChainManager(ShiroFilterChainManager filterChainManager) {
        this.filterChainManager = filterChainManager;
    }

}
