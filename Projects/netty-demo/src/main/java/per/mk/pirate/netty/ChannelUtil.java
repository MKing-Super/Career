package per.mk.pirate.netty;


public class ChannelUtil {

    public final static String FLAG = "_";

    public static String getSendMsg(String msg) {
        return msg + FLAG;
    }

}
