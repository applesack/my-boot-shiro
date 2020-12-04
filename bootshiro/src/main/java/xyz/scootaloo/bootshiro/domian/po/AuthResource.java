package xyz.scootaloo.bootshiro.domian.po;

import lombok.Data;

import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:23
 */
@Data
public class AuthResource {

    private Integer id;
    private String  code;
    private String  name;
    private String  parentId;
    private String  uri;
    private Short   type;
    private String  method;
    private String  icon;
    private Short   status;
    private Date    createTime;
    private Date    updateTime;

}
