package xyz.scootaloo.bootshiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import xyz.scootaloo.bootshiro.domain.bo.Account;
import xyz.scootaloo.bootshiro.domain.bo.Role;
import xyz.scootaloo.bootshiro.domain.po.AuthUser;
import xyz.scootaloo.bootshiro.mapper.AuthUserMapper;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 15:44
 */
@Service
public class AccountService {
    // services
    private AuthUserMapper userMapper;
    private UserService userService;

    public Account loadAccount(String appId) throws DataAccessException {
        AuthUser user = userMapper.selectByUniqueKey(appId);
        return user != null ? new Account(user.getUsername(), user.getPassword(), user.getSalt()) : null;
    }

    public boolean isAccountExistByUid(String uid) {
        AuthUser user = userMapper.selectByPrimaryKey(uid);
        return user != null ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean registerAccount(AuthUser account) throws DataAccessException {
        // 给新用户授权访客角色
        userService.authorityUserRole(account.getUid(), Role.GUEST);
        return userMapper.insertSelective(account) == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public String loadAccountRole(String appId) throws DataAccessException {
        return userMapper.selectUserRoles(appId);
    }

    // setter

    @Autowired
    public void setUserMapper(AuthUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}
