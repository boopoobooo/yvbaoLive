package cn.junbao.yubao.live.im.core.server.handler.impl;

import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextHashMap;
import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.interfaces.ImTokenRpc;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginMsgHandler implements ISimpleHandler {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Override
    public void handler(ChannelHandlerContext channelHandlerContext, ImMsg msg) {

        byte[] msgBody = msg.getBody();
        if (msgBody == null || msgBody.length == 0 ){
            log.error("[LoginMsgHandler]参数异常，body is null ");
            throw new IllegalArgumentException("body is null !");
        }
        ImMsgBody imMsgBody = JSON.parseObject(new String(msgBody), ImMsgBody.class);
        Long userIdFromMsg = imMsgBody.getUserId();
        int appIdFromMsg = imMsgBody.getAppId();
        String token = imMsgBody.getToken();
        if (StringUtils.isBlank(token) || (userIdFromMsg == null) || appIdFromMsg < 1000){
            log.error("[LoginMsgHandler]参数异常，当前参数： appId = {}, userId from msg = {}, token = {} ",appIdFromMsg,userIdFromMsg,token);
            throw new IllegalArgumentException("param is error !");
        }
        Long userIdByToken = imTokenRpc.getUserIdByToken(imMsgBody.getToken());
        log.info("[LoginMsgHandler]  userIdByToken = {}, useridofMsg = {}",userIdByToken,imMsgBody.getUserId());
        if (userIdByToken != null && userIdByToken.equals(imMsgBody.getUserId())){
            //token校验成功，允许建立连接
            //将channelHandlerContext加入缓存
            ChannelHandlerContextHashMap.put(userIdByToken,channelHandlerContext);
            ImContextUtil.setUserId(channelHandlerContext,userIdByToken);

            ImMsgBody respBody = new ImMsgBody();
            respBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
            respBody.setUserId(userIdByToken);
            respBody.setData("true");
            ImMsg respMsg = ImMsg.build(ImMsgTypeCode.IM_LOGIN_MSG.getCode(),JSON.toJSONString(respBody));
            log.info("[LoginMsgHandler]登录成功 userid = {} appid = {}",userIdByToken, appIdFromMsg);
            channelHandlerContext.writeAndFlush(respMsg);
        }
        //token校验失败,断开连接
        channelHandlerContext.close();
        log.error("[LoginMsgHandler]校验失败，token is error ");
        throw new IllegalArgumentException("token is error !");

    }
}
