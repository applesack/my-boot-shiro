package xyz.scootaloo.bootshiro.security.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import xyz.scootaloo.bootshiro.security.provider.AccountProvider;
import xyz.scootaloo.bootshiro.security.token.PasswordToken;
import xyz.scootaloo.bootshiro.utils.Md5Utils;
import xyz.scootaloo.bootshiro.domain.bo.Account;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:32
 */
public class PasswordRealm extends AuthorizingRealm {

    private AccountProvider accountProvider;

    @Override
    public Class<?> getAuthenticationTokenClass() {
        return PasswordToken.class;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (!(token instanceof PasswordToken)) {
            return null;
        }
        if(null == token.getPrincipal() || null == token.getCredentials()) {
            throw new UnknownAccountException();
        }

        String appId = (String)token.getPrincipal();
        Account account = accountProvider.loadAccount(appId);
        if (account != null) {
            // 用盐对密码进行MD5加密
            ((PasswordToken) token).setPassword(Md5Utils.md5(((PasswordToken) token)
                    .getPassword() + account.getSalt()));
            return new SimpleAuthenticationInfo(appId,account.getPassword(), getName());
        } else {
            return new SimpleAuthenticationInfo(appId,"", getName());
        }
    }

    // setter

    public void setAccountProvider(AccountProvider accountProvider) {
        this.accountProvider = accountProvider;
    }

}
