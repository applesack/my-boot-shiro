package xyz.scootaloo.bootshiro.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.scootaloo.bootshiro.domain.po.AuthAccountLog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 15:14
 */
@SpringBootTest
public class TestAuthAccountLogMapper {

    @Autowired
    AuthAccountLogMapper authAccountLogMapper;

    // 插入功能正常
    @Test
    public void testInsert() {
        AuthAccountLog authAccountLog = new AuthAccountLog();
        authAccountLog.setCreateTime(new Date(2010, Calendar.DECEMBER, 6));
        authAccountLog.setUserId("ws");
        authAccountLogMapper.insert(authAccountLog);
    }

    @Test
    public void testSelect() {
        List<AuthAccountLog> authAccountLogList = authAccountLogMapper.selectAccountLogList();
        System.out.println(authAccountLogList);
    }

}
