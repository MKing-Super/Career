package com.example.home.utils.websocket;

import com.example.home.utils.websocket.client.NettyClient;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 后台-客户端（socket）
// 测试
public class ClientMain {

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
        NettyClient.setServerIp("127.0.0.1");
        NettyClient.setServerPort(8861);
//        NettyClient.connect();

        System.out.println("====================================");
        NettyClient.getInstance().writeAndFlush("我炸了！");
        NettyClient.getInstance().writeAndFlush("66666");
    }
}
