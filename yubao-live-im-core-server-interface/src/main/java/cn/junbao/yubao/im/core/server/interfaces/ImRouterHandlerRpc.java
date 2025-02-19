package cn.junbao.yubao.im.core.server.interfaces;

import cn.junbao.yubao.live.im.dto.ImMsgBody;

/**
 * 负责消息的 精确转发
 */
public interface ImRouterHandlerRpc {

    void sendMessage(ImMsgBody imMsgBody);
}
