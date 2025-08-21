package per.mk.pirate.netty.example.server;

import cn.hutool.core.date.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Promise;
import per.mk.pirate.netty.example.CustomThreadFactory;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerHandler extends ChannelInboundHandlerAdapter {

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
            new CustomThreadFactory("server-pool"), // threadFactory：自定义 CustomThreadFactory
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
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 4.channelRead() 每次收到数据时");

        // 服务端 业务异步处理
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteBuf buf = (ByteBuf) msg;
                    String received = buf.toString(CharsetUtil.UTF_8); // 解码消息
                    System.out.println("服务端收到: " + received);
                    Thread.sleep(2000);
                    // 响应客户端
                    String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
                    ByteBuf response = Unpooled.copiedBuffer("服务端响应-连接建立后发送第一条响应消息。 " + format, CharsetUtil.UTF_8);
                    ChannelFuture future = ctx.writeAndFlush(response);// 自动释放 ByteBuf

                    // 监听结果 异步回调（支持链式）
                    future.addListener((ChannelFutureListener) f -> {
                        if (f.isSuccess()) {
                            System.out.println("异步写入成功1");
                        } else {
                            System.out.println("异步写入失败1: " + f.cause());
                        }
                    }).addListener((ChannelFutureListener) f -> {
                        if (f.isSuccess()) {
                            System.out.println("异步写入成功2");
                        } else {
                            System.out.println("异步写入失败2: " + f.cause());
                        }
                    });

                    EventLoop eventLoop = channel.eventLoop();
                    // 创建 Promise 容器
                    Promise<Integer> promise = eventLoop.newPromise();
                    // 异步任务完成后设置结果
                    eventLoop.execute(() -> {
                        try {
                            promise.setSuccess(1); // 手动设置成功
                        } catch (Exception e) {
                            promise.setFailure(e); // 手动设置失败
                        }
                    });
                    // 获取结果
                    promise.addListener(f -> {
                        if (f.isSuccess()) {
                            System.out.println("结果: " + f.getNow());
                        }
                    });

                }catch (Exception e){
                    System.out.println("服务端 业务异步处理 异常");
                    e.printStackTrace();
                }
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