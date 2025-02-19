package cn.junbao.yubao.live.im.core.server.service;

import cn.junbao.yubao.live.im.dto.ImMsgBody;

public interface ImRouterHandlerService {
    /**
     * 当收到业务服务的请求，进行处理
     *
     * @param imMsgBody
     */
    void onReceive(ImMsgBody imMsgBody);
}
