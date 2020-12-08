package xyz.scootaloo.bootshiro.support.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.scootaloo.bootshiro.domain.po.AuthAccountLog;
import xyz.scootaloo.bootshiro.domain.po.AuthOperationLog;
import xyz.scootaloo.bootshiro.mapper.AuthAccountLogMapper;
import xyz.scootaloo.bootshiro.mapper.AuthOperationLogMapper;
import xyz.scootaloo.bootshiro.support.TaskManager;
import xyz.scootaloo.bootshiro.support.TimerTaskImpl;

import java.util.TimerTask;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 10:49
 */
@Slf4j
@Component
public class TaskFactory {

    private static AuthOperationLogMapper operationLogMapper;
    private static AuthAccountLogMapper accountLogMapper;

    public static TimerTask loginLog(String userId, String ip, Short succeed, String message) {
        return new TimerTaskImpl(() -> accountLogMapper
                .insertSelective(LogObjectFactory
                        .createAccountLog(userId, "用户登陆日志", ip, succeed, message)));
    }

    public static TimerTask registerLog(String userId, String ip, Short succeed, String message) {
        return new TimerTaskImpl(() -> accountLogMapper.insertSelective(LogObjectFactory
                        .createAccountLog(userId, "用户注册日志", ip, succeed, message)));
    }

    public static TimerTask businessLog(String userId, String api, String method, Short succeed, String message) {
        return new TimerTaskImpl(() -> operationLogMapper.insertSelective(LogObjectFactory
                .createOperationLog(userId, "业务操作日志", api, method, succeed, message)));
    }

    public TaskFactory() {
    }

    // setter

    @Autowired
    public void setOperationLogMapper(AuthOperationLogMapper aOperationLogMapper) {
        operationLogMapper = aOperationLogMapper;
    }

    @Autowired
    public void setAccountLogMapper(AuthAccountLogMapper aAccountLogMapper) {
        accountLogMapper = aAccountLogMapper;
    }

}
