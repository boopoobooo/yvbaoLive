package cn.junbao.yubao.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class ChannelHandlerContextHashMap {

    private static Map<Long, ChannelHandlerContext> channelHandlerContextMap = new HashMap<>();

    public static ChannelHandlerContext get(Long userId){
        return channelHandlerContextMap.get(userId);
    }
    public static void put(Long userId,ChannelHandlerContext channelHandlerContext){
        channelHandlerContextMap.put(userId,channelHandlerContext);
    }
    public static void remove(Long userId){
        channelHandlerContextMap.remove(userId);
    }

}
