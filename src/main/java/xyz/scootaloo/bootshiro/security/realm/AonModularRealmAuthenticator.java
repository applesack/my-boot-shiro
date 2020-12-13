package xyz.scootaloo.bootshiro.security.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 假如系统内有多个realm类，逐个调用这些realm的supports方法，将token传递给支持处理此token的realm类，
 * 所有的realm类也应该重写getAuthenticationTokenClass方法，不然这里的supports调用的还是默认的实现。
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 11:15
 */
public class AonModularRealmAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        assertRealmsConfigured();
        List<Realm> realms = this.getRealms()
                .stream()
                .filter(realm -> realm.supports(authenticationToken))
                .collect(toList());
        return realms.size() == 1 ?
                this.doSingleRealmAuthentication(realms.iterator().next(), authenticationToken) :
                this.doMultiRealmAuthentication(realms, authenticationToken);
    }

}