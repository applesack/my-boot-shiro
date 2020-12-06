package xyz.scootaloo.bootshiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.scootaloo.bootshiro.domain.po.AuthAccountLog;
import xyz.scootaloo.bootshiro.mapper.AuthAccountLogMapper;

import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 11:00
 */
@Service
public class AccountLogService {

    private AuthAccountLogMapper accountLogMapper;

    public List<AuthAccountLog> getAccountLogList() {
        return accountLogMapper.selectAccountLogList();
    }

    // setter

    @Autowired
    public void setAccountLogMapper(AuthAccountLogMapper accountLogMapper) {
        this.accountLogMapper = accountLogMapper;
    }

}
