package per.mk.pirate.netty.websocket.server;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 后台-服务端（websocket、socket）
public class ServerMain {
    private static final Logger log = LoggerFactory.getLogger(ServerMain.class);

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
        int websocketServerPort = 8860;
        int nettyServerPort = 8861;
        log.info("参数个数：{}",args.length);
        for (int i = 0 ; i < args.length ; i++){
            log.info("参数 {} -> {}",i,args[i]);
            if (i == 0){
                websocketServerPort = Integer.parseInt(args[0]);
            }else if (i == 1){
                nettyServerPort = Integer.parseInt(args[1]);
            }
        }
        log.info("========================================");
        log.info("websocketServerPort -> {} ,nettyServerPort -> {}",websocketServerPort,nettyServerPort);

        WebsocketServer.setServerPort(websocketServerPort);
        NettyServer.setServerPort(nettyServerPort);
        // 页面调用
        executor.execute(new WebsocketServer());
        // 后台方法调用
        executor.execute(new NettyServer());
    }
}
