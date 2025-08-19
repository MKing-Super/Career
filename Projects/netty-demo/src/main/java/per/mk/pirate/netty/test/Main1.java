package per.mk.pirate.netty.test;

import java.math.BigDecimal;

/**
 * @describe: TODO
 * @Author MK
 * @PackageName netty-demo
 * @Package per.mk.pirate.netty
 * @Date 2025/8/18 15:34
 * @Version 1.0
 */
public class Main1 {
    public static void main(String[] args) throws Exception {
        AuctionClient client = new AuctionClient();
        AuctionManager.getInstance().setClient(client);
        try {
            client.connect();
//            new HeartBeatClientTask().run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestVo login = new RequestVo(ServiceType.SYNC_LOGIN.getValue(), 1, "sid", new BigDecimal("66.66"), null, 123);
        new NoticeNettyTask(login).run();
        Thread.sleep(1000);
        RequestVo requestVo = new RequestVo(ServiceType.JG_SYNC_BID_APP.getValue(), 1, "sid", new BigDecimal("66.66"), null, 123);
        new NoticeNettyTask(requestVo).run();
    }
}
