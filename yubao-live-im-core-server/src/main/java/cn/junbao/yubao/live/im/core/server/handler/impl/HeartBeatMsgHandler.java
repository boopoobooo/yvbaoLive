package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatMsgHandler implements ISimpleHandler {
    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {
        log.info("HeartBeatMsgHandler------msg = {}",msg);
        channelHandlerContext.writeAndFlush(msg);
    }
}
