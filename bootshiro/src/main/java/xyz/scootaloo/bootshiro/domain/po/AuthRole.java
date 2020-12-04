package xyz.scootaloo.bootshiro.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:28
 */
@Data
public class AuthRole {

    private Integer Id;
    private String  code;
    private String  name;
    private Short   status;
    private Date    createTime;
    private Date    updateTime;

}
