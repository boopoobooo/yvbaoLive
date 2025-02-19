package cn.junbao.yubao.live.msg.provider.consumer.handler;

import cn.junbao.yubao.live.im.dto.ImMsgBody;

public interface IMessageHandler {
    void onMesReceive(ImMsgBody imMsgBody);
}
