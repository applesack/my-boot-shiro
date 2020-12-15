package xyz.scootaloo.bootshiro.domain.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:28
 */
@Data
@ApiModel(description = "角色")
public class AuthRole {

    @ApiModelProperty(value = "角色对应的编号")
    private Integer Id;
    @ApiModelProperty(value = "角色代号")
    private String  code;
    @ApiModelProperty(value = "角色信息描述")
    private String  name;
    @ApiModelProperty(value = "角色状态")
    private Short   status;
    @ApiModelProperty(value = "创建此角色是日期")
    private Date    createTime;
    @ApiModelProperty(value = "修改此角色的日期")
    private Date    updateTime;

}
