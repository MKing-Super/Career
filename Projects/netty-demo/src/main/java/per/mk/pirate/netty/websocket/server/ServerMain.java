package per.mk.pirate.netty.websocket.server;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @describe: TODO
 * @Author MK
 * @PackageName netty-demo
 * @Package per.mk.pirate.netty.websocket.server
 * @Date 2025/8/21 11:36
 * @Version 1.0
 */
public class ServerMain {
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,          // corePoolSize
            4,      // maximumPoolSize
            100,        // keepAliveTime
            TimeUnit.SECONDS,       // unit
            new ArrayBlockingQueue<>(10), // workQueue（有界队列）
            new DefaultThreadFactory("server-pool"), // threadFactory
            new ThreadPoolExecutor.CallerRunsPolicy() // handler
    );
    public static void main(String[] args) {
        // 页面调用
        executor.execute(new WebsocketServer());
    }
}
