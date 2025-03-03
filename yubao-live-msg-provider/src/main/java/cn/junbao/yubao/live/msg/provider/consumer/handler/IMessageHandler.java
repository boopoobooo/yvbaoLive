package cn.junbao.yubao.live.msg.provider.consumer.handler;

import cn.junbao.yubao.live.im.dto.ImMsgBody;

/**
 *  处理Mq的消息包
 */
public interface IMessageHandler {
    void onMesReceive(ImMsgBody imMsgBody);
}
