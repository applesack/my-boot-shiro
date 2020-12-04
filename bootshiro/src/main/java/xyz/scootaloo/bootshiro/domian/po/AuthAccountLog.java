package xyz.scootaloo.bootshiro.domian.po;

import lombok.Data;

import java.util.Date;

/**
 * 对应数据库中的auth_account_log数据表
 * 存储账号日志
 *
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:12
 */
@Data
public class AuthAccountLog {

    private Long   id;
    private String logName;
    private String userId;
    private Date   createTime;
    private Short  succeed;
    private String message;

}
