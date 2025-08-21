package per.mk.pirate.netty.websocket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class NettyClient implements Runnable {

    // 服务端IP
    private final static String SERVER_IP = "127.0.0.1";
    // 服务端PORT
    private final static Integer SERVER_PORT = 8861;

    @Override
    public void run() {
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
            System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);

            // 注册停机钩子实现优雅关闭
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                group.shutdownGracefully(1, 5, TimeUnit.SECONDS); // 安静期1秒，超时5秒
                System.out.println("Client resources released");
            }));

            // 阻塞至客户端通道关闭
            future.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}