package cn.junbao.yubao.live.im.core.server.handler.webSokcet;

import cn.junbao.yubao.live.im.core.server.handler.impl.LoginMsgHandler;
import cn.junbao.yubao.live.im.interfaces.ImTokenRpc;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * WebSocket 握手处理器，用于处理客户端的 WebSocket 连接请求
 *
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class WsSharkHandler extends ChannelInboundHandlerAdapter {


    //指定监听的端口
    @Value("${yubao.im.ws.port}")
    private int port;
    @Value("${spring.cloud.nacos.discovery.ip}")
    private String serverIp;
    @DubboReference(check = false)
    private ImTokenRpc imTokenRpc;
    @Resource
    private LoginMsgHandler loginMsgHandler;

    private WebSocketServerHandshaker webSocketServerHandshaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //握手接入ws
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, ((FullHttpRequest) msg));
            return;
        }

        //正常关闭链路
        if (msg instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) ((WebSocketFrame) msg).retain());
            return;
        }
        //将消息传递给下一个链路处理器去处理
        ctx.fireChannelRead(msg);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        // ws://127.0.0.1:8809/{token}/{userId}/{code}/{param}
        // 基于code去做不同策略的参数解析
        String webSocketUrl = "ws://" + serverIp + ":" + port;
        // 构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketUrl, null, false);
        String uri = msg.uri();
        String[] paramArr = uri.split("/");
        String token = paramArr[1];
        Long userId = Long.valueOf(paramArr[2]);
        Long queryUserId = imTokenRpc.getUserIdByToken(token);
        //token的尾部就是appId
        Integer appId = Integer.valueOf(token.substring(token.lastIndexOf("%") + 1));
        if (queryUserId == null || !queryUserId.equals(userId)) {
            log.error("[WsSharkHandler] token 校验不通过！");
            //校验不通过，不允许建立连接
            ctx.close();
            return;
        }
        //建立ws的握手连接
        webSocketServerHandshaker = wsFactory.newHandshaker(msg);

        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }
        // 开始握手过程，建立 WebSocket 连接
        ChannelFuture channelFuture = webSocketServerHandshaker.handshake(ctx.channel(), msg);
        //首次握手建立ws连接后，返回一定的内容给到客户端
        if (channelFuture.isSuccess()) {
            Integer code = Integer.valueOf(paramArr[3]);
            Integer roomId = null;
            if (code == ParamCodeEnum.LIVING_ROOM_LOGIN.getCode()) {
                // 如果是直播间登录请求，从参数数组中获取房间 ID
                roomId = Integer.valueOf(paramArr[4]);
            }
            loginMsgHandler.loginSuccessHandler(ctx, userId, appId, roomId);
            log.info("[WebsocketSharkHandler] channel is connect!");
        }
    }

    enum ParamCodeEnum {
        LIVING_ROOM_LOGIN(1001, "直播间登录");

        int code;
        String desc;

        ParamCodeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
