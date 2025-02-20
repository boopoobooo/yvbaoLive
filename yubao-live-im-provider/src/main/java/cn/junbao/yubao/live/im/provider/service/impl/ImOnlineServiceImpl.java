package cn.junbao.yubao.live.im.provider.service.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.live.im.constants.ImConstants;
import cn.junbao.yubao.live.im.provider.service.ImOnlineService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author idea
 * @Date: Created in 09:30 2023/7/16
 * @Description
 */
@Service
@Slf4j
public class ImOnlineServiceImpl implements ImOnlineService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean isOnline(long userId, int appId) {
        Boolean result = redisTemplate.hasKey(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
        log.debug("[isOnline] userId = {},appid ={}, isOnline = {}",userId,appId,result);
        return result;
    }

}
