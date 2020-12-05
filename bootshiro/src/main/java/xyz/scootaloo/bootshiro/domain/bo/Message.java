package xyz.scootaloo.bootshiro.domain.bo;

import lombok.Getter;
import xyz.scootaloo.bootshiro.domain.dto.StatusCodeObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 后端发送给前端的消息统一格式：
 * 主要包含两个部分，
 * <p>第一个部分"status", 包含本次响应的状态，其中success属性表示本次响应是否成功，code属性表示本次响应的状态码，
 * message属性中有本次响应的说明，timestamp是时间戳，当success属性为false时可以参考code做对应的处理。</p>
 * <p>第二部分是响应中携带的数据，以键值对的格式表示，json格式是这样的：</p>
 * <pre class="code">
 * {@code
 *  {
 *      "status" : {
 *          "code" : 6666,
 *          "message" : "成功的请求",
 *          "timestamp" : "2020-12-05T13:15:29.046+00:00",
 *          "success" : true
 *      },
 *      "data" : {
 *          "name" : "buck",
 *          "age" : 17
 *      }
 *  }
 * }</pre>
 *
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 16:22
 */
@Getter
public class Message {

    // 消息状态
    private StatusCodeObject status;
    // 消息内容，存储实体交互数据
    private Map<String, Object> data = new HashMap<>();

    // 默认成功消息
    public static Message success() {
        return of(StatusCode.SUCCESS);
    }

    // 默认失败消息
    public static Message failure() {
        return of(StatusCode.FAILURE);
    }

    // 使用其他的状态码开始创建Message对象
    public static Message of(StatusCode statusCode) {
        Message message = getInstance();
        message.status = statusCode.toMap();
        return message;
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

    // 获取Message实例
    private static Message getInstance() {
        return new Message();
    }

}
