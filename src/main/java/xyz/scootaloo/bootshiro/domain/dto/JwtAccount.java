package xyz.scootaloo.bootshiro.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 9:40
 */
@Data
public class JwtAccount implements Serializable {

    private static final long serialVersionUID = -895875540581785581L;

    private String tokenId;  // 令牌Id
    private String appId;    // 客户标识(用户名、账号)
    private String issuer;   // 签发者(JWT令牌此项有值)
    private Date   issuedAt; // 签发时间
    private String audience; // 接收方(JWT令牌此项有值)
    private String roles;    // 访问主张-角色(JWT令牌此项有值)
    private String perms;    // 访问主张-资源(JWT令牌此项有值)
    private String host;     // 客户地址

}
