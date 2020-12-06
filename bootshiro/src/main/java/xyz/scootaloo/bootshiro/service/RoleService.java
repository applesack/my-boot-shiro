package xyz.scootaloo.bootshiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import xyz.scootaloo.bootshiro.domain.po.AuthRole;
import xyz.scootaloo.bootshiro.domain.po.AuthRoleResource;
import xyz.scootaloo.bootshiro.mapper.AuthRoleMapper;
import xyz.scootaloo.bootshiro.mapper.AuthRoleResourceMapper;

import java.util.Date;
import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 10:56
 */
@Service
public class RoleService {

    private AuthRoleResourceMapper roleResourceMapper;
    private AuthRoleMapper roleMapper;
    
    public boolean authorityRoleResource(int roleId, int resourceId) throws DataAccessException {
        AuthRoleResource authRoleResource = new AuthRoleResource();
        authRoleResource.setRoleId((short) roleId);
        authRoleResource.setResourceId((short) resourceId);
        authRoleResource.setCreateTime(new Date());
        authRoleResource.setUpdateTime(new Date());
        return roleResourceMapper.insert(authRoleResource) == 1? Boolean.TRUE : Boolean.FALSE;
    }
    
    public boolean addRole(AuthRole role) throws DataAccessException {
        int num = roleMapper.insertSelective(role);
        return num == 1? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean updateRole(AuthRole role) throws DataAccessException {
        int num = roleMapper.updateByPrimaryKeySelective(role);
        return num == 1? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean deleteRoleByRoleId(Integer roleId) throws DataAccessException {
        int num = roleMapper.deleteByPrimaryKey(roleId);
        return num == 1? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean deleteAuthorityRoleResource(Integer roleId, Integer resourceId) throws DataAccessException {
        int num = roleResourceMapper.deleteByUniqueKey(roleId, resourceId);
        return num == 1? Boolean.TRUE : Boolean.FALSE;
    }

    public List<AuthRole> getRoleList() throws DataAccessException {
        return roleMapper.selectRoles();
    }

    // setter

    @Autowired
    public void setRoleResourceMapper(AuthRoleResourceMapper roleResourceMapper) {
        this.roleResourceMapper = roleResourceMapper;
    }

    @Autowired
    public void setRoleMapper(AuthRoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

}
