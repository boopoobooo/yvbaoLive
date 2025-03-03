package cn.junbao.yubao.im.core.server.interfaces;

import cn.junbao.yubao.live.im.dto.ImMsgBody;

import java.util.List;

/**
 * 负责消息的 精确转发
 */
public interface ImRouterHandlerRpc {

    void sendMessage(ImMsgBody imMsgBody);

    /**
     * 支持批量发送消息
     *
     * @param imMsgBodyList
     */
    void batchSendMsg(List<ImMsgBody> imMsgBodyList);
}
