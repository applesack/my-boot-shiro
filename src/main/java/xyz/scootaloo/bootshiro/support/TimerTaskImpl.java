package xyz.scootaloo.bootshiro.support;

import java.util.TimerTask;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月07日 11:03
 */
public class TimerTaskImpl extends TimerTask {

    private final Runnable runnable;

    public TimerTaskImpl(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }

}
