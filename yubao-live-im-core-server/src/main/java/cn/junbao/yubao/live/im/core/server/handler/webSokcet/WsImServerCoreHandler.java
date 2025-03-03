package cn.junbao.yubao.live.im.core.server.handler.webSokcet;

import cn.junbao.yubao.live.im.core.server.common.ImContextUtil;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.factory.ImHandlerFactory;
import cn.junbao.yubao.live.im.core.server.handler.impl.LogoutMsgHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * im消息统一handler入口
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class WsImServerCoreHandler extends SimpleChannelInboundHandler {


    @Resource
    private ImHandlerFactory imHandlerFactory;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LogoutMsgHandler logoutMsgHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            wsMsgHandler(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 正常或者意外断线，都会触发到这里
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ImContextUtil.getUserId(ctx);
        Integer appId = ImContextUtil.getAppId(ctx);
        if (userId != null && appId != null) {
            logoutMsgHandler.logoutMsgHandler(ctx,userId,appId);
        }
    }

    private void wsMsgHandler(ChannelHandlerContext ctx, WebSocketFrame msg) {
        //如果不是文本消息，统一后台不会处理
        if (!(msg instanceof TextWebSocketFrame)) {
            log.error(String.format("[WebsocketCoreHandler]  wsMsgHandler , %s msg types not supported", msg.getClass().getName()));
            return;
        }
        try {
            // 返回应答消息
            String content = ((TextWebSocketFrame) msg).text();
            JSONObject jsonObject = JSON.parseObject(content, JSONObject.class);
            ImMsg imMsg = new ImMsg();
            imMsg.setMagic(jsonObject.getShort("magic"));
            imMsg.setMsgCode(jsonObject.getInteger("code"));
            imMsg.setLength(jsonObject.getInteger("len"));
            imMsg.setBody(jsonObject.getString("body").getBytes());
            imHandlerFactory.doMsgHandler(ctx, imMsg);
        } catch (Exception e) {
            log.error("[WebsocketCoreHandler] wsMsgHandler error is:", e);
        }

    }

}
