package xyz.scootaloo.bootshiro.domain.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.scootaloo.bootshiro.domain.po.AuthResource;

import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月14日 13:16
 */
@Getter
@Setter
public class MenuTreeNode extends BaseTreeNode {

    private String code;
    private String name;
    private String uri;
    private Short  type;
    private String method;
    private String icon;
    private Short  status;
    private Date   createTime;
    private Date   updateTime;

    public MenuTreeNode() {
    }

    public static MenuTreeNode of(AuthResource resource) {
        MenuTreeNode node = new MenuTreeNode();
        node.setCode(resource.getCode());
        node.setName(resource.getName());
        node.setUri(resource.getUri());
        node.setType(resource.getType());
        node.setMethod(resource.getMethod());
        node.setIcon(resource.getIcon());
        node.setStatus(resource.getStatus());
        node.setCreateTime(resource.getCreateTime());
        node.setUpdateTime(resource.getUpdateTime());
        node.setParentId(resource.getParentId());
        node.setId(resource.getId());
        return node;
    }

}
