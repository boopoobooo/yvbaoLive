package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextCache;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogoutMsgHandler implements ISimpleHandler {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {

        Long userId = ImContextUtil.getUserId(channelHandlerContext);
        Integer appId = ImContextUtil.getAppId(channelHandlerContext);
        if (userId == null || appId == null ){
            log.error("[LogoutMsgHandler登出消息]参数异常，userId 或 appid 为 null, userId = {},appid = {}",userId,appId);
            throw new IllegalArgumentException("参数异常，userId 或 appid 为 null !");
        }
        //关闭连接,和删除本地缓存及上下文
        redisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId);
        ChannelHandlerContextCache.remove(userId);

        ImContextUtil.removeUserId(channelHandlerContext);
        ImContextUtil.removeAppId(channelHandlerContext);
        log.info("[LogoutMsgHandler]用户登出,userId = {}",userId);

    }
}
