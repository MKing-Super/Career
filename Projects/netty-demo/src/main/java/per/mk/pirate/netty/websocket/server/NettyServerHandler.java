package per.mk.pirate.netty.websocket.server;

import cn.hutool.core.date.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

import java.util.Date;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final ChannelGroup webSocketGroup = ChannelManager.webSocketGroup;
    // 普通 socket 的 channel 集合
    private static final ChannelGroup socketGroup = ChannelManager.socketGroup;

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
        socketGroup.add(ctx.channel());
//        socketGroup.writeAndFlush(new TextWebSocketFrame("["+incoming.remoteAddress()+"] 用户加入"));
        System.out.println("------------------ 后台用户（用户加入） ---------------------------");
        socketGroup.forEach(ch ->
                System.out.println("ID: " + ch.id() + " | Addr: " + ch.remoteAddress())
        );
        System.out.println("----------------------------------------------------");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 4.channelRead() 每次收到数据时");
        System.out.println("服务端收到：" + msg);

        // 广播消息给所有 客户端
        socketGroup.forEach(ch ->{
            // 响应客户端
            String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            ByteBuf response = Unpooled.copiedBuffer("服务端响应-连接建立后发送第一条响应消息。 " + format, CharsetUtil.UTF_8);
            ChannelFuture future = ch.writeAndFlush(response);
            // 监听结果 异步回调（支持链式）
            future.addListener((ChannelFutureListener) f -> {
                String s = "ID: " + ch.id() + " | Addr: " + ch.remoteAddress();
                if (f.isSuccess()) {
                    System.out.println(s + " 异步写入成功~");
                } else {
                    System.out.println(s + " 异步写入失败！ error：" + f.cause());
                }
            });
        });
        // 广播消息给所有 websocket页面
        webSocketGroup.forEach(ch -> {
            ChannelFuture future = ch.writeAndFlush(new TextWebSocketFrame("[" + ch.remoteAddress() + "] " + msg) );
            // 监听结果 异步回调（支持链式）
            future.addListener((ChannelFutureListener) f -> {
                String s = "ID: " + ch.id() + " | Addr: " + ch.remoteAddress();
                if (f.isSuccess()) {
                    System.out.println(s + " 异步写入成功~");
                } else {
                    System.out.println(s + " 异步写入失败！ error：" + f.cause());
                }
            });
        });

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " 5.channelInactive() 连接断开时");
//        socketGroup.writeAndFlush(new TextWebSocketFrame("["+incoming.remoteAddress()+"] 用户离开"));
        System.out.println("------------------ 后台用户（用户离开） ---------------------------");
        socketGroup.forEach(ch ->
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