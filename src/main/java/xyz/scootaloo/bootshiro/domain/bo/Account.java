package xyz.scootaloo.bootshiro.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 13:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;
    private String password;
    private String salt;

}
