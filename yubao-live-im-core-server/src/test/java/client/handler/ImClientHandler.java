package client.handler;

import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.common.ImMsgDecoder;
import cn.junbao.yubao.live.im.core.server.common.ImMsgEncoder;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.interfaces.ImTokenRpc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ImClientHandler implements InitializingBean {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        log.info("==initChannel==客户端初始化连接");
                        socketChannel.pipeline().addLast(new ImMsgDecoder());
                        socketChannel.pipeline().addLast(new ImMsgEncoder());
                        socketChannel.pipeline().addLast(new ClientHandler());

                    }
                });

                Map<Long,Channel> userChannelMap = new HashMap<>();
                for (int i = 1; i <= 1; i++) {
                    Long userId = 10000L+i;
                    String token = null;
                    try {
                        token = imTokenRpc.createImLoginToken(userId, AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
                    } catch (Exception e) {
                        log.error("[ERROR] e = "+e);
                        throw new RuntimeException(e);
                    }
                    log.info("rpc  token = {}",token);
                    ImMsgBody imMsgBody = new ImMsgBody();
                    imMsgBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
                    imMsgBody.setUserId(userId);
                    imMsgBody.setToken(token);
                    imMsgBody.setData("login");

                    ChannelFuture channelFuture = null;
                    try {
                        channelFuture = bootstrap.connect("localhost", 9090).sync();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Channel channel = channelFuture.channel();
                    userChannelMap.put(userId,channel);

                    channel.writeAndFlush(ImMsg.build(ImMsgTypeCode.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBody)));

                }

                while (true){
                    for (Long userIdInMap : userChannelMap.keySet()) {
                        ImMsgBody reqBody = new ImMsgBody();
                        reqBody.setUserId(userIdInMap);
                        reqBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userid",userIdInMap);
                        jsonObject.put("context","你好你好！！！！"+userIdInMap);
                        reqBody.setData(String.valueOf(jsonObject));
                        ImMsg imMsg = ImMsg.build(ImMsgTypeCode.IM_BIZ_MSG.getCode(), JSON.toJSONString(reqBody));
                        userChannelMap.get(userIdInMap).writeAndFlush(imMsg);
                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }//while
            }
        });
        thread.start();
    }//

    private void sendHeartBeat(Long userId, Channel channel) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ImMsgBody imMsgBody = new ImMsgBody();
                imMsgBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
                imMsgBody.setUserId(userId);
                ImMsg loginMsg = ImMsg.build(ImMsgTypeCode.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(imMsgBody));
                channel.writeAndFlush(loginMsg);
            }
        }).start();
    }
}
