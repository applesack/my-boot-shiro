package xyz.scootaloo.bootshiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.scootaloo.bootshiro.service.AccountService;
import xyz.scootaloo.bootshiro.service.UserService;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月05日 11:38
 */
@RestController
@RequestMapping("/account")
public class AccountController extends BaseHttpServ {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Value("${bootshiro.enableEncryptPassword}")
    private boolean isEncryptPassword;



}
