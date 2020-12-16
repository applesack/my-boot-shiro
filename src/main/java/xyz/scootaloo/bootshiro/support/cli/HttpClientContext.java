package xyz.scootaloo.bootshiro.support.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.client.fluent.Request;
import xyz.scootaloo.bootshiro.domain.bo.Message;
import xyz.scootaloo.bootshiro.domain.dto.StatusCodeObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月15日 16:41
 */
public class HttpClientContext {
    // 后处理器
    private final Set<Consumer<String>> POST_PROCESSES = new LinkedHashSet<>();
    // 头部和报文体, 以及缓存数据
    public final Map<String, String> HEADERS = new HashMap<>();
    public final Map<String, Object> BODY = new HashMap<>();
    public final Map<String, Object> CACHE = new HashMap<>();
    public static HttpClientContext INSTANCE;

    // JSON转换
    private static final ObjectMapper CONVERTER = new ObjectMapper();

    public void executeRequest(Request request) throws IOException {
        String responseBody = request.execute().returnContent().asString(Consts.UTF_8);
        // 应用后处理器
        for (Consumer<String> consumer : POST_PROCESSES) {
            consumer.accept(responseBody);
        }
    }

    public void addPostProcess(Process process) {
        POST_PROCESSES.add(process.consumer);
    }

    public void delPostProcess(Process process) {
        POST_PROCESSES.remove(process.consumer);
    }

    public static HttpClientContext getInstance() {
        HttpClientContext instance = new HttpClientContext();
        if (INSTANCE == null)
            INSTANCE = instance;
        return instance;
    }

    public enum Process {
        PRINT_TO_CONSOLE(System.out::println),
        CACHE((String rBody) -> {
            try {
                Message message = CONVERTER.readValue(rBody, Message.class);
                INSTANCE.CACHE.put("message", message);
                System.out.println(message.getMeta());
                System.out.println(message.getData());
                Map<String, Object> data = message.getData();
                StatusCodeObject meta = message.getMeta();
                if (meta.isSuccess()) {
                    INSTANCE.BODY.put(DStr.timestamp, meta.getTimestamp());
                    data.forEach(INSTANCE.BODY::put);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        private final Consumer<String> consumer;

        Process(Consumer<String> consumer) {
            this.consumer = consumer;
        }

        public Consumer<String> get() {
            return this.consumer;
        }
    }

}
