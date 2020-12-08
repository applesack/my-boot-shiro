package xyz.scootaloo.bootshiro.security.provider.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.scootaloo.bootshiro.domain.bo.Account;
import xyz.scootaloo.bootshiro.security.provider.AccountProvider;
import xyz.scootaloo.bootshiro.service.AccountService;

/**
 * 从数据库中获取用户信息 (账号，密码，盐值等信息)
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:24
 */
@Component
public class DatabaseAccountProvider implements AccountProvider {

    private AccountService accountService;

    @Override
    public Account loadAccount(String appId) {
        return accountService.loadAccount(appId);
    }

    // setter

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

}
