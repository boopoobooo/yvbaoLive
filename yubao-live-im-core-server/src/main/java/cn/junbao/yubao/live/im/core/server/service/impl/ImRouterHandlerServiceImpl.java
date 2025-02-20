package cn.junbao.yubao.live.im.core.server.service.impl;

import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextCache;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.service.IMsgAckCheckService;
import cn.junbao.yubao.live.im.core.server.service.ImRouterHandlerService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author: Junbao
 * @Date: 2025/2/18 17:57
 * @Description:
 */
@Service
@Slf4j
public class ImRouterHandlerServiceImpl implements ImRouterHandlerService {

    @Resource
    private IMsgAckCheckService msgAckCheckService;

    /**
     * 将 router 中转过来的信息 进行回推到对应的客户端
     * @param imMsgBody
     */
    @Override
    public void onReceive(ImMsgBody imMsgBody) {
        if (this.sendMsgToClient(imMsgBody)){
            //发送成功，则记录ACK确认
            msgAckCheckService.recordMsgAck(imMsgBody,1);
            msgAckCheckService.sendDelayMsg(imMsgBody);
        }
    }

    public boolean sendMsgToClient (ImMsgBody imMsgBody ){
        Long userId = imMsgBody.getUserId();
        ChannelHandlerContext channelHandlerContext = ChannelHandlerContextCache.get(userId);
        if (channelHandlerContext != null ){
            imMsgBody.setMsgId(UUID.randomUUID().toString());
            ImMsg respMsg = ImMsg.build(ImMsgTypeCode.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBody));
            //用户在线,才推送消息
            log.info("[sendMsgToClient] writeAndFlush, userid = {},data = {}",imMsgBody.getUserId(),imMsgBody.getData());
            channelHandlerContext.writeAndFlush(respMsg);
            return true;
        }
        return false;
    }
}
