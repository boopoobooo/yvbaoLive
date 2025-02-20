package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextCache;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AckMsgHandler implements ISimpleHandler {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {


    }
}
