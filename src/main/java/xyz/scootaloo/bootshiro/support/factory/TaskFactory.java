package xyz.scootaloo.bootshiro.support.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.scootaloo.bootshiro.mapper.AuthAccountLogMapper;
import xyz.scootaloo.bootshiro.mapper.AuthOperationLogMapper;
import xyz.scootaloo.bootshiro.support.TimerTaskImpl;

import java.util.TimerTask;

/**
 * 此类为快速创建计时器任务的工厂类。
 * 主要用于创建数据库日志写入任务，为任务管理器提供参数，任务管理器参见:
 * @see xyz.scootaloo.bootshiro.support.TaskManager#executeTask(TimerTask)
 * ----------------------------------------------------------------------
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 10:49
 */
@Slf4j
@Component
public class TaskFactory {
    // 操作日志 和 账户日志 的 mapper
    private static AuthOperationLogMapper operationLogMapper;
    private static AuthAccountLogMapper accountLogMapper;

    // 登陆日志写入任务
    public static TimerTask loginLog(String userId, String ip, Short succeed, String message) {
        return new TimerTaskImpl(() -> accountLogMapper
                .insertSelective(LogObjectFactory
                        .createAccountLog(userId, "用户登陆日志", ip, succeed, message)));
    }

    // 注册日志写入任务
    public static TimerTask registerLog(String userId, String ip, Short succeed, String message) {
        return new TimerTaskImpl(() -> accountLogMapper.insertSelective(LogObjectFactory
                        .createAccountLog(userId, "用户注册日志", ip, succeed, message)));
    }

    // 用户登出日志写入任务
    public static TimerTask logoutLog(String userId, String ip, Short success, String message) {
        return new TimerTaskImpl(() -> accountLogMapper.insertSelective(LogObjectFactory
                        .createAccountLog(userId, "用户退出日志", ip, success, message)));
    }

    // 业务日志写入任务
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
