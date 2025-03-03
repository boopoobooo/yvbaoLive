package cn.junbao.yubao.im.router.provider.rpc;

import cn.junbao.yubao.im.router.provider.service.ImRouterService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.router.interfaces.ImRouterRpc;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/2/18 16:21
 * @Description:
 */
@DubboService
public class ImRouterRpcImpl implements ImRouterRpc {

    @Resource
    private ImRouterService imRouterService;

    @Override
    public boolean sendMsg(ImMsgBody imMsgBody) {
        return imRouterService.sendMsg(imMsgBody);
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        imRouterService.batchSendMsg(imMsgBodyList);
    }
}
