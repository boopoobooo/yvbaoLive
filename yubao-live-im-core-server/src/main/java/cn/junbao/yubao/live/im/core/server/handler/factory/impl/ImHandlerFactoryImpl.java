package cn.junbao.yubao.live.im.core.server.handler.factory.impl;

import cn.junbao.yubao.live.im.common.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.ISimpleHandler;
import cn.junbao.yubao.live.im.core.server.handler.factory.ImHandlerFactory;
import cn.junbao.yubao.live.im.core.server.handler.impl.DefaultBizMsgHandler;
import cn.junbao.yubao.live.im.core.server.handler.impl.HeartBeatMsgHandler;
import cn.junbao.yubao.live.im.core.server.handler.impl.LoginMsgHandler;
import cn.junbao.yubao.live.im.core.server.handler.impl.LogoutMsgHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ImHandlerFactoryImpl implements ImHandlerFactory {

    private static final Map<Integer, ISimpleHandler> imHandlerGroup = new HashMap<>();

    static {
        imHandlerGroup.put(ImMsgTypeCode.IM_LOGIN_MSG.getCode(), new LoginMsgHandler());
        imHandlerGroup.put(ImMsgTypeCode.IM_LOGOUT_MSG.getCode(),  new LogoutMsgHandler());
        imHandlerGroup.put(ImMsgTypeCode.IM_BIZ_MSG.getCode(), new DefaultBizMsgHandler());
        imHandlerGroup.put(ImMsgTypeCode.IM_HEARTBEAT_MSG.getCode(), new HeartBeatMsgHandler());
    }

    @Override
    public void doMsgHandler(ChannelHandlerContext channelHandlerContext, Object msg) {
        ImMsg imMsg = (ImMsg) msg;
        ISimpleHandler iSimpleHandler = imHandlerGroup.get(imMsg.getCode());
        if (iSimpleHandler == null){
            log.error("[doMsgHandler] code error , code is {}",imMsg.getCode());
            return;
        }
        iSimpleHandler.handler(channelHandlerContext,imMsg);
    }
}
