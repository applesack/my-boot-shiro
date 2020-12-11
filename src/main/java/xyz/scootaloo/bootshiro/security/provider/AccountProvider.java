package xyz.scootaloo.bootshiro.security.provider;

import xyz.scootaloo.bootshiro.domain.bo.Account;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:21
 */
@FunctionalInterface
public interface AccountProvider {

    /**
     * 提供用户id对应的账号密码盐值信息
     * @param appId 用户标记
     * @return Account
     */
    Account loadAccount(String appId);

}
