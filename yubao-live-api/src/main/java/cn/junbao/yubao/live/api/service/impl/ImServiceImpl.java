package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.ImService;
import cn.junbao.yubao.live.api.vo.resp.ImConfigVO;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.interfaces.ImTokenRpc;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/2/28 14:23
 * @Description:
 */
@Service
@Slf4j
public class ImServiceImpl implements ImService {

    @DubboReference(check = false)
    private ImTokenRpc imTokenRpc;

    @Resource
    private DiscoveryClient discoveryClient;
    @Override
    public ImConfigVO getImConfig() {
        ImConfigVO imConfigVO = new ImConfigVO();
        Long userId = WebRequestContext.getUserId();
        if (userId == null){
            log.info("[getImConfig] userId is null未登录");
            //todo 未登录的处理逻辑
        }
        imConfigVO.setToken(imTokenRpc.createImLoginToken(userId, AppIdEnum.YUBAO_LIVE_BIZ.getAppId()));
        buildImServerAddress(imConfigVO);
        return imConfigVO;
    }

    private void buildImServerAddress(ImConfigVO imConfigVO){
        List<ServiceInstance> instances = discoveryClient.getInstances("yubao-live-im-core-server");
        Collections.shuffle(instances);
        ServiceInstance instance = instances.get(0);
        imConfigVO.setWsImServerAddress(instance.getHost()+":"+"8086");
        imConfigVO.setTcpImServerAddress(instance.getHost()+":"+"8085");
    }
}
