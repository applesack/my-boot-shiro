package xyz.scootaloo.bootshiro.security.realm;

import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.scootaloo.bootshiro.security.matcher.JwtMatcher;
import xyz.scootaloo.bootshiro.security.matcher.PasswordMatcher;
import xyz.scootaloo.bootshiro.security.provider.AccountProvider;
import xyz.scootaloo.bootshiro.security.token.JwtToken;
import xyz.scootaloo.bootshiro.security.token.PasswordToken;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:04
 */
@Component
public class RealmManager {

    private JwtMatcher jwtMatcher;
    private PasswordMatcher passwordMatcher;
    private AccountProvider accountProvider;

    public List<Realm> initGetRealm() {
        List<Realm> realmList = new LinkedList<>();
        // ----- password
        PasswordRealm passwordRealm = new PasswordRealm();
        passwordRealm.setAccountProvider(accountProvider);
        passwordRealm.setCredentialsMatcher(passwordMatcher);
        passwordRealm.setAuthenticationTokenClass(PasswordToken.class);
        realmList.add(passwordRealm);
        // ----- jwt
        JwtRealm jwtRealm = new JwtRealm();
        jwtRealm.setCredentialsMatcher(jwtMatcher);
        jwtRealm.setAuthenticationTokenClass(JwtToken.class);
        realmList.add(jwtRealm);
        return Collections.unmodifiableList(realmList);
    }

    public RealmManager() {
    }

    // setter

    @Autowired
    public void setJwtMatcher(JwtMatcher jwtMatcher) {
        this.jwtMatcher = jwtMatcher;
    }

    @Autowired
    public void setPasswordMatcher(PasswordMatcher passwordMatcher) {
        this.passwordMatcher = passwordMatcher;
    }

    @Autowired
    public void setAccountProvider(AccountProvider accountProvider) {
        this.accountProvider = accountProvider;
    }

}
