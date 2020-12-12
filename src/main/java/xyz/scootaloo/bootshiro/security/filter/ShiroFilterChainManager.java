package xyz.scootaloo.bootshiro.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.scootaloo.bootshiro.security.config.RestPathMatchingFilterChainResolver;
import xyz.scootaloo.bootshiro.security.provider.ShiroFilterRulesProvider;
import xyz.scootaloo.bootshiro.security.rule.RolePermRule;
import xyz.scootaloo.bootshiro.service.AccountService;
import xyz.scootaloo.bootshiro.support.TaskManager;
import xyz.scootaloo.bootshiro.support.factory.MapPutter;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理系统中的过滤链。
 * 获取过滤链，过获取过滤规则，重新加载过滤链等
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 11:23
 */
@Slf4j
@Component
public class ShiroFilterChainManager {
    // 注入一个过滤规则提供者，默认这个过滤规则是从数据库中获取
    private ShiroFilterRulesProvider shiroFilterRulesProvider;

    // 改为用 new 的方式实例化 filter

    // 过滤器的依赖
    @Value("${bootshiro.enableEncryptPassword}")
    private boolean isEncryptPassword;
    private StringRedisTemplate redisTemplate;
    private AccountService accountService;
    private TaskManager taskManager;

    // 绑定一个过滤工厂bean
    private ShiroFilterFactoryBean shiroFilterFactoryBean;

    /**
     * 获取过滤链
     * 按照原计划这两个过滤链是通过Autowire自动注入的，但是后来出现了过滤链匹配错误的问题。
     * 解决这个问题目前的方案是使用new关键字来创建实例，具体内容参考:
     * <a href="https://www.guitu18.com/post/2020/01/06/64.html">自定义Filter实现及其问题排查记录</a>
     * 内容比较多，暂时没看完，有兴趣可以了解一下。
     * @return java.util.Map<java.lang.String,javax.servlet.Filter>
     */
    public Map<String, Filter> initGetFilters() {
        // 实例化并设置依赖
        PasswordFilter passwordFilter = new PasswordFilter();
        passwordFilter.setRedisTemplate(redisTemplate);
        passwordFilter.setEncryptPassword(isEncryptPassword);

        BonJwtFilter jwtFilter = new BonJwtFilter();
        jwtFilter.setAccountService(accountService);
        jwtFilter.setRedisTemplate(redisTemplate);
        jwtFilter.setTaskManager(taskManager);

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

        // ------------- dynamic 动态URL
        if (shiroFilterRulesProvider != null) {
            List<RolePermRule> rolePermRules = this.shiroFilterRulesProvider.loadRolePermRules();
            if (null != rolePermRules) {
                rolePermRules.forEach(rule -> {
                    String chain = rule.toFilterChain();
                    if (null != chain) {
                        filterChain.putIfAbsent(rule.getUrl(), chain);
                    }
                });
            }
        }
        return filterChain;
    }

    /**
     * 动态重新加载过滤链规则
     * 在此之前已经在这个对象上注入了本系统的过滤器工厂对象，使得这个方法可以使用
     * @see xyz.scootaloo.bootshiro.security.config.ShiroConfig#shiroFilterFactoryBean(SecurityManager, ShiroFilterChainManager)
     * 于是这里只需要获取过滤工厂的实例，对其中的过滤map重新加载，即可实现动态地修改过滤规则
     */
    public void reloadFilterChain() {
        AbstractShiroFilter abstractShiroFilter;
        try {
            abstractShiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
            if (abstractShiroFilter == null)
                throw new NullPointerException("重载过滤链时无法获取实例");
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
    public void setShiroFilterRulesProvider(ShiroFilterRulesProvider shiroFilterRulesProvider) {
        this.shiroFilterRulesProvider = shiroFilterRulesProvider;
    }

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void setShiroFilterFactoryBean(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        this.shiroFilterFactoryBean = shiroFilterFactoryBean;
    }

}
