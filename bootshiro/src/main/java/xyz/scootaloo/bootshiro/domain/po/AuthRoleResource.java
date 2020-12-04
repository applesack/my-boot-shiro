package xyz.scootaloo.bootshiro.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:30
 */
@Data
public class AuthRoleResource {

    private Integer id;
    private Short   roleId;
    private Short   resourceId;
    private Date    createTime;
    private Date    updateTime;

}
