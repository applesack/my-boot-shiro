package xyz.scootaloo.bootshiro.utils;

import xyz.scootaloo.bootshiro.domain.dto.BaseTreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月14日 14:24
 */
public class TreeUtils {
    // 默认假如一个菜单的parentId是-1的时候，那么这个菜单是根节点
    private static final int BASE_ID = -1;

    // 原算法
    public static <T extends BaseTreeNode> List<T> buildTreeBy2Loop(List<T> treeNodes, Object root) {
        List<T> trees = new ArrayList<>();
        for (T node : treeNodes) {
            if (root.equals(node.getParentId())) {
                trees.add(node);
            }

            for (T treeNode : treeNodes) {
                if (node.getId().equals(treeNode.getParentId())) {
                    if (node.getChildren() == null) {
                        node.setChildren(new ArrayList<>());
                    }
                    node.addChild(treeNode);
                }
            }
        }
        return trees;
    }

    /**
     * 备用算法，双重循环改成两次循环
     * 时间复杂度: O(N^2) -> O(N)
     * 空间复杂度: O(1) -> O(N)
     * @param treeNodes 节点集合
     * @param <T> the type extends BaseTreeNode.
     * @return 父节点列表，每个父节点包含对应的子节点
     */
    public static <T extends BaseTreeNode> List<T> buildTreeList(List<T> treeNodes) {
        Map<Integer, T> cache = new HashMap<>(16);
        List<T> childrenNodes = new ArrayList<>(16);
        List<T> parentNodes = new ArrayList<>(8);
        for (T node : treeNodes) {
            // 假如这个节点是根节点，则插入到父节点集合，同时缓存起来
            if (node.getId() == BASE_ID) {
                cache.put(node.getId(), node);
                parentNodes.add(node);
            } else {
                // 否则这个节点是一个子节点，存放到子节点集合中
                childrenNodes.add(node);
            }
        }

        // 遍历子节点，查找它们的父节点，将子节点绑定到父节点
        for (T child : childrenNodes) {
            T parentNode = cache.get(child.getParentId());
            if (parentNode != null) {
                parentNode.addChild(child);
            }
        }

        // 返回结果: 父节点集合
        return parentNodes;
    }

}
