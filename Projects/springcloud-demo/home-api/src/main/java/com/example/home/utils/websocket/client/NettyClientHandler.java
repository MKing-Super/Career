package com.example.home.utils.websocket.client;

import cn.hutool.core.date.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info(incoming.remoteAddress() + " 1.handlerAdded() 处理器添加到 Pipeline时");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception  {
        Channel incoming = ctx.channel();
        log.info(incoming.remoteAddress() + " 2.channelRegistered() 通道绑定到 EventLoop线程时");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info(incoming.remoteAddress() + " 3.channelActive() 连接建立完成时");

        // 连接建立后发送第一条消息
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        ByteBuf msg = Unpooled.copiedBuffer("客户端发起-连接建立后发送第一条消息 Hello Server! " + format , CharsetUtil.UTF_8);
        ChannelFuture future = ctx.writeAndFlush(msg);
        // 监听结果 异步回调（支持链式）
        future.addListener((ChannelFutureListener) f -> {
            String s = "ID: " + incoming.id() + " | Addr: " + incoming.remoteAddress();
            if (f.isSuccess()) {
                log.info(s + " 异步写入成功~");
            } else {
                log.info(s + " 异步写入失败！ error：" + f.cause());
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel incoming = ctx.channel();
        log.info(incoming.remoteAddress() + " 4.channelRead() 每次收到数据时");
        // 客户端 业务异步处理
        log.info("客户端收到: " + msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info(incoming.remoteAddress() + " 5.channelInactive() 连接断开时");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info(incoming.remoteAddress() + " 6.channelUnregistered() 通道从 EventLoop解绑时");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info(incoming.remoteAddress() + " 7.handlerRemoved() 处理器从 Pipeline移除时");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        log.info(incoming.remoteAddress() + " #.exceptionCaught() 任意阶段发生异常时");
        cause.printStackTrace();
        ctx.close(); // 异常时关闭连接
    }

}