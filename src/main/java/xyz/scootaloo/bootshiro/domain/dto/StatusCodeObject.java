package xyz.scootaloo.bootshiro.domain.dto;

import lombok.Getter;
import xyz.scootaloo.bootshiro.domain.bo.StatusCode;

import java.sql.Timestamp;

/**
 * 中转
 * @see StatusCode
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 21:05
 */
@Getter
public class StatusCodeObject {

    private final Integer   code;
    private final String    message;
    private final Timestamp timestamp;
    private final boolean   success;

    public StatusCodeObject(StatusCode statusCode) {
        code = statusCode.code();
        message = statusCode.message();
        timestamp = statusCode.timestamp();
        success = statusCode.success();
    }

}
