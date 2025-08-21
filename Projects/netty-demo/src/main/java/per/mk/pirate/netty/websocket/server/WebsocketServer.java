package per.mk.pirate.netty.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.concurrent.TimeUnit;

public class WebsocketServer implements Runnable {

    // 服务端PORT
    private final static Integer SERVER_PORT = 8080;

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);    // 处理连接请求
        EventLoopGroup workerGroup = new NioEventLoopGroup();            // 处理数据读写
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)               // NIO 通道
                    .option(ChannelOption.SO_BACKLOG, 128)         // 连接队列大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保活机制
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 入站处理链
                            pipeline.addLast(new HttpServerCodec());// HTTP编解码器
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));// 聚合HTTP请求为完整报文（最大64KB）
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));// 处理WebSocket协议升级与帧管理

                            // 业务逻辑
                            pipeline.addLast(new WebsocketHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(SERVER_PORT).sync(); // 绑定端口
            System.out.println("Server started on port " + SERVER_PORT);

            // 注册停机钩子实现优雅关闭
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully(1, 5, TimeUnit.SECONDS); // 安静期1秒，超时5秒
                workerGroup.shutdownGracefully(1, 5, TimeUnit.SECONDS);
                System.out.println("Server resources released");
            }));

            // 阻塞至关闭
            future.channel().closeFuture().sync();
        }catch (Exception e){
          e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
