package xyz.scootaloo.bootshiro.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 10:15
 */
@Data
public class AuthUserRole {

    private Integer id;
    private String  userId;
    private Integer roleId;
    private Date    createTime;
    private Date    updateTime;

}
