package cn.junbao.yubao.live.im.router.interfaces;

import cn.junbao.yubao.live.im.dto.ImMsgBody;

public interface ImRouterRpc {

    /**
     * 向 im-core-server 发送消息
     *
     * @param imMsgBody
     * @return
     */
    boolean sendMsg(ImMsgBody imMsgBody);
}
