package cn.junbao.yubao.live.im.router.interfaces;

import cn.junbao.yubao.live.im.dto.ImMsgBody;

import java.util.List;

public interface ImRouterRpc {

    /**
     * 向 im-core-server 发送消息
     *
     * @param imMsgBody
     * @return
     */
    boolean sendMsg(ImMsgBody imMsgBody);

    /**
     * 批量发送消息到对应客户端  直播间消息场景
     */
    void batchSendMsg(List<ImMsgBody> imMsgBodyList) ;
}
