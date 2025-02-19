package cn.junbao.yubao.live.im.core.server.service.impl;

import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextCache;
import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.service.ImRouterHandlerService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

/**
 * @Author: Junbao
 * @Date: 2025/2/18 17:57
 * @Description:
 */
@Service
public class ImRouterHandlerServiceImpl implements ImRouterHandlerService {

    /**
     * 将 router 中转过来的信息 进行发送到对应的客户端
     * @param imMsgBody
     */
    @Override
    public void onReceive(ImMsgBody imMsgBody) {
        //接收方的userid
        Long userId = imMsgBody.getUserId();
        ChannelHandlerContext channelHandlerContext = ChannelHandlerContextCache.get(userId);
        if (channelHandlerContext != null ){
            ImMsg imMsg = ImMsg.build(ImMsgTypeCode.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBody));
            //用户在线,才推送消息
            channelHandlerContext.writeAndFlush(imMsg);
        }
    }
}
