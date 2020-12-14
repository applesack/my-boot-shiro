package xyz.scootaloo.bootshiro.domain.po;

import lombok.Data;

import java.util.Date;

/**
 * 对应数据库中的auth_operation_log数据表
 * ---------------------------------
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:16
 */
@Data
public class AuthOperationLog {

    private Integer id;
    private String  logName;
    private String  userId;
    private String  api;
    private String  method;
    private Date    createTime;
    private Short   succeed;
    private String  message;

}
