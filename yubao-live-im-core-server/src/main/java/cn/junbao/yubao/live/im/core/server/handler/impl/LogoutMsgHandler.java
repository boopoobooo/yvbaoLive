package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.im.core.server.dto.ImOfflineDTO;
import cn.junbao.yubao.im.core.server.dto.ImOnlineDTO;
import cn.junbao.yubao.live.common.interfaces.topic.ImCoreServerTopicNames;
import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextCache;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.mq.starter.publisher.EventPublisher;
import com.alibaba.fastjson.JSON;
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

    @Resource
    private EventPublisher eventPublisher;

    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {

        Long userId = ImContextUtil.getUserId(channelHandlerContext);
        Integer appId = ImContextUtil.getAppId(channelHandlerContext);
        if (userId == null || appId == null ){
            log.error("[LogoutMsgHandler登出消息]参数异常，userId 或 appid 为 null, userId = {},appid = {}",userId,appId);
            //直接放弃连接
            channelHandlerContext.close();
            throw new IllegalArgumentException("参数异常，userId 或 appid 为 null !");
        }
        //发送登出确认信号消息包
        this.logoutMsgNotice(channelHandlerContext,userId,appId);
        //进行登出处理
        this.logoutMsgHandler(channelHandlerContext,userId,appId);

    }

    public void logoutMsgHandler(ChannelHandlerContext channelHandlerContext, Long userId, Integer appId){
        log.info("[LogoutMsgHandler] logout success,userId is {},appId is {}", userId, appId);

        //关闭连接,和删除本地缓存及上下文
        redisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId);
        ChannelHandlerContextCache.remove(userId);

        ImContextUtil.removeUserId(channelHandlerContext);
        ImContextUtil.removeAppId(channelHandlerContext);
    }

    /**
     * 登出的时候，发送确认信号，这个是正常网络断开才会发送，异常断线则不发送
     */
    private void logoutMsgNotice(ChannelHandlerContext ctx, Long userId, Integer appId) {
        ImMsgBody respBody = new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("logout is true");
        ImMsg respMsg = ImMsg.build(ImMsgTypeCode.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(respBody));
        ctx.writeAndFlush(respMsg);
        ctx.close();
    }

    /**
     * 发送登出消息Mq
     */
    private void sendLogoutMq(Long userId, Integer appId,Integer roomId) {
        ImOfflineDTO imOnlineDTO = new ImOfflineDTO() ;
        imOnlineDTO.setUserId(userId);
        imOnlineDTO.setAppId(appId);
        imOnlineDTO.setRoomId(roomId);
        imOnlineDTO.setLogOutTime(System.currentTimeMillis());

        //发送mq消息
        try {
            eventPublisher.publish(ImCoreServerTopicNames.IM_OFFLINE_TOPIC, JSON.toJSONString(imOnlineDTO));
            log.info("[sendLogoutMq]Mq消息发送成功:{}", JSON.toJSONString(imOnlineDTO));
        } catch (Exception e) {
            log.error("[sendLogoutMq]Mq消息发送失败！！！ {}",e.getMessage());
        }
    }

}
