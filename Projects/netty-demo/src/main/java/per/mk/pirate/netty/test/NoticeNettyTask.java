package per.mk.pirate.netty.test;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.TimerTask;

public class NoticeNettyTask extends TimerTask {

    private RequestVo req;

    public NoticeNettyTask(RequestVo req) {
        this.req = req;
    }

    @Override
    public void run() {
        if (null == req || StrUtil.isBlank(req.getServiceId())) {
            return;
        }

        String msg = ChannelUtil.getSendMsg(JSONObject.toJSONString(req));
        System.out.println(msg);
        System.out.println("------------>>>>>>>>>>>>【NoticeNettyTask】auction server push message to netty...content is ");
        AuctionManager.getInstance().getClient().writeAndFlush(msg);

    }

}