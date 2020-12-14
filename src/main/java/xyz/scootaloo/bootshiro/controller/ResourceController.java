package xyz.scootaloo.bootshiro.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.dto.MenuTreeNode;
import xyz.scootaloo.bootshiro.domain.po.AuthResource;
import xyz.scootaloo.bootshiro.service.ResourceService;
import xyz.scootaloo.bootshiro.utils.TreeUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月14日 12:55
 */
@Slf4j
@RestController
@RequestMapping("/resource")
public class ResourceController extends BaseHttpServ {

    private ResourceService resourceService;

    @GetMapping("/authorityMenu")
    public Message getAuthorityMenu(HttpServletRequest request) {
        String uid = request.getHeader("appId");
        List<AuthResource> resources = resourceService.getAuthorityMenusByUid(uid);
        List<MenuTreeNode> treeNodes = resourceToNode(resources);

        List<MenuTreeNode> menuTreeNodes = TreeUtils.buildTreeBy2Loop(treeNodes, -1);
        return Message.success()
                .addData("menuTree", menuTreeNodes);
    }

    @GetMapping("/menus")
    public Message getMenus() {
        List<AuthResource> resources = resourceService.getMenus();
        List<MenuTreeNode> treeNodes = resourceToNode(resources);

        List<MenuTreeNode> menuTreeNodes = TreeUtils.buildTreeBy2Loop(treeNodes,-1);
        return Message.success()
                .addData("menuTree", menuTreeNodes);
    }

    @PostMapping("/menu")
    public Message addMenu(@RequestBody AuthResource menu) {
        boolean flag = resourceService.addMenu(menu);
        return Message.expression(flag);
    }

    @PutMapping("/menu")
    public Message updateMenu(@RequestBody AuthResource menu) {
        boolean flag = resourceService.modifyMenu(menu);
        return Message.expression(flag);
    }

    @DeleteMapping("/menu/{menuId}")
    public Message deleteMenuByMenuId(@PathVariable Integer menuId) {
        boolean flag = resourceService.deleteMenuByMenuId(menuId);
        return Message.expression(flag);
    }

    @GetMapping("/api/{teamId}/{currentPage}/{pageSize}")
    public Message getApiList(@PathVariable Integer teamId,
                              @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        List<AuthResource> resources;
        PageHelper.startPage(currentPage, pageSize);
        switch (teamId) {
            case -1: { // 获取api分类
                resources = resourceService.getApiTeamList();
                return Message.success()
                        .addData("data",resources);
            }
            case 0: { // 获取全部api
                resources = resourceService.getApiList();
            } break;
            default: { // 其他,查询teamId对应分类下的apis
                resources = resourceService.getApiListByTeamId(teamId);
            }
        }

        return Message.success()
                .addData("data", new PageInfo<>(resources));
    }

    @PostMapping("/api")
    public Message addApi(@RequestBody AuthResource api) {
        boolean flag = resourceService.addMenu(api);
        return Message.expression(flag);
    }

    @PutMapping("/api")
    public Message updateApi(@RequestBody AuthResource api) {
        boolean flag = resourceService.modifyMenu(api);
        return Message.expression(flag);
    }

    @DeleteMapping("/api/{apiId}")
    public Message deleteApiByApiId(@PathVariable Integer apiId) {
        boolean flag = resourceService.deleteMenuByMenuId(apiId);
        return Message.expression(flag);
    }

    /**
     * 使用静态工厂方法实现集合转换
     * @param resources 资源
     * @return 含有资源的节点
     */
    private List<MenuTreeNode> resourceToNode(Collection<AuthResource> resources) {
        List<MenuTreeNode> nodeList = new ArrayList<>(resources.size());
        for (AuthResource resource : resources) {
            nodeList.add(MenuTreeNode.of(resource));
        }
        return nodeList;
    }

    // setter

    @Autowired
    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

}
