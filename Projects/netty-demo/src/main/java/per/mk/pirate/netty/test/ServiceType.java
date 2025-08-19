package per.mk.pirate.netty.test;


public enum ServiceType {

    CLIENT_HEART_BEAT("0000", "客户端心跳"),
    SERVICE_HEART_BEAT("0001", "服务端心跳"),
    SYNC_LOGIN("1000", "登录同步拍直播室"),
    SYNC_BID_APP("1001", "同步拍APP出价"),
    SYNC_BID_PC("1002", "同步拍PC出价"),
    SYNC_BID_ADMIN("1003", "同步拍助手出价"),
    SYNC_OPER_ADMIN("1004", "同步拍助手操作"),

    // SC_开头的为（SmallCircle）私域拍操作
    SC_SYNC_BID_APP("2000", "私域拍APP出价"),
    SC_SYNC_LOGIN("2001", "登录私域拍直播室"),
    SC_SYNC_LOGOUT("2002", "退出私域拍直播室"),
    CHANGE_REVERSE_PRICE("3001","委托方修改保留价"),


    JG_SYNC_BID_APP("5000", "机构拍APP出价"),
    JG_SYNC_AUCTION_BEGIN("5001", "机构拍拍卖会开始"),
    JG_SYNC_AUCTION_END("5002", "机构拍拍卖会结束"),
    JG_SYNC_AUCTION_PACKAGE_END("5003", "机构拍拍品包结束");

    private ServiceType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private String value;

    private String desc;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}

