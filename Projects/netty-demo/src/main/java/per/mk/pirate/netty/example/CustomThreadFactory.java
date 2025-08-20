package per.mk.pirate.netty.example;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {
    // 线程安全计数器 使用 AtomicInteger生成唯一线程ID，避免并发冲突
    private final AtomicInteger threadCounter = new AtomicInteger(1);
    // 线程名前缀
    private final String namePrefix;

    public CustomThreadFactory(String poolName) {
        this.namePrefix = poolName + "-thread-"; // 如 "order-pool-thread-"
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, namePrefix + threadCounter.getAndIncrement());
        thread.setDaemon(false); // 默认为非守护线程
        thread.setPriority(Thread.NORM_PRIORITY); // 默认优先级
        thread.setUncaughtExceptionHandler((t, e) ->
                System.err.println("线程 " + t.getName() + " 异常: " + e)
        ); // 统一异常处理
        return thread;
    }
}
