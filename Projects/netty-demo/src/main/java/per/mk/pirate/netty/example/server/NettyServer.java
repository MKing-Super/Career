package per.mk.pirate.netty.example.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.concurrent.TimeUnit;

// 服务端启动类
public class NettyServer {

    // 服务端PORT
    private final static Integer SERVER_PORT = 8080;

    public static void main(String[] args) throws InterruptedException {
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
                            // 添加处理器链
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4)); // 解决粘包
                            ch.pipeline().addLast(new ServerHandler()); // 业务处理器
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
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}