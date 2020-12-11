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
    // 线程池，宽度=20
    private final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(20);

    /**
     * 向线程池中提交一个任务，TimerTask在本项目有一个默认的实现。
     * @see xyz.scootaloo.bootshiro.support.TimerTaskImpl
     * 需要使用时创建此对象并在构造器中注入Runnable实现即可。
     * 另外本项目还提供一个创建任务的工厂类，需要时可以从这里快捷获取。
     * @see xyz.scootaloo.bootshiro.support.factory.TaskFactory
     * -------------------------------------------------------
     * @param timerTask 被提交的任务
     */
    public void executeTask(TimerTask timerTask) {
        EXECUTOR.schedule(timerTask, DELAY_TIME, TimeUnit.MICROSECONDS);
    }

    public TaskManager() {
    }

}
