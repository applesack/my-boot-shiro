package xyz.scootaloo.bootshiro.security.provider.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.scootaloo.bootshiro.mapper.AuthResourceMapper;
import xyz.scootaloo.bootshiro.security.provider.ShiroFilterRulesProvider;
import xyz.scootaloo.bootshiro.security.rule.RolePermRule;

import java.util.List;

/**
 * 从数据库中获取
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 11:25
 */
@Service("ShiroFilterRulesProvider")
public class DatabaseShiroFilterRulesProvider implements ShiroFilterRulesProvider {

    private AuthResourceMapper authResourceMapper;

    @Override
    public List<RolePermRule> loadRolePermRules() {
        return authResourceMapper.selectRoleRules();
    }

    // setter

    @Autowired
    public void setAuthResourceMapper(AuthResourceMapper authResourceMapper) {
        this.authResourceMapper = authResourceMapper;
    }

}
