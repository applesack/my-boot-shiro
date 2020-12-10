package xyz.scootaloo.bootshiro.security.realm;

import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:04
 */
@Component
public class RealmManager {

    private PasswordRealm passwordRealm;
    private JwtRealm jwtRealm;

    public List<Realm> initGetRealm() {
        List<Realm> realmList = new LinkedList<>();
        realmList.add(passwordRealm);
        realmList.add(jwtRealm);
        return Collections.unmodifiableList(realmList);
    }

    public RealmManager() {
    }

    // setter

    @Autowired
    public void setPasswordRealm(PasswordRealm passwordRealm) {
        this.passwordRealm = passwordRealm;
    }

    @Autowired
    public void setJwtRealm(JwtRealm jwtRealm) {
        this.jwtRealm = jwtRealm;
    }

}
