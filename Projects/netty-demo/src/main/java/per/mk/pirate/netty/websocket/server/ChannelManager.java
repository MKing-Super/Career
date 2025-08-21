package per.mk.pirate.netty.websocket.server;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

// 对 channel 管理
public class ChannelManager {

    // 集中存储：将多个 Channel（如客户端连接）存储在同一个线程安全的容器中（内部基于 ConcurrentHashMap），避免手动维护分散的连接
    // 自动清理：当 Channel关闭时，自动从集合中移除，无需手动维护生命周期（通过监听 ChannelFuture实现）
    public static ChannelGroup webSocketGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);// websocket channel集合
    public static ChannelGroup socketGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);// 普通socket channel集合

}
