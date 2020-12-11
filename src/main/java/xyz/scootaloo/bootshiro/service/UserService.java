package xyz.scootaloo.bootshiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import xyz.scootaloo.bootshiro.domain.po.AuthUser;
import xyz.scootaloo.bootshiro.domain.po.AuthUserRole;
import xyz.scootaloo.bootshiro.mapper.AuthUserMapper;
import xyz.scootaloo.bootshiro.mapper.AuthUserRoleMapper;

import java.util.Date;
import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 15:47
 */
@Service
public class UserService {

    private AuthUserMapper userMapper;
    private AuthUserRoleMapper userRoleMapper;

    public String loadAccountRole(String appId) throws DataAccessException {
        return userMapper.selectUserRoles(appId);
    }

    public List<AuthUser> getUserList() throws DataAccessException {
        return userMapper.selectUserList();
    }

    public List<AuthUser> getUserListByRoleId(Integer roleId) throws DataAccessException {
        return userMapper.selectUserListByRoleId(roleId);
    }

    public boolean authorityUserRole(String uid, int roleId) throws DataAccessException {
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setRoleId(roleId);
        authUserRole.setUserId(uid);
        authUserRole.setCreateTime(new Date());
        authUserRole.setUpdateTime(new Date());
        return userRoleMapper.insert(authUserRole) == 1? Boolean.TRUE :Boolean.FALSE;
    }

    public boolean deleteAuthorityUserRole(String uid, int roleId) throws DataAccessException {
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setUserId(uid);
        authUserRole.setRoleId(roleId);
        return userRoleMapper.deleteByUniqueKey(authUserRole) == 1? Boolean.TRUE : Boolean.FALSE;
    }

    public AuthUser getUserByAppId(String appId) throws DataAccessException {
        return userMapper.selectByUniqueKey(appId);
    }

    public List<AuthUser> getNotAuthorityUserListByRoleId(Integer roleId) throws DataAccessException {
        return userMapper.selectUserListExtendByRoleId(roleId);
    }

    // setter

    @Autowired
    public void setUserMapper(AuthUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setUserRoleMapper(AuthUserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

}
