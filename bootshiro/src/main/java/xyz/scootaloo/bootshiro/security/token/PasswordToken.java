package xyz.scootaloo.bootshiro.security.token;

import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import xyz.scootaloo.bootshiro.security.provider.AccountProvider;

import java.io.Serializable;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:36
 */
@Getter
@Setter
public class PasswordToken implements AuthenticationToken, Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;
    private String password;
    private String timestamp;
    private String host;

    @Override
    public Object getPrincipal() {
        return this.appId;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }

    public PasswordToken(String appId, String password, String timestamp, String host) {
        this.appId = appId;
        this.timestamp = timestamp;
        this.host = host;
        this.password = password;

    }
}
