package per.mk.pirate.netty;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;
import java.util.Date;

public class AuctionClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionClient.class);

    private SocketChannel socketChannel;

    private Date updateTime = new Date();

    public void connect() throws InterruptedException {
        ChannelFuture future = null;
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.group(eventLoopGroup);
            bootstrap.remoteAddress("127.0.0.1",20880);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new DelimiterBasedFrameDecoder(2048, Unpooled.copiedBuffer(ChannelUtil.FLAG.getBytes())));
                    socketChannel.pipeline().addLast(
                            new StringDecoder(Charset.forName("UTF-8")));
                    socketChannel.pipeline().addLast(
                            new StringEncoder(Charset.forName("UTF-8")));
                    socketChannel.pipeline().addLast(
                            new AuctionClientHandler());
                }
            });
            future = bootstrap.connect("127.0.0.1",20880).sync();
            this.socketChannel = (SocketChannel) future.channel();
        } catch (Exception e) {
            LOGGER.error("AuctionClient.connect()..." , e);
            eventLoopGroup.shutdownGracefully();
        } finally {
        }
    }

    public ChannelFuture register() {
        RequestVo reqVo = new RequestVo();
        reqVo.setServiceId(ServiceType.SERVICE_HEART_BEAT.getValue());
        return writeAndFlush(JSONObject.toJSONString(reqVo));
    }

    public ChannelFuture writeAndFlush(Object msg) {
        if (null == socketChannel || !socketChannel.isActive()) {
            try {
                connect();
            } catch (InterruptedException e) {
                LOGGER.error("AuctionClient.writeAndFlush()..." , e);
            }
        }

        return socketChannel.writeAndFlush(msg);
    }

    public ChannelFuture write(Object msg) {
        if (null == socketChannel || !socketChannel.isActive()) {
            try {
                connect();
            } catch (InterruptedException e) {
                LOGGER.error("AuctionClient.write()..." , e);
            }
        }
        return socketChannel.write(msg);
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
}
