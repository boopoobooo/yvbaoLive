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
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

                ChannelFuture channelFuture = null;
                try {
                    channelFuture = bootstrap.connect("localhost", 9090).sync();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Channel channel = channelFuture.channel();

                Map<Long,Channel> userChannelMap = new HashMap<>();
                String token = null;

                Scanner scanner = new Scanner(System.in);
                System.out.println("请输入userId");
                Long userId = scanner.nextLong();
                System.out.println("输入senderName");
                Long objectId = scanner.nextLong();

                try {
                    token = imTokenRpc.createImLoginToken(userId, AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
                } catch (Exception e) {
                    log.error("[ERROR] e = "+e);
                    throw new RuntimeException(e);
                }
                log.info("rpc  token = {}",token);

                //登录消息包
                ImMsgBody imMsgBody = new ImMsgBody();
                imMsgBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
                imMsgBody.setUserId(userId);
                imMsgBody.setToken(token);
                userChannelMap.put(userId,channel);
                channel.writeAndFlush(ImMsg.build(ImMsgTypeCode.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBody)));

                //异步发送心跳包
                sendHeartBeat(userId,channel);


                while (true) {
                    System.out.println("请输入聊天内容");
                    String content = scanner.nextLine();
                    if (StringUtils.isEmpty(content)) {
                        continue;
                    }
                    ImMsgBody bizBody = new ImMsgBody();
                    bizBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
                    bizBody.setUserId(userId);
                    bizBody.setBizCode("501");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", userId);
                    jsonObject.put("objectId", objectId);
                    jsonObject.put("content", content);
                    bizBody.setData(JSON.toJSONString(jsonObject));
                    ImMsg heartBeatMsg = ImMsg.build(ImMsgTypeCode.IM_BIZ_MSG.getCode(), JSON.toJSONString(bizBody));
                    channel.writeAndFlush(heartBeatMsg);
                }
            }
        });
        thread.start();
    }//

    private void sendHeartBeat(Long userId, Channel channel) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
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
