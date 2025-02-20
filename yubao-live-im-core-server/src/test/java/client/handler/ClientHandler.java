package client.handler;

import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.service.IMsgAckCheckService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jakarta.annotation.Resource;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private IMsgAckCheckService msgAckCheckService;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ImMsg imMsg = (ImMsg)  msg  ;
        ImMsgBody msgBody = JSON.parseObject(new String(imMsg.getBody()), ImMsgBody.class);
        ImMsgBody respBody = new ImMsgBody();
        respBody.setMsgId(msgBody.getMsgId());
        respBody.setUserId(msgBody.getUserId());
        respBody.setAppId(msgBody.getAppId());
        ImMsg ackMsg = ImMsg.build(ImMsgTypeCode.ACK_IM_MSG.getCode(), JSON.toJSONString(respBody));
        ctx.writeAndFlush(ackMsg);
        System.out.println("===[服务端响应数据===imMsg = "+imMsg);
    }
}
