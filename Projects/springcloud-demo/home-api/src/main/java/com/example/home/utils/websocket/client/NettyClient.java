package com.example.home.utils.websocket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    // 服务端IP
    private static String SERVER_IP = "127.0.0.1";
    // 服务端PORT
    private static Integer SERVER_PORT = 8861;

    // 静态默认实例
    private static NettyClient instance = new NettyClient();
    // 通道实例
    private static NioSocketChannel socketChannel = null;

    // 客户端websocket连接 服务端
    public static void connect() {
        log.info("connect() start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        EventLoopGroup group = new NioEventLoopGroup(); // 统一管理 I/O
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)          // NIO 通道
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 入站处理链
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                    1024 * 1024,  // maxFrameLength = 1MB
                                    0,            // lengthFieldOffset (长度字段从0开始)
                                    4,            // lengthFieldLength (int占4字节)
                                    0,            // lengthAdjustment (长度仅含Body)
                                    4             // initialBytesToStrip (丢弃长度字段，只传Body)
                            )); // 切割帧
                            pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));// 解码器

                            // 出站处理链
                            pipeline.addLast(new LengthFieldPrepender(4));  // 添加长度头
                            pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));    // 编码器

                            // 业务逻辑
                            pipeline.addLast(new NettyClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(SERVER_IP, SERVER_PORT).sync(); // 连接服务端
            log.info("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);
            socketChannel = (NioSocketChannel) future.channel();

            // 注册停机钩子实现优雅关闭
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                group.shutdownGracefully(1, 5, TimeUnit.SECONDS); // 安静期1秒，超时5秒
                log.info("Client resources released");
            }));

            // 阻塞至客户端通道关闭
//            future.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
            group.shutdownGracefully();
        } finally {
            log.info("connect() end <<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
    }

    // 设置 ip
    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }

    // 设置 端口
    public static void setServerPort(Integer serverPort) {
        SERVER_PORT = serverPort;
    }

    // 获取实例
    public static NettyClient getInstance() {
        return instance;
    }

    // 写入缓冲区并刷新（发送消息）
    public ChannelFuture writeAndFlush(Object msg){
        try {
            if (socketChannel == null || !socketChannel.isActive()){
                NettyClient.connect();
            }
            return socketChannel.writeAndFlush(msg);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return null;
        }
    }


}