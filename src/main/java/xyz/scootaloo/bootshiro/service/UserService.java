package xyz.scootaloo.bootshiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import xyz.scootaloo.bootshiro.domain.bo.Role;
import xyz.scootaloo.bootshiro.domain.po.AuthUser;
import xyz.scootaloo.bootshiro.domain.po.AuthUserRole;
import xyz.scootaloo.bootshiro.mapper.AuthUserMapper;
import xyz.scootaloo.bootshiro.mapper.AuthUserRoleMapper;

import java.util.Date;
import java.util.List;

/**
 * 对于用户的操作
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 15:47
 */
@Service
public class UserService {
    // mappers
    private AuthUserMapper userMapper;
    private AuthUserRoleMapper userRoleMapper;

    // 获取该用户所具有的所有角色
    public String loadAccountRole(String appId) throws DataAccessException {
        return userMapper.selectUserRoles(appId);
    }

    // 获取所有用户
    public List<AuthUser> getUserList() throws DataAccessException {
        return userMapper.selectUserList();
    }

    // 获取持有某一角色的所有用户
    public List<AuthUser> getUserListByRoleId(Role role) throws DataAccessException {
        return userMapper.selectUserListByRoleId(role.getRoleId());
    }

    // 给这个用户授予一个角色，返回是否授予成功
    public boolean authorityUserRole(String uid, Role role) throws DataAccessException {
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setRoleId(role.getRoleId());
        authUserRole.setUserId(uid);
        authUserRole.setCreateTime(new Date());
        authUserRole.setUpdateTime(new Date());
        return userRoleMapper.insert(authUserRole) == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    // 删除这个用户的某个角色
    public boolean deleteAuthorityUserRole(String uid, Role role) throws DataAccessException {
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setUserId(uid);
        authUserRole.setRoleId(role.getRoleId());
        return userRoleMapper.deleteByUniqueKey(authUserRole) == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    // 根据用户名获取User对象
    public AuthUser getUserByAppId(String appId) throws DataAccessException {
        return userMapper.selectByUniqueKey(appId);
    }

    // 获取不具备某角色的所有用户
    public List<AuthUser> getNotAuthorityUserListByRoleId(Role role) throws DataAccessException {
        return userMapper.selectUserListExtendByRoleId(role.getRoleId());
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
