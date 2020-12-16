package xyz.scootaloo.bootshiro.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
 * @author : flutterdash@qq.com
 * @since : 2020年12月14日 11:41
 */
@RestController
@Api("管理日志")
@RequestMapping("/log")
public class LogController extends BaseHttpServ {
    // services
    private AccountLogService accountLogService;
    private OperationLogService operationLogService;

    @ApiOperation(value = "获取账户日志列表")
    @RequestMapping("/accountLog/{currentPage}/{pageSize}")
    public Message getAccountLogList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthAccountLog> authAccountLogs = accountLogService.getAccountLogList();
        return Message.success()
                .addData("data", new PageInfo<>(authAccountLogs));
    }

    @ApiOperation(value = "获取操作日志列表")
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
