package xyz.scootaloo.bootshiro.support;

import org.springframework.stereotype.Component;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 10:35
 */
@Component
public class TaskManager {

    // 默认延迟时间
    private static final int DELAY_TIME = 10;

    private final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(20);

    /**
     * 提交一个任务
     * @param timerTask 被提交的任务
     */
    public void executeTask(TimerTask timerTask) {
        EXECUTOR.schedule(timerTask, DELAY_TIME, TimeUnit.MICROSECONDS);
    }

    public TaskManager() {
    }

}
