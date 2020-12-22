package xyz.scootaloo.bootshiro.domain.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象树节点
 * 用于表示多级菜单，每个菜单都有一个id和parentId，还有一些子节点
 * @author : flutterdash@qq.com
 * @since : 2020年12月14日 13:00
 */
@Data
public abstract class BaseTreeNode {

    protected Integer id;
    protected Integer parentId;
    protected List<BaseTreeNode> children = new ArrayList<>();

    public void addChild(BaseTreeNode child) {
        this.children.add(child);
    }

}
