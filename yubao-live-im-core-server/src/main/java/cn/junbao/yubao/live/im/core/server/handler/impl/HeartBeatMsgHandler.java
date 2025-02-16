package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.live.framework.redis.starter.key.ImCoreServerCacheKeyBuilder;
import cn.junbao.yubao.live.im.constants.ImConstants;
import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 心跳消息 处理器
 */
@Slf4j
@Component
public class HeartBeatMsgHandler implements ISimpleHandler {

    @Resource
    private RedisTemplate<String ,Object> redisTemplate;

    @Resource
    private ImCoreServerCacheKeyBuilder cacheKeyBuilder;
    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {
        Long userId = ImContextUtil.getUserId(channelHandlerContext);
        Integer appId = ImContextUtil.getAppId(channelHandlerContext);
        if (userId == null || appId == null ){
            log.error("[LogoutMsgHandler登出消息]参数异常，userId 或 appid 为 null, userId = {},appid = {}",userId,appId);
            throw new IllegalArgumentException("参数异常，userId 或 appid 为 null !");
        }
        String cacheKey = cacheKeyBuilder.buildImUserOnline(appId, userId);
        this.recordUserOnlineHeartBeat(cacheKey,userId);
        this.removeUserOnlineTimeOutRecore(cacheKey);
        redisTemplate.expire(cacheKey,5, TimeUnit.MINUTES);

        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setAppId(appId);
        imMsgBody.setUserId(userId);
        imMsgBody.setData("[HeartBeat] true");
        log.info("[HeartBeatMsgHandler] 用户在线心跳记录 userId = {},cacheKey = {}",userId,cacheKey);
        channelHandlerContext.writeAndFlush(ImMsg.build(ImMsgTypeCode.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(imMsgBody)));
    }

    /**
     * 删除超出时间范围的心跳记录 （ 2 * 心跳记录间隔 ）
     */
    private void removeUserOnlineTimeOutRecore(String cacheKey) {
        redisTemplate.opsForZSet().removeRangeByScore(cacheKey,0,
                System.currentTimeMillis()- ImConstants.USER_HEARTBEAT_RECORD_INTERVAL * 2);

    }

    /**
     * 记录用户在线心跳记录
     * @param cacheKey 缓存key
     */
    private void recordUserOnlineHeartBeat(String cacheKey,Long userId) {
        redisTemplate.opsForZSet().add(cacheKey,userId,System.currentTimeMillis());
    }
}
