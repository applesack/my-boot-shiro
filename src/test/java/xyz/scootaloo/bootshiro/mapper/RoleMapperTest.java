package xyz.scootaloo.bootshiro.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.scootaloo.bootshiro.security.rule.RolePermRule;
import xyz.scootaloo.bootshiro.service.AccountService;

import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 22:26
 */
@SpringBootTest
public class RoleMapperTest {

    @Autowired
    private AuthResourceMapper authResourceMapper;

    @Autowired
    public AccountService accountService;

    @Test
    public void testRoleMapper() {
        List<RolePermRule> rolePermRuleList = authResourceMapper.selectRoleRules();
        for (RolePermRule role : rolePermRuleList) {
            System.out.println(role.toFilterChain());
        }
    }

    @Test
    public void testLoadAccountRole() {
        String[] appIds = {
                "admin",
                "测试用户3",
                "测试用户2"
        };
        for (String appId : appIds) {
            String roles = accountService.loadAccountRole(appId);
            System.out.println(appId + ": " + roles);
        }

    }

}
