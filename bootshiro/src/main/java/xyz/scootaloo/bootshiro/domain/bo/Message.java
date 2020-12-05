package xyz.scootaloo.bootshiro.domain.bo;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 16:22
 */
@Getter
public class Message {

    // 消息状态
    private StatusCode statusCode;
    // 消息内容，存储实体交互数据
    private Map<String, Object> data = new HashMap<>();

    // 默认成功消息
    public Message success() {
        statusCode = StatusCode.SUCCESS;
        return this;
    }

    // 默认失败消息
    public Message failure() {
        statusCode = StatusCode.FAILURE;
        return this;
    }

    // 使用自定义的状态码
    public Message of(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    // 添加数据
    public Message addData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    // 设置数据
    public Message setData(Map<String, Object> nDate) {
        this.data = nDate;
        return this;
    }

}
