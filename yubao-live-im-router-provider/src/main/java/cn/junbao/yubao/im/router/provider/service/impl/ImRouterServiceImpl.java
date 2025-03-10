package cn.junbao.yubao.im.router.provider.service.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.im.core.server.interfaces.ImRouterHandlerRpc;
import cn.junbao.yubao.im.router.provider.service.ImRouterService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import com.alibaba.cloud.commons.lang.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Junbao
 * @Date: 2025/2/18 17:06
 * @Description:
 */
@Service
@Slf4j
public class ImRouterServiceImpl implements ImRouterService {

    @DubboReference(check = false)
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

    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        List<Long> userIdList = imMsgBodyList.stream().map(ImMsgBody::getUserId).toList();
        log.info("[batchSendMsg] userIdList: {}", userIdList);
        //将不同的userId的 immsgbody分类存入map, 得到 userId->immsgbody的映射
        Map<Long, ImMsgBody> userIdMsgMap = imMsgBodyList.stream().collect(Collectors.toMap(ImMsgBody::getUserId, x -> x));
        //构建缓存key
        Integer appId = imMsgBodyList.get(0).getAppId();
        List<String> cacheKeyList = new ArrayList<>();
        userIdList.forEach(userId -> {
            String cacheKey = ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId;
            cacheKeyList.add(cacheKey);
        });
        //批量取出每个用户绑定的ip地址   value为 ip:port%userId
        List<Object> ipList = redisTemplate.opsForValue().multiGet(cacheKeyList).stream().filter(x -> x != null).collect(Collectors.toList());
        //构建 ip -》 List<userId> 的映射
        Map<String, List<Long>> ipUserIdMap = new HashMap<>();
        for (Object object : ipList) {
            String ipAddress = String.valueOf(object);
            String currentIp = ipAddress.substring(0, ipAddress.indexOf("%"));
            String userIdStr = ipAddress.substring(ipAddress.indexOf("%") + 1);
            Long userId = Long.parseLong(userIdStr);

            ipUserIdMap.computeIfAbsent(currentIp, k -> new ArrayList<>()).add(userId);
        }

        //将连接同一台ip地址的imMsgBody组装到同一个list集合中，然后进行统一的发送
        for (String currentIp : ipUserIdMap.keySet()) {
            RpcContext.getContext().set("ip", currentIp);
            List<ImMsgBody> batchSendMsgGroupByIpList = new ArrayList<>();
            List<Long> ipBindUserIdList = ipUserIdMap.get(currentIp);
            for (Long userId : ipBindUserIdList) {
                ImMsgBody imMsgBody = userIdMsgMap.get(userId);
                batchSendMsgGroupByIpList.add(imMsgBody);
            }

            //调用发送消息的方法
            routerHandlerRpc.batchSendMsg(batchSendMsgGroupByIpList);
        }


    }
}
