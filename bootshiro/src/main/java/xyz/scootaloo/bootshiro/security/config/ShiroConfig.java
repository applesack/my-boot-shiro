package xyz.scootaloo.bootshiro.security.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroFilterChainManager filterChainManager,
                                                         RestShiroFilterFactoryBean shiroFilterFactoryBean) {
        /*
         * 1. 绑定
         */
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        filterChainManager.setShiroFilterFactoryBean(shiroFilterFactoryBean);
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
