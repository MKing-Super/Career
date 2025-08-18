package per.mk.pirate.netty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Timer;

public class Main {
    public static void main(String[] args) throws Exception {
        contextInitialized();
        Thread.sleep(1000);
        RequestVo login = new RequestVo(ServiceType.SYNC_LOGIN.getValue(), 1, "sid", new BigDecimal("66.66"), null, 123);
        new NoticeNettyTask(login).run();
        Thread.sleep(1000);
        RequestVo requestVo = new RequestVo(ServiceType.JG_SYNC_BID_APP.getValue(), 1, "sid", new BigDecimal("66.66"), null, 123);
        new NoticeNettyTask(requestVo).run();
    }

    public static void contextInitialized() {
        AuctionClient client = new AuctionClient();
        AuctionManager.getInstance().setClient(client);

//        new Timer().schedule(new HeartBeatClientTask(), new Date(), 5000);// 每5秒心跳检测
        try {
            client.connect();
            new HeartBeatClientTask().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
