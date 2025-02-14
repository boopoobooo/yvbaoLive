package cn.junbao.yubao.live.im.core.server.handler;

import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import io.netty.channel.ChannelHandlerContext;

public interface ISimpleHandler {
    void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg);
}
