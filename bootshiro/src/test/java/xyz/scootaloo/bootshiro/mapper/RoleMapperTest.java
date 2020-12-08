package xyz.scootaloo.bootshiro.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.scootaloo.bootshiro.security.rule.RolePermRule;

import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月08日 22:26
 */
@SpringBootTest
public class RoleMapperTest {

    @Autowired
    private AuthResourceMapper authResourceMapper;

    @Test
    public void testRoleMapper() {
        List<RolePermRule> rolePermRuleList = authResourceMapper.selectRoleRules();
        for (RolePermRule role : rolePermRuleList) {
            System.out.println(role);
        }
    }

}
