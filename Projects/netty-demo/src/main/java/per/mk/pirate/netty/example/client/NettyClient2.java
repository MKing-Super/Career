package per.mk.pirate.netty.example.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyClient2 {
    // 服务端IP
    private final static String SERVER_IP = "127.0.0.1";
    // 服务端PORT
    private final static Integer SERVER_PORT = 8080;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(); // 统一管理 I/O
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)          // NIO 通道
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new LengthFieldPrepender(4));  // 添加长度头
                            ch.pipeline().addLast(new ClientHandler());         // 业务处理器
                        }
                    });
            ChannelFuture future = bootstrap.connect(SERVER_IP, SERVER_PORT).sync(); // 连接服务端
            System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);

            // 阻塞至客户端通道关闭
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
