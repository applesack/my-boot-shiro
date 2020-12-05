package xyz.scootaloo.bootshiro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.scootaloo.bootshiro.domain.bo.Message;

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

}
