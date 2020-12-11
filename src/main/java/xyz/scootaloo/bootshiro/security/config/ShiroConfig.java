package xyz.scootaloo.bootshiro.security.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import xyz.scootaloo.bootshiro.security.filter.ShiroFilterChainManager;
import xyz.scootaloo.bootshiro.security.filter.StatelessWebSubjectFactory;
import xyz.scootaloo.bootshiro.security.realm.AonModularRealmAuthenticator;
import xyz.scootaloo.bootshiro.security.realm.RealmManager;

/**
 * Shiro配置类
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 10:02
 */
@Configuration
public class ShiroConfig {

    @Bean // 过滤器配置
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroFilterChainManager filterChainManager) {
        /* 0. 创建一个过滤工厂bean
         * 1. 将过滤管理器与过滤工厂绑定，当需要动态的刷新权限规则时用
         * 2. 绑定SecurityManager到到这个过滤器工厂 --必需
         * 3. 将过滤器注入到滤器工厂
         * 4. 设置过滤工厂的过滤规则
         * 5. 返回设置好的过滤工厂
         */
        RestShiroFilterFactoryBean shiroFilterFactoryBean = new RestShiroFilterFactoryBean();
        filterChainManager.setShiroFilterFactoryBean(shiroFilterFactoryBean);
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setFilters(filterChainManager.initGetFilters());
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainManager.initGetFilterChain());
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager(RealmManager realmManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setAuthenticator(new AonModularRealmAuthenticator());
        securityManager.setRealms(realmManager.initGetRealm());

        // 无状态subjectFactory设置
        DefaultSessionStorageEvaluator evaluator
                = (DefaultSessionStorageEvaluator) ((DefaultSubjectDAO) securityManager
                .getSubjectDAO()).getSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(Boolean.FALSE);
        StatelessWebSubjectFactory subjectFactory = new StatelessWebSubjectFactory();
        securityManager.setSubjectFactory(subjectFactory);

        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

}
