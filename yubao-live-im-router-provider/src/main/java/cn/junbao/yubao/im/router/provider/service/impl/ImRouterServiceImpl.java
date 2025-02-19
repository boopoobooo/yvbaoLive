package cn.junbao.yubao.im.router.provider.service.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.im.core.server.interfaces.ImRouterHandlerRpc;
import cn.junbao.yubao.im.router.provider.service.ImRouterService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import com.alibaba.cloud.commons.lang.StringUtils;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: Junbao
 * @Date: 2025/2/18 17:06
 * @Description:
 */
@Service
public class ImRouterServiceImpl implements ImRouterService {

    @DubboReference
    private ImRouterHandlerRpc routerHandlerRpc;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public boolean sendMsg(ImMsgBody imMsgBody) {
        String bindAddress = String.valueOf(redisTemplate.opsForValue().get(ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBody.getAppId() + ":" + imMsgBody.getUserId()));
        if (StringUtils.isBlank(bindAddress)){
            return false;
        }
        RpcContext.getContext().set("ip", bindAddress);
        routerHandlerRpc.sendMessage(imMsgBody);
        return true;
    }
}
