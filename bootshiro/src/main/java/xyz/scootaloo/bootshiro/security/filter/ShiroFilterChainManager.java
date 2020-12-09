package xyz.scootaloo.bootshiro.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.scootaloo.bootshiro.security.config.RestPathMatchingFilterChainResolver;
import xyz.scootaloo.bootshiro.security.provider.ShiroFilterRulesProvider;
import xyz.scootaloo.bootshiro.security.rule.RolePermRule;
import xyz.scootaloo.bootshiro.support.factory.MapPutter;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 11:23
 */
@Slf4j
@Component
public class ShiroFilterChainManager {

    private ShiroFilterRulesProvider shiroFilterRulesProvider;

    private PasswordFilter passwordFilter;
    private BonJwtFilter jwtFilter;

    private ShiroFilterFactoryBean shiroFilterFactoryBean;

    /**
     * 获取过滤链
     * @return java.util.Map<java.lang.String,javax.servlet.Filter>
     */
    public Map<String, Filter> initGetFilters() {
        return MapPutter.set(new LinkedHashMap<String, Filter>())
                .put("auth", passwordFilter)
                .put("jwt", jwtFilter)
                .get();
    }
    /**
     * 初始化获取过滤链规则
     * @return java.util.Map<java.lang.String, java.lang.String>
     */
    public Map<String,String> initGetFilterChain() {
        final String anonStr = "anon"; // 标记为 anon 的路径会被过滤器忽略
        Map<String,String> filterChain = MapPutter
                            .<String, String>set(new LinkedHashMap<>())
                // ------------- anon 默认过滤器忽略的URL
                            .put("/css/**", anonStr)
                            .put("/js/**", anonStr)
                // ------------- auth 默认需要认证过滤器的URL 走auth--PasswordFilter
                            .put("/account/**", "auth")
                            .get();

        // -------------dynamic 动态URL
        if (shiroFilterRulesProvider != null) {
            List<RolePermRule> rolePermRules = this.shiroFilterRulesProvider.loadRolePermRules();
            if (null != rolePermRules) {
                StringBuilder sb = new StringBuilder();
                rolePermRules.forEach(rule -> {
                    sb.setLength(0);
                    String chain = rule.toFilterChain(sb);
                    if (null != chain) {
                        filterChain.putIfAbsent(rule.getUrl(), chain);
                    }
                });
            }
        }
        return filterChain;
    }

    /**
     * description 动态重新加载过滤链规则
     */
    public void reloadFilterChain() {
        AbstractShiroFilter abstractShiroFilter;
        try {
            abstractShiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
            RestPathMatchingFilterChainResolver filterChainResolver
                    = (RestPathMatchingFilterChainResolver) abstractShiroFilter.getFilterChainResolver();
            DefaultFilterChainManager filterChainManager
                    = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
            filterChainManager.getFilterChains().clear();
            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            shiroFilterFactoryBean.setFilterChainDefinitionMap(this.initGetFilterChain());
            shiroFilterFactoryBean.getFilterChainDefinitionMap().forEach(filterChainManager::createChain);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public ShiroFilterChainManager() {
    }

    // setter

    @Autowired
    public void setPasswordFilter(PasswordFilter passwordFilter) {
        this.passwordFilter = passwordFilter;
    }

    @Autowired
    public void setShiroFilterRulesProvider(ShiroFilterRulesProvider shiroFilterRulesProvider) {
        this.shiroFilterRulesProvider = shiroFilterRulesProvider;
    }

    @Autowired
    public void setJwtFilter(BonJwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    public void setShiroFilterFactoryBean(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        this.shiroFilterFactoryBean = shiroFilterFactoryBean;
    }

}
