package per.mk.pirate.netty;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.TimerTask;

public class NoticeNettyTask extends TimerTask {

    private RequestVo req;

    public NoticeNettyTask(RequestVo req) {
        this.req = req;
    }

    @Override
    public void run() {
        if (null == req || StringUtils.isEmpty(req.getServiceId())) {
            return;
        }

        String msg = ChannelUtil.getSendMsg(JSONObject.toJSONString(req));
        System.out.println(msg);
        System.out.println("------------>>>>>>>>>>>>【NoticeNettyTask】auction server push message to netty...content is ");
        AuctionManager.getInstance().getClient().writeAndFlush(msg);

    }

}