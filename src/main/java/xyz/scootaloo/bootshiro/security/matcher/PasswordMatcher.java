package xyz.scootaloo.bootshiro.security.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;

/**
 * 自定义的匹配器，匹配登陆者的用户名和密码
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:05
 */
@Component
public class PasswordMatcher implements CredentialsMatcher {

    /**
     * @param token 由{@link xyz.scootaloo.bootshiro.security.filter.PasswordFilter}中生成，
     *              在执行subject.login之前，PasswordFilter会根据request中的appId、password，userKey等属性创建的对象。
     * @param info 由{@link xyz.scootaloo.bootshiro.security.realm.PasswordRealm}中生成，
     *             在这个方法内对token中的用户名向数据库内查找对应的用户信息，这两个对象都包含用户名和密码。
     * @return 主要是匹配用户名和密码，两者都一致则为true
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        return token.getPrincipal().toString()
                .equals(info.getPrincipals()
                        .getPrimaryPrincipal().toString())
                && token.getCredentials().toString()
                        .equals(info.getCredentials().toString());
    }

}
