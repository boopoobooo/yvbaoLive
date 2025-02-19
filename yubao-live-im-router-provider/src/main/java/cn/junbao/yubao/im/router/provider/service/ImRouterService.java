package cn.junbao.yubao.im.router.provider.service;

import cn.junbao.yubao.live.im.dto.ImMsgBody;

public interface ImRouterService {
    /**
     * 向 im-core-server 发送消息到对应客户端
     *
     * @param imMsgBody
     * @return
     */
    boolean sendMsg(ImMsgBody imMsgBody);
}
