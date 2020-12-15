package xyz.scootaloo.bootshiro.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
public class StatusCodeObject {
    @ApiModelProperty(value = "响应状态码")
    private final Integer   code;
    @ApiModelProperty(value = "关于响应结果的提示")
    private final String    message;
    @ApiModelProperty(value = "产生此响应时的时间戳")
    private final Timestamp timestamp;
    @ApiModelProperty(value = "这条响应是否成功", dataType = "boolean")
    private final boolean   success;

    public StatusCodeObject(StatusCode statusCode) {
        code = statusCode.code();
        message = statusCode.message();
        timestamp = statusCode.timestamp();
        success = statusCode.success();
    }

}
