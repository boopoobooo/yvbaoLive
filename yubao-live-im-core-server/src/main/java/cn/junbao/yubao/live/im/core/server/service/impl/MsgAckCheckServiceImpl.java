package cn.junbao.yubao.live.im.core.server.service.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.live.common.interfaces.topic.ImCoreServerTopicNames;
import cn.junbao.yubao.live.framework.redis.starter.key.ImCoreServerCacheKeyBuilder;
import cn.junbao.yubao.live.im.core.server.service.IMsgAckCheckService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.mq.starter.publisher.EventPublisher;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: Junbao
 * @Date: 2025/2/20 09:51
 * @Description:
 */
@Slf4j
@Service
public class MsgAckCheckServiceImpl implements IMsgAckCheckService {

    @Resource
    private RedisTemplate<String ,Object> redisTemplate;

    @Resource
    private ImCoreServerCacheKeyBuilder imCoreServerCacheKeyBuilder;
    @Resource
    private EventPublisher eventPublisher;
    @Override
    public void doMsgAck(ImMsgBody imMsgBody) {
        //移除redis中的记录
        int appId = imMsgBody.getAppId();
        Long userId = imMsgBody.getUserId();
        redisTemplate.delete(imCoreServerCacheKeyBuilder.buildImMsgAckMapKey(appId,userId));
    }

    @Override
    public void recordMsgAck(ImMsgBody imMsgBody, int times) {
        int appId = imMsgBody.getAppId();
        Long userId = imMsgBody.getUserId();
        String msgId = imMsgBody.getMsgId();
        redisTemplate.opsForHash().put(imCoreServerCacheKeyBuilder.buildImMsgAckMapKey(appId,userId),msgId,times);
    }

    @Override
    public void sendDelayMsg(ImMsgBody imMsgBody) {
        String msgBodyStr = JSON.toJSON(imMsgBody).toString();
        try {
            eventPublisher.publishDelayedMessage(ImCoreServerTopicNames.YUBAO_IM_MSG_ACK_TOPIC,msgBodyStr, ImCoreServerConstants.ACK_MSG_DELAY_TIME);

        }catch (Exception e ){
            log.error("[sendDelayMsg] error = "+e);
        }

    }

    @Override
    public int getMsgAckTimes(String msgId, long userId, int appId) {
        Object value = redisTemplate.opsForHash().get(imCoreServerCacheKeyBuilder.buildImMsgAckMapKey(appId, userId), msgId);
        if (value == null) {
            return -1;
        }
        return (int) value;
    }
}
