package per.mk.pirate.netty.test;


import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.TimerTask;

public class HeartBeatClientTask extends TimerTask {

    @Override
    public void run() {
        AuctionClient client = AuctionManager.getInstance().getClient();

        long interval = (new Date().getTime() - client.getUpdateTime()
                .getTime()) / 1000;

        if (interval > 25) {// 超出5个心跳，重新连接
            try {
                client.connect();
            } catch (InterruptedException e) {
            }
        }

        try {
            RequestVo requestVo = new RequestVo();

            requestVo.setServiceId(ServiceType.SERVICE_HEART_BEAT.getValue());
            requestVo.setJsonData(client.getSocketChannel().id().asLongText());

            client.writeAndFlush(ChannelUtil.getSendMsg(JSONObject
                    .toJSONString(requestVo)));// 发送心跳到netty
        } catch (Exception e) {
        }
    }
}
