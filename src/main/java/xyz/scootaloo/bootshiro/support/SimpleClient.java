package xyz.scootaloo.bootshiro.support;

import xyz.scootaloo.bootshiro.support.cli.HttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月15日 16:29
 */
public class SimpleClient {

    private static final List<String> initCmd = new ArrayList<>();

    static {
        // 这些命令会在程序启动时执行
        initCmd.add("enable checkServer");    // 程序启动前先检查是否有活动的服务
        initCmd.add("postProcess add print"); // 每次发送请求后将请求结果打印到控制台
        initCmd.add("postProcess add cache"); // 每次发送请求后将请求的结果缓存到集合中
    }

    public static void main(String[] args) throws Exception {
        new HttpClient(initCmd).run();
    }

}
