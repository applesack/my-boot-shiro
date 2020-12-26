package xyz.scootaloo.bootshiro.resource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.scootaloo.bootshiro.domain.dto.MenuTreeNode;
import xyz.scootaloo.bootshiro.domain.po.AuthResource;
import xyz.scootaloo.bootshiro.service.ResourceService;
import xyz.scootaloo.bootshiro.utils.TreeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : ${YEAR}年${MONTH}月${DAY}日 ${TIME}
 */
@SpringBootTest
public class TestMenu {
    @Autowired
    private ResourceService resourceService;

    @Test
    public void testMenuLoad() {
        String uid = "admin";
        List<AuthResource> resources = resourceService.getAuthorityMenusByUid(uid);
        List<MenuTreeNode> treeNodes = resourceToNode(resources);

        List<MenuTreeNode> menuTreeNodes = TreeUtils.buildTreeList(treeNodes);
        System.out.println(menuTreeNodes);
    }

    private List<MenuTreeNode> resourceToNode(Collection<AuthResource> resources) {
        List<MenuTreeNode> nodeList = new ArrayList<>(resources.size());
        for (AuthResource resource : resources) {
            nodeList.add(MenuTreeNode.of(resource));
        }
        return nodeList;
    }

}
