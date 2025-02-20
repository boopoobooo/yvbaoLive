package cn.junbao.yubao.live.im.core.server.handler.factory.impl;

import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import cn.junbao.yubao.live.im.core.server.handler.factory.ImHandlerFactory;
import cn.junbao.yubao.live.im.core.server.handler.impl.*;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ImHandlerFactoryImpl implements ImHandlerFactory, InitializingBean {

    private static final Map<Integer, ISimpleHandler> imHandlerGroup = new HashMap<>();
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void doMsgHandler(ChannelHandlerContext channelHandlerContext, Object msg) {
        ImMsg imMsg = (ImMsg) msg;
        ISimpleHandler iSimpleHandler = imHandlerGroup.get(imMsg.getMsgCode());
        if (iSimpleHandler == null){
            log.error("[doMsgHandler] code error , code is {}",imMsg.getMsgCode());
            return;
        }
        iSimpleHandler.handler(channelHandlerContext,imMsg);
    }

    /**
     * 初始化imHandlerGroup
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        imHandlerGroup.put(ImMsgTypeCode.IM_LOGIN_MSG.getCode(), applicationContext.getBean(LoginMsgHandler.class));
        imHandlerGroup.put(ImMsgTypeCode.IM_LOGOUT_MSG.getCode(), applicationContext.getBean(LogoutMsgHandler.class));
        imHandlerGroup.put(ImMsgTypeCode.IM_BIZ_MSG.getCode(), applicationContext.getBean(DefaultBizMsgHandler.class));
        imHandlerGroup.put(ImMsgTypeCode.IM_HEARTBEAT_MSG.getCode(), applicationContext.getBean(HeartBeatMsgHandler.class));
        imHandlerGroup.put(ImMsgTypeCode.ACK_IM_MSG.getCode(), applicationContext.getBean(AckMsgHandler.class));
    }
}
