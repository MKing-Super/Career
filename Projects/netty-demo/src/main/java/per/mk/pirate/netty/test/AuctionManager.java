package per.mk.pirate.netty.test;

public class AuctionManager {

    private AuctionManager() {

    }

    private static AuctionManager instance = new AuctionManager();

    private AuctionClient client;

    public static AuctionManager getInstance() {
        return instance;
    }

    public AuctionClient getClient() {
        return client;
    }

    public void setClient(AuctionClient client) {
        this.client = client;
    }
}
