package xyz.scootaloo.bootshiro.support.cli;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.support.factory.MapPutter;
import xyz.scootaloo.bootshiro.utils.AesUtils;
import xyz.scootaloo.bootshiro.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月15日 16:52
 */
public class StrategyFactory {

    public static final StrategyFactory INSTANCE = new StrategyFactory();
    private final HttpClientContext context = HttpClientContext.getInstance(); // 上下文
    private static final Map<String, Accept> DESC_MAP = new HashMap<>();

    private final String hostname = "127.0.0.1";
    private int port = 80;

    public static Map<String, Consumer<List<String>>> register() {
        Map<String, Consumer<List<String>>> result = new HashMap<>();
        Class<StrategyFactory> clazz = StrategyFactory.class;

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            Accept cmdHandle = method.getAnnotation(Accept.class);
            if (cmdHandle == null)
                continue;
            String cmdName = cmdHandle.value();
            if (result.containsKey(cmdName))
                continue;

            DESC_MAP.put(cmdName, cmdHandle);
            result.put(cmdName,  (List<String> args) -> {
                try {
                    method.invoke(INSTANCE, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }

        return result;
    }

    @Accept("postProcess")
    public void postProcess(List<String> args) {
        Assert(args.size(), 2);
        String method = args.get(0);
        if (method.equals("add")) {
            switch (args.get(1)) {
                case "print": context.addPostProcess(HttpClientContext.Process.PRINT_TO_CONSOLE); return;
                case "cache": context.addPostProcess(HttpClientContext.Process.CACHE); return;
            }
        }
        if (method.equals("del")) {
            switch (args.get(1)) {
                case "print": context.delPostProcess(HttpClientContext.Process.PRINT_TO_CONSOLE); return;
                case "cache": context.delPostProcess(HttpClientContext.Process.CACHE);
            }
        }
    }

    @Accept(value = "hello", desc = "描述", example = "hello -f")
    public void hello(List<String> args) {
        System.out.println("hello" + args);
    }

    @Accept("test")
    public void test(List<String> args) throws IOException {
        System.out.println(Request.Get("https://baidu.com").execute().returnContent().asString(Consts.UTF_8));
    }

    @Accept(value = "reg", desc = "用户注册", example = "reg 用户名 密码")
    public void register(List<String> args) throws IOException {
        Assert(args.size(), 2);
        String username = args.get(0);
        String password = args.get(1);
        // 请求tokenKey和userKey
        context.executeRequest(Request.Get(getHost() + getTokenKey()));
        Message message = (Message) context.CACHE.get("message");
        if (!message.getMeta().isSuccess()) {
            System.out.println("请求失败");
            return;
        }
        String tokenKey = String.valueOf(context.BODY.get("tokenKey"));
        String userKey = String.valueOf(context.BODY.get("userKey"));
        String enPass = AesUtils.aesEncode(password, tokenKey);

        // 执行注册请求
        context.executeRequest(Request.Post(createUrl(port, "/account/register"))
                .bodyString(toJson(MapPutter
                        .put("uid", userKey)
                        .put("username", username)
                        .put("password", enPass)
                        .put("methodName", "register")
                        .put("methodName", "register")
                        .put("userKey", userKey)
                        .put("timestamp", String.valueOf(context.BODY.get("timestamp")))
                        .get()), ContentType.APPLICATION_JSON));

        message = (Message) context.CACHE.get("message");
        if (message.getMeta().isSuccess()) {
            context.CACHE.put("username", username);
            context.CACHE.put("password", password);
        }
    }

    @Accept(value = "help", desc = "使用帮助", example = "help 或者 help 命令名")
    public void help(List<String> args) {
        int size = args.size();
        if (size == 0 || (size == 1 && args.get(0).equalsIgnoreCase("all"))) {
            DESC_MAP.forEach((k, v) -> printCommand(v));
        } else if (size == 1) {
            Accept accept = DESC_MAP.get(args.get(0));
            if (accept == null) {
                System.out.println(ConsoleColor.RED + "没有这个命令");
                return;
            }
            printCommand(accept);
        }
    }

    @Accept(value = "enable")
    public void enable(List<String> args) throws Exception {
        Assert(args.size(), 1);
        if ("checkServer".equals(args.get(0))) {
            check();
        }
    }

    /**
     * 自动从profile中检测已启用的服务端口
     * @throws URISyntaxException 检测过程中抛出异常，程序终止
     */
    private void check() throws Exception {
        List<File> profiles = new ArrayList<>();
        File resourceDir = new File(Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("")).toURI());
        for (File file : Objects.requireNonNull(resourceDir.listFiles())) {
            String filename = file.getName();
            if (filename.endsWith(".yml")) {
                profiles.add(file);
            }
        }

        Set<Integer> ignorePortSet = new HashSet<>();
        ignorePortSet.add(6379);
        ignorePortSet.add(3306);
        ignorePortSet.add(3308);

        List<Integer> ports = new ArrayList<>();
        for (File file : profiles) {
            try (FileReader reader = new FileReader(file);
                 BufferedReader bufferedReader = new BufferedReader(reader)) {
                String line = bufferedReader.readLine();
                while (line != null) {
                    line = line.trim();
                    if (line.startsWith("port:")) {
                        int port = Integer.parseInt(StringUtils.splitBy(line, ' ').get(1));
                        if (!ignorePortSet.contains(port)) {
                            ports.add(port);
                        }
                    }
                    line = bufferedReader.readLine();
                }
            }
        }

        for (Integer port : ports) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(hostname, port));
                System.out.println("检测到应用在端口" + port + "上运行。");
                this.port = port;
                return;
            } catch (Exception ignore) {
            }
        }

        throw new RuntimeException(ConsoleColor.RED + "未检测到服务，测试程序正在关闭。");
    }

    private String toJson(Map<String, String> map) {
        StringBuilder sb = new StringBuilder(16);
        sb.append('{');
        map.forEach((k, v) -> {
            sb.append("\"").append(k).append("\":").append(v).append(",");
        });
        sb.append('}');
        return sb.toString();
    }

    private String createUrl(int port, String uri) {
        return "http://" + hostname + ":" + port + uri;
    }

    private String getTokenKey() {
        return "/account?tokenKey=get";
    }

    private String getHost() {
        return "http://" + hostname + ":" + port;
    }

    private void printCommand(Accept accept) {
        System.out.println("-------------------------");
        System.out.println("命令名: " + accept.value());
        System.out.println("描述: " + accept.desc());
        System.out.println("示例: " + accept.example());
        System.out.println();
    }

    private void Assert(int len, int acceptLen) {
        if (len != acceptLen)
            throw new RuntimeException("命令项个数与命令处理器不匹配。");
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Accept {

        String value();
        String desc() default "";
        String example() default "";

    }

}
