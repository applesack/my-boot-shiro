package xyz.scootaloo.bootshiro.security.matcher;

import io.jsonwebtoken.ExpiredJwtException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;
import xyz.scootaloo.bootshiro.utils.JwtUtils;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:09
 */
@Component
public class JwtMatcher implements CredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String jwt = (String) info.getCredentials();
        try {
            // 假如在解析过程中出现异常，则该jwt是无效的
            JwtUtils.parseJwt(jwt, JwtUtils.SECRET_KEY);
        } catch (ExpiredJwtException e) {
            // 令牌过期
            throw new AuthenticationException("expiredJwt");
        } catch (Exception e) {
            // 令牌错误
            throw new AuthenticationException("errJwt");
        }
        return true;
    }

}
