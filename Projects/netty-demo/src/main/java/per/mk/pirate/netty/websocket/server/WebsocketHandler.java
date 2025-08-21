package per.mk.pirate.netty.websocket.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

// SimpleChannelInboundHandler 只处理文本帧，更适合websocket
public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>  {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " 1.handlerAdded() 处理器添加到 Pipeline时");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception  {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " 2.channelRegistered() 通道绑定到 EventLoop线程时");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " 3.channelActive() 连接建立完成时");
        channels.add(ctx.channel());
        channels.writeAndFlush(new TextWebSocketFrame("["+incoming.remoteAddress()+"] 用户加入"));
        System.out.println("------------------ 在线用户（用户加入） ---------------------------");
        channels.forEach(ch ->
                System.out.println("ID: " + ch.id() + " | Addr: " + ch.remoteAddress())
        );
        System.out.println("----------------------------------------------------");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 4.channelRead0() 每次收到数据时");
        // 广播消息给所有客户端
        channels.forEach(channel1 ->{
            channel1.writeAndFlush(new TextWebSocketFrame("[" + channel.remoteAddress() + "] " + msg.text()));
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " 5.channelInactive() 连接断开时");
        channels.writeAndFlush(new TextWebSocketFrame("["+incoming.remoteAddress()+"] 用户离开"));
        System.out.println("------------------ 在线用户（用户离开） ---------------------------");
        channels.forEach(ch ->
                System.out.println("ID: " + ch.id() + " | Addr: " + ch.remoteAddress())
        );
        System.out.println("----------------------------------------------------");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " 6.channelUnregistered() 通道从 EventLoop解绑时");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " 7.handlerRemoved() 处理器从 Pipeline移除时");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " #.exceptionCaught() 任意阶段发生异常时");
        cause.printStackTrace();
        ctx.close(); // 异常时关闭连接
    }

}