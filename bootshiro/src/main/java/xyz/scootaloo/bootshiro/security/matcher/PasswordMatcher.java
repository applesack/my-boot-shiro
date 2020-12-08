package xyz.scootaloo.bootshiro.security.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:05
 */
@Component
public class PasswordMatcher implements CredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        return token.getPrincipal().toString()
                .equals(info.getPrincipals()
                        .getPrimaryPrincipal().toString())
                && token.getCredentials().toString()
                        .equals(info.getCredentials().toString());
    }

}
