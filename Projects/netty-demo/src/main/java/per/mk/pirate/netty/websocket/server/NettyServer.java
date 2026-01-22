package per.mk.pirate.netty.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class NettyServer implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    // 服务端PORT
    private static Integer SERVER_PORT = 8861;

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
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                    1024 * 1024,  // maxFrameLength = 1MB
                                    0,            // lengthFieldOffset (长度字段从0开始)
                                    4,            // lengthFieldLength (int占4字节)
                                    0,            // lengthAdjustment (长度仅含Body)
                                    4             // initialBytesToStrip (丢弃长度字段，只传Body)
                            )); // 切割帧
                            pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));// 解码器

                            // 出站处理链
                            pipeline.addLast(new LengthFieldPrepender(4)); // 添加长度头
                            pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));    // 编码器

                            // 业务逻辑
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(SERVER_PORT).sync(); // 绑定端口
            log.info("NettyServer started on port " + SERVER_PORT);

            // 注册停机钩子实现优雅关闭
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully(1, 5, TimeUnit.SECONDS); // 安静期1秒，超时5秒
                workerGroup.shutdownGracefully(1, 5, TimeUnit.SECONDS);
                log.warn("NettyServer resources released");
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

    public static void setServerPort(Integer serverPort) {
        SERVER_PORT = serverPort;
    }
}
