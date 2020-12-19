package xyz.scootaloo.bootshiro.domain.bo;

/**
 * 管理各种基础的常量
 * @author : flutterdash@qq.com
 * @since : 2020年12月19日 18:48
 */
public interface DVal {

    // request
    String appId = "appId";
    String jwt = "jwt";
    String body = "body";
    String uid = "uid";
    String authorization = "authorization";
    String deviceInfo = "deviceInfo";

    // form
    String username = "username";
    String password = "password";
    String realName = "realName";
    String avatar = "avatar";
    String phone = "phone";
    String email = "email";
    String sex = "sex";
    String createWhere = "createWhere";

    // redis
    long refreshPeriodTime = 36000L;
    String jwtSessionPrefix = "JWT-SESSION-";
    String tokenKeyPrefix = "JWT-SESSION-";

    // other
    String expiredJwt = "expiredJwt";

}
