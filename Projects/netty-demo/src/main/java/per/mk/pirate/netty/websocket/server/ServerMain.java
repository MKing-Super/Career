package per.mk.pirate.netty.websocket.server;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 服务端 启动
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
