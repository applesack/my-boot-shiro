package xyz.scootaloo.bootshiro.security.token;

import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.AuthenticationToken;

import java.io.Serializable;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:59
 */
@Getter
@Setter
public class JwtToken implements AuthenticationToken, Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;      // 用户的标识
    private String ipHost;     // 用户的IP
    private String deviceInfo; // 设备信息
    private String jwt;        // json web token值

    @Override
    public Object getPrincipal() {
        return this.appId;
    }

    @Override
    public Object getCredentials() {
        return this.jwt;
    }

    public JwtToken(String ipHost, String deviceInfo, String jwt,String appId) {
        this.ipHost = ipHost;
        this.deviceInfo = deviceInfo;
        this.jwt = jwt;
        this.appId = appId;
    }

}
