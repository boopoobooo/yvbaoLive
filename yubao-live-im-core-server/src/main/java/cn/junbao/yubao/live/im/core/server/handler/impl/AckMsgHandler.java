package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextCache;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import cn.junbao.yubao.live.im.core.server.service.IMsgAckCheckService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
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
    @Resource
    private IMsgAckCheckService msgAckCheckService;

    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {
        Long userId = ImContextUtil.getUserId(channelHandlerContext);
        Integer appid = ImContextUtil.getAppId(channelHandlerContext);
        if (userId == null && appid == null) {
            channelHandlerContext.close();
            throw new IllegalArgumentException("attr is error");
        }
        ImMsgBody imMsgBody = JSON.parseObject(msg.getBody(), ImMsgBody.class);
        log.info("[AckMsgHandler] 收到客户端的ack确认消息包,imMsgBody = {}",imMsgBody);
        msgAckCheckService.doMsgAck(imMsgBody);

    }
}
