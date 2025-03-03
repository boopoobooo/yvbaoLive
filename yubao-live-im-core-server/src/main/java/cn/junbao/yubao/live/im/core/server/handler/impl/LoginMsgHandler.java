package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.im.core.server.dto.ImOnlineDTO;
import cn.junbao.yubao.live.common.interfaces.topic.ImCoreServerTopicNames;
import cn.junbao.yubao.live.im.constants.ImConstants;
import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextCache;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.interfaces.ImTokenRpc;
import cn.junbao.yubao.live.mq.starter.publisher.EventPublisher;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LoginMsgHandler implements ISimpleHandler {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Resource
    private RedisTemplate<String ,Object > redisTemplate;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {

        //防止重复登录
        if (ImContextUtil.getUserId(channelHandlerContext) != null ){
            //重复登录，直接返回
            return;
        }

        byte[] msgBody = msg.getBody();
        if (msgBody == null || msgBody.length == 0 ){
            log.error("[LoginMsgHandler]参数异常，body is null ");
            throw new IllegalArgumentException("body is null !");
        }
        ImMsgBody imMsgBody = JSON.parseObject(new String(msgBody), ImMsgBody.class);
        Long userIdFromMsg = imMsgBody.getUserId();
        Integer appIdFromMsg = imMsgBody.getAppId();

        String token = imMsgBody.getToken();
        if (StringUtils.isBlank(token) || (userIdFromMsg == null)){
            log.error("[LoginMsgHandler]参数异常，当前参数： appId = {}, userId from msg = {}, token = {} ",appIdFromMsg,userIdFromMsg,token);
            throw new IllegalArgumentException("param is error !");
        }
        //获取用户登录消息token
        Long userIdByToken = imTokenRpc.getUserIdByToken(imMsgBody.getToken());
        log.info("[LoginMsgHandler]  userIdByToken = {}, useridofMsg = {}",userIdByToken,imMsgBody.getUserId());

        if (userIdByToken != null && userIdByToken.equals(imMsgBody.getUserId())){
            loginSuccessHandler(channelHandlerContext,userIdFromMsg,appIdFromMsg,null);
            return;
        }//

        //token校验失败,断开连接
        channelHandlerContext.close();
        log.error("[LoginMsgHandler]校验失败，token is error ");
        throw new IllegalArgumentException("token is error !");
    }



    /**
     * 如果用户登录成功则处理相关记录
     *
     * @param ctx
     * @param userId
     * @param appId
     */
    public void loginSuccessHandler(ChannelHandlerContext ctx, Long userId, Integer appId, Integer roomId) {
        //按照userId保存好相关的channel对象信息
        ChannelHandlerContextCache.put(userId, ctx);
        ImContextUtil.setUserId(ctx, userId);
        ImContextUtil.setAppId(ctx, appId);
        if (roomId != null) {
            ImContextUtil.setRoomId(ctx, roomId);
        }
        //将im消息回写给客户端
        ImMsgBody respBody = new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgTypeCode.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));

        //在redis中存储 对应的im服务器ip地址 和 用户userid的映射关系
        redisTemplate.opsForValue().set(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId,
                ChannelHandlerContextCache.getServerIpAddress() + "%" + userId,
                ImConstants.USER_HEARTBEAT_RECORD_INTERVAL * 2, TimeUnit.SECONDS);
        log.info("[LoginMsgHandler] login success,userId is {},appId is {}", userId, appId);
        ctx.writeAndFlush(respMsg);
        sendLoginMq(userId, appId, roomId);

    }

    private void sendLoginMq(Long userId, Integer appId, Integer roomId){
        ImOnlineDTO imOnlineDTO = new ImOnlineDTO() ;
        imOnlineDTO.setUserId(userId);
        imOnlineDTO.setAppId(appId);
        imOnlineDTO.setRoomId(roomId);
        imOnlineDTO.setLoginTime(System.currentTimeMillis());

        //发送mq消息
        try {
            eventPublisher.publish(ImCoreServerTopicNames.IM_ONLINE_TOPIC,JSON.toJSONString(imOnlineDTO));
            log.info("[sendMqMessage]Mq消息发送成功:{}",JSON.toJSONString(imOnlineDTO));
        } catch (Exception e) {
            log.error("[sendMqMessage]Mq消息发送失败！！！ {}",e.getMessage());
        }
    }
}
