package xyz.scootaloo.bootshiro.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 存储数据表中user表记录的简要信息
 * 用于在用户登陆时拿这些信息和用户提交的用户密码进行匹配
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 13:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;    // 用户标识，一般指用户名
    private String password; // 用户密码
    private String salt;     // 加密密码所用的盐值

}
