package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextHashMap;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogoutMsgHandler implements ISimpleHandler {
    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {

        Long userId = ImContextUtil.getUserId(channelHandlerContext);
        Integer appId = ImContextUtil.getAppId(channelHandlerContext);
        if (userId == null || appId == null ){
            log.error("[LogoutMsgHandler登出消息]参数异常，userId 或 appid 为 null, userId = {},appid = {}",userId,appId);
            throw new IllegalArgumentException("参数异常，userId 或 appid 为 null !");
        }
        //关闭连接
        ChannelHandlerContextHashMap.remove(userId);
        channelHandlerContext.close();
        log.info("[LogoutMsgHandler]用户登出,userId = {}",userId);

    }
}
