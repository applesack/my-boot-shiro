package xyz.scootaloo.bootshiro.domain.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:23
 */
@Data
@ApiModel(description = "资源")
public class AuthResource {

    @ApiModelProperty(value = "资源的编号")
    private Integer id;
    @ApiModelProperty(value = "资源代号")
    private String  code;
    @ApiModelProperty(value = "资源描述")
    private String  name;
    @ApiModelProperty(value = "做为菜单项时，此资源的父节点的编号")
    private Integer parentId;
    @ApiModelProperty(value = "资源的路径")
    private String  uri;
    @ApiModelProperty(value = "资源的类型")
    private Short   type;
    @ApiModelProperty(value = "请求该资源指定的方法")
    private String  method;
    @ApiModelProperty(value = "资源的图标路径")
    private String  icon;
    @ApiModelProperty(value = "资源的状态")
    private Short   status;
    @ApiModelProperty(value = "资源创建的日期")
    private Date    createTime;
    @ApiModelProperty(value = "资源修改的日期")
    private Date    updateTime;

}
