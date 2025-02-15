package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultBizMsgHandler implements ISimpleHandler {
    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {
        log.info("DefaultBizMsgHandler----msg = {}",msg);
        channelHandlerContext.writeAndFlush(msg);
    }
}
