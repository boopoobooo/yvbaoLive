package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.constants.ImConstants;
import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextCache;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.interfaces.ImTokenRpc;
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
        if (StringUtils.isBlank(token) || (userIdFromMsg == null) || appIdFromMsg < 1000){
            log.error("[LoginMsgHandler]参数异常，当前参数： appId = {}, userId from msg = {}, token = {} ",appIdFromMsg,userIdFromMsg,token);
            throw new IllegalArgumentException("param is error !");
        }
        //获取用户登录消息token
        Long userIdByToken = imTokenRpc.getUserIdByToken(imMsgBody.getToken());
        log.info("[LoginMsgHandler]  userIdByToken = {}, useridofMsg = {}",userIdByToken,imMsgBody.getUserId());
        if (userIdByToken != null && userIdByToken.equals(imMsgBody.getUserId())){
            //token校验成功，允许建立连接
            //将channelHandlerContext加入缓存
            ChannelHandlerContextCache.put(userIdByToken,channelHandlerContext);
            ImContextUtil.setUserId(channelHandlerContext,userIdByToken);
            ImContextUtil.setAppId(channelHandlerContext,appIdFromMsg);

            ImMsgBody respBody = new ImMsgBody();
            respBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
            respBody.setUserId(userIdByToken);
            respBody.setData("Login true");
            ImMsg respMsg = ImMsg.build(ImMsgTypeCode.IM_LOGIN_MSG.getCode(),JSON.toJSONString(respBody));

            //将上下文的ip地址端口信息，存入redis中
            redisTemplate.opsForValue().set(ImCoreServerConstants.IM_BIND_IP_KEY+appIdFromMsg+":"+userIdFromMsg,
                    ChannelHandlerContextCache.getServerIpAddress(),
                    ImConstants.USER_HEARTBEAT_RECORD_INTERVAL * 2 ,
                    TimeUnit.SECONDS);

            log.info("[LoginMsgHandler]登录成功 userid = {} appid = {}",userIdByToken, appIdFromMsg);
            channelHandlerContext.writeAndFlush(respMsg);
            return;
        }
        //token校验失败,断开连接
        channelHandlerContext.close();
        log.error("[LoginMsgHandler]校验失败，token is error ");
        throw new IllegalArgumentException("token is error !");

    }
}
