package xyz.scootaloo.bootshiro.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.po.AuthAccountLog;
import xyz.scootaloo.bootshiro.domain.po.AuthOperationLog;
import xyz.scootaloo.bootshiro.service.AccountLogService;
import xyz.scootaloo.bootshiro.service.OperationLogService;

import java.util.List;

/**
 * 管理日志
 * @author : flutterdash@qq.com
 * @since : 2020年12月14日 11:41
 */
@RestController
@RequestMapping("/log")
public class LogController {
    // services
    private AccountLogService accountLogService;
    private OperationLogService operationLogService;

    @RequestMapping("/accountLog/{currentPage}/{pageSize}")
    public Message getAccountLogList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthAccountLog> authAccountLogs = accountLogService.getAccountLogList();
        return Message.success()
                .addData("data", new PageInfo<>(authAccountLogs));
    }

    @RequestMapping("/operationLog/{currentPage}/{pageSize}")
    public Message getOperationLogList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthOperationLog> authOperationLogs = operationLogService.getOperationList();
        return Message.success()
                .addData("data", new PageInfo<>(authOperationLogs));
    }

    // setter

    @Autowired
    public void setAccountService(AccountLogService accountLogService) {
        this.accountLogService = accountLogService;
    }

    @Autowired
    public void setOperationLogService(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

}
