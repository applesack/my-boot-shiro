package xyz.scootaloo.bootshiro.support;

import org.springframework.stereotype.Component;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务管理器，一般用于存储日志
 * 任务在线程池中执行，不影响主业务效率
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 10:35
 */
@Component
public class TaskManager {

    // 默认延迟时间
    private static final int DELAY_TIME = 10;

    private final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(20);

    /**
     * 向线程池中提交一个任务
     * @param timerTask 被提交的任务
     */
    public void executeTask(TimerTask timerTask) {
        EXECUTOR.schedule(timerTask, DELAY_TIME, TimeUnit.MICROSECONDS);
    }

    public TaskManager() {
    }

}
