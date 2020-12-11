package xyz.scootaloo.bootshiro.security.provider;

import xyz.scootaloo.bootshiro.security.rule.RolePermRule;

import java.util.List;

/**
 * 在应用运行过程中需要改变过滤规则时，则这个接口的方法被调用。
 * @see xyz.scootaloo.bootshiro.security.provider.impl.DatabaseAccountProvider
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 11:24
 */
@FunctionalInterface
public interface ShiroFilterRulesProvider {

    /**
     * 获取这些对象
     * @return java.util.List
     */
    List<RolePermRule> loadRolePermRules();

}
