package per.mk.pirate.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class AuctionClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String m = (String) msg;

        RequestVo requestVo = JSONObject.parseObject(m, RequestVo.class);

        if (ServiceType.SERVICE_HEART_BEAT.getValue().equals(
                requestVo.getServiceId())) {// 心跳
            AuctionManager.getInstance().getClient().setUpdateTime(new Date());
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
