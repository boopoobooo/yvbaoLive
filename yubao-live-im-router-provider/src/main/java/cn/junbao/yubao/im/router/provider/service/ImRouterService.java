package cn.junbao.yubao.im.router.provider.service;

import cn.junbao.yubao.live.im.dto.ImMsgBody;

import java.util.List;

public interface ImRouterService {
    /**
     * 向 im-core-server 发送消息到对应客户端
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
