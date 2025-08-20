package per.mk.pirate.netty.example.client;

import cn.hutool.core.date.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import per.mk.pirate.netty.example.CustomThreadFactory;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    // corePoolSize     核心线程数（常驻线程，空闲时不会被销毁）
    // maximumPoolSize  最大线程数（含核心线程）
    // keepAliveTime    非核心线程的空闲存活时间
    // unit             keepAliveTime的时间单位
    // workQueue        任务队列，缓存待执行任务
    // threadFactory    线程工厂，定制线程命名/优先级
    // handler          拒绝策略（当队列和线程池均满时触发）
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4,          // corePoolSize
            8,      // maximumPoolSize
            30,        // keepAliveTime
            TimeUnit.SECONDS,       // unit
            new ArrayBlockingQueue<>(100), // workQueue（有界队列）
            new CustomThreadFactory("client-pool"), // threadFactory：自定义 CustomThreadFactory
            new ThreadPoolExecutor.CallerRunsPolicy() // handler
    );

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
//        System.out.println("Client:" + incoming.remoteAddress() + " 在线~");

        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        // 连接建立后发送第一条消息
        ByteBuf msg = Unpooled.copiedBuffer("客户端发起-连接建立后发送第一条消息 Hello Server! " + format , CharsetUtil.UTF_8);
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " 4.channelRead() 每次收到数据时");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // 客户端 业务异步处理
                ByteBuf buf = (ByteBuf) msg;
                System.out.println("客户端收到: " + buf.toString(CharsetUtil.UTF_8));
            }
        });

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println(incoming.remoteAddress() + " 5.channelInactive() 连接断开时");
//        System.out.println("Client:" + incoming.remoteAddress() + " 掉线！！");
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