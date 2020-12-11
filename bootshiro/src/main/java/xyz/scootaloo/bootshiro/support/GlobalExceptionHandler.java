package xyz.scootaloo.bootshiro.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.bo.StatusCode;

import java.sql.SQLNonTransientException;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月10日 23:41
 */
@Slf4j
@Order(-1)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Message sqlException(DataAccessException e) {
        log.error("数据操作异常:", e);
        final Throwable cause = e.getCause();
        // 之后判断cause类型进一步记录日志处理
        if (cause instanceof SQLNonTransientException) {
            return Message.of(StatusCode.DATABASES_CONFLICT);
        }
        return Message.of(StatusCode.DEFAULT_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public Message notFoundException(RuntimeException e) {
        log.error("运行时异常:",e);
        return Message.of(StatusCode.DEFAULT_SERVER_ERROR);
    }

}
