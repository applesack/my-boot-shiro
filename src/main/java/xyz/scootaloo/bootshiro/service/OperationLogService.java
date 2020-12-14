package xyz.scootaloo.bootshiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.scootaloo.bootshiro.domain.po.AuthOperationLog;
import xyz.scootaloo.bootshiro.mapper.AuthOperationLogMapper;

import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 10:50
 */
@Service
public class OperationLogService {

    private AuthOperationLogMapper operationLogMapper;

    public List<AuthOperationLog> getOperationList() {
        return operationLogMapper.selectOperationLogList();
    }

    // setter

    @Autowired
    public void setOperationLogMapper(AuthOperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

}
