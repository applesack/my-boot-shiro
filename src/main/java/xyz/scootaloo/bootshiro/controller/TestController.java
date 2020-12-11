package xyz.scootaloo.bootshiro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.utils.IpUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 18:56
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/message")
    public Message getMessage() {
        return Message.success()
                .addData("name", "buck")
                .addData("age", 17);
    }

    @GetMapping("/getIp")
    public Message getIp(HttpServletRequest request) {
        String ipAddress = IpUtils.getIp(request);
        return Message.success()
                .addData("ip", ipAddress);
    }

}
