package cn.junbao.yubao.live.im.provider.rpc;

import cn.junbao.yubao.live.im.interfaces.ImOnlineRpc;
import cn.junbao.yubao.live.im.provider.service.ImOnlineService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author idea
 */
@DubboService
public class ImOnlineRpcImpl implements ImOnlineRpc {

    @Resource
    private ImOnlineService imOnlineService;

    @Override
    public boolean isOnline(long userId, int appId) {
        return imOnlineService.isOnline(userId,appId);
    }
}
