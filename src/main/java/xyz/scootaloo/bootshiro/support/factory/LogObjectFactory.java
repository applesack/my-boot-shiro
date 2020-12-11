package xyz.scootaloo.bootshiro.support.factory;

import xyz.scootaloo.bootshiro.domain.po.AuthAccountLog;
import xyz.scootaloo.bootshiro.domain.po.AuthOperationLog;

import java.util.Date;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 11:07
 */
public class LogObjectFactory {

    public static AuthAccountLog createAccountLog(String userId, String logName, String ip, Short succeed, String message) {
        AuthAccountLog accountLog = new AuthAccountLog();
        accountLog.setUserId(userId);
        accountLog.setLogName(logName);
        accountLog.setIp(ip);
        accountLog.setSucceed(succeed);
        accountLog.setMessage(message);
        accountLog.setCreateTime(new Date());
        return accountLog;
    }

    public static AuthOperationLog createOperationLog(String userId, String logName, String api, String method, Short succeed, String message) {
        AuthOperationLog operationLog = new AuthOperationLog();
        operationLog.setUserId(userId);
        operationLog.setLogName(logName);
        operationLog.setApi(api);
        operationLog.setMethod(method);
        operationLog.setSucceed(succeed);
        operationLog.setMessage(message);
        operationLog.setCreateTime(new Date());
        return operationLog;
    }

    private LogObjectFactory() {
    }

}
