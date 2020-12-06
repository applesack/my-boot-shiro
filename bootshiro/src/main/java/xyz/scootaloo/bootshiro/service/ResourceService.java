package xyz.scootaloo.bootshiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import xyz.scootaloo.bootshiro.domain.po.AuthResource;
import xyz.scootaloo.bootshiro.mapper.AuthResourceMapper;

import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 10:52
 */
@Service
public class ResourceService {

    private AuthResourceMapper resourceMapper;

    public List<AuthResource> getAuthorityMenusByUid(String appId) throws DataAccessException {
        return resourceMapper.selectAuthorityMenusByUid(appId);
    }

    public List<AuthResource> getMenus() throws DataAccessException {
        return resourceMapper.selectMenus();
    }
    
    public Boolean addMenu(AuthResource menu) throws DataAccessException {
        int num = resourceMapper.insertSelective(menu);
        return num == 1 ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public Boolean modifyMenu(AuthResource menu) throws DataAccessException {
        int num = resourceMapper.updateByPrimaryKeySelective(menu);
        return num == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean deleteMenuByMenuId(Integer menuId) throws DataAccessException {
        int num = resourceMapper.deleteByPrimaryKey(menuId);
        return num == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public List<AuthResource> getApiTeamList() throws DataAccessException {
        return resourceMapper.selectApiTeamList();
    }

    public List<AuthResource> getApiList() throws DataAccessException {
        return resourceMapper.selectApiList();
    }
    
    public List<AuthResource> getApiListByTeamId(Integer teamId) throws DataAccessException {
        return resourceMapper.selectApiListByTeamId(teamId);
    }
    
    public List<AuthResource> getAuthorityApisByRoleId(Integer roleId) throws DataAccessException {
        return resourceMapper.selectApisByRoleId(roleId);
    }

    public List<AuthResource> getAuthorityMenusByRoleId(Integer roleId) throws DataAccessException {
        return resourceMapper.selectMenusByRoleId(roleId);
    }

    public List<AuthResource> getNotAuthorityApisByRoleId(Integer roleId) throws DataAccessException {
        return resourceMapper.selectNotAuthorityApisByRoleId(roleId);
    }

    public List<AuthResource> getNotAuthorityMenusByRoleId(Integer roleId) throws DataAccessException {
        return resourceMapper.selectNotAuthorityMenusByRoleId(roleId);
    }

    // setter

    @Autowired
    public void setResourceMapper(AuthResourceMapper resourceMapper) {
        this.resourceMapper = resourceMapper;
    }

}
