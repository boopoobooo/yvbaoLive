package cn.junbao.yubao.live.im.core.server.rpc;

import cn.junbao.yubao.im.core.server.interfaces.ImRouterHandlerRpc;
import cn.junbao.yubao.live.im.core.server.service.ImRouterHandlerService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/2/18 16:17
 * @Description:
 */
@DubboService
public class ImRouterHandlerRpcImpl implements ImRouterHandlerRpc {


    @Resource
    private ImRouterHandlerService imRouterHandlerService;

    @Override
    public void sendMessage(ImMsgBody imMsgBody) {
       imRouterHandlerService.onReceive(imMsgBody);
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        for (ImMsgBody imMsgBody : imMsgBodyList) {
            imRouterHandlerService.onReceive(imMsgBody);
        }
    }
}
