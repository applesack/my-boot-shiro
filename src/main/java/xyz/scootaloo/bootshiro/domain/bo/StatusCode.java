package xyz.scootaloo.bootshiro.domain.bo;

import xyz.scootaloo.bootshiro.domain.dto.StatusCodeObject;

import java.sql.Timestamp;

/**
 * 统一业务操作状态码。
 * 一种想法，让成功的状态码为双数，失败的状态码为单数，这样可以节省一个字段 (未实现)。
 * 参照源码做了一些优化，将状态码部分从Message对象中分离出来。
 * @see xyz.scootaloo.bootshiro.domain.bo.Message
 * ---------------------------------------------
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 16:30
 */
public enum StatusCode {

    // 基础请求状态信息
    SUCCESS(6666, Boolean.TRUE , "成功请求"),
    FAILURE(1111, Boolean.FALSE, "请求失败"),

    // 认证授权相关
    ISSUED_TOKEN_KEY_SUCCESS (1000, Boolean.TRUE , "动态密钥签发成功"),
    ISSUED_TOKEN_KEY_FAIL    (1001, Boolean.FALSE, "动态密钥签发失败"),
    LOGIN_FAIL        (1002, Boolean.FALSE, "用户密码认证失败"),
    ISSUED_JWT_SUCCESS(1003, Boolean.TRUE , "用户密码认证成功, JWT签发成功, 返回jwt"),
    ISSUED_JWT_FAIL   (1004, Boolean.FALSE, "JWT签发失败"),
    NEW_JWT           (1005, Boolean.FALSE, "jwt_real_token过期,jwt_refresh_token还未过期, " +
                                                            "服务器返回新的jwt, 客户端需携带新返回的jwt对这次请求重新发起"),
    EXPIRED_JWT       (1006, Boolean.FALSE, "jwt_real_token过期, jwt_refresh_token过期(通知客户端重新登录)"),
    ERROR_JWT         (1007, Boolean.FALSE, "jwt_token认证失败无效(包括用户认证失败或者jwt令牌错误无效或者用户未登录)"),
    NO_PERMISSION     (1008, Boolean.FALSE, "jwt_token无权限访问此资源"),

    // 用户相关
    LACK_ACCOUNT_INFO(2001, Boolean.FALSE, "注册账户信息不完善"),
    REGISTER_SUCCESS (2002, Boolean.TRUE , "注册用户成功"),
    ACCOUNT_EXIST    (2003, Boolean.FALSE, "账号已存在"),
    REGISTER_FAILURE (2004, Boolean.FALSE, "注册失败"),
    LOGOUT_ERROR     (2005, Boolean.FALSE, "用户未登陆无法登出"),
    LOGOUT_SUCCESS   (2006, Boolean.TRUE , "用户登出成功"),

    // 服务器错误
    DEFAULT_SERVER_ERROR(3000, Boolean.FALSE, "服务器开小差"),
    DATABASES_CONFLICT  (3001, Boolean.FALSE, "数据库冲突操作失败");

    // 业务类状态码 4000+ ... ...

    // status
    private final Integer code;        // 状态码
    private final String  message;     // 附加消息
    private final Timestamp timestamp; // 时间戳
    private final Boolean success;     // 是否成功

    // default constructor
    StatusCode(Integer code, Boolean success, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.success = success;
    }

    // getter
    public Integer code() {
        return this.code;
    }
    public String message() {
        return this.message;
    }
    public Timestamp timestamp() {
        return this.timestamp;
    }
    public Boolean success() {
        return this.success;
    }

    /**
     * 枚举类对象不能用@ResponseBody注解自动转换为json(转换的对象不包含枚举类除枚举属性之外的状态)，
     * 所以要把这个枚举类中的信息反馈给前端需要先转换成pojo类
     * @return 可被@ResponseBody解析成带有状态的json
     */
    public StatusCodeObject toMap() {
        return new StatusCodeObject(this);
    }

}
