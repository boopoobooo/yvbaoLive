package cn.junbao.yubao.live.im.core.server.handler.factory;

import io.netty.channel.ChannelHandlerContext;

/**
 * IM 处理器工厂
 */
public interface ImHandlerFactory {

    void doMsgHandler(ChannelHandlerContext channelHandlerContext, Object msg);
}
