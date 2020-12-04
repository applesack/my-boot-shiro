package xyz.scootaloo.bootshiro.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:31
 */
@Data
public class AuthUser {

    private Integer uid;
    private String  username;
    private String  password;
    private String  salt;
    private String  realName;
    private String  avatar;
    private String  phone;
    private String  email;
    private Byte    sex;
    private Byte    status;
    private Date    createTime;
    private String  updateTime;
    private Byte    createWhere;

}
