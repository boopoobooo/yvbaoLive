package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.live.common.interfaces.topic.ImCoreServerTopicNames;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import cn.junbao.yubao.live.mq.starter.EventPublisher;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultBizMsgHandler implements ISimpleHandler {

    @Resource
    private EventPublisher eventPublisher;

    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {
        Long userId = ImContextUtil.getUserId(channelHandlerContext);
        Integer appId = ImContextUtil.getAppId(channelHandlerContext);
        if (userId == null || appId == null ){
            log.error("[DefaultBizMsgHandler]参数异常，userId = {},appid = {}",userId,appId);
            channelHandlerContext.close();
            throw new IllegalArgumentException("参数异常，userId 或 appid 为 null !");
        }

        byte[] msgBody = msg.getBody();
        if (msgBody  == null || msgBody.length == 0 ){
            log.error("[DefaultBizMsgHandler]参数异常，msgBody = {}",msgBody);
            throw new IllegalArgumentException("参数异常，msgBody 为 null !");
        }

        try {
            String messageBody = new String(msgBody);
            log.info("[DefaultBizMsgHandler]正在发送mq消息,body = {}",messageBody);
            eventPublisher.publish(ImCoreServerTopicNames.YUBAO_IM_BIZ_MSG_TOPIC, messageBody);
        } catch (Exception e) {
            log.error("[DefaultBizMsgHandler] mq发送错误 ERROR, e:"+e);
            throw new RuntimeException(e);
        }
    }
}
