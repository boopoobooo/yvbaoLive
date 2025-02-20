package cn.junbao.yubao.live.msg.provider.consumer.handler.impl;

import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.router.interfaces.ImRouterRpc;
import cn.junbao.yubao.live.msg.dto.MessageDTO;
import cn.junbao.yubao.live.msg.enums.ImMsgBizCodeEnum;
import cn.junbao.yubao.live.msg.provider.consumer.handler.IMessageHandler;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @Author: Junbao
 * @Date: 2025/2/19 09:55
 * @Description:
 */
@Component
@Slf4j
public class SingleMessageHandlerImpl implements IMessageHandler {


    @DubboReference
    private ImRouterRpc imRouterRpc;
    @Override
    public void onMesReceive(ImMsgBody imMsgBody) {
        String bizCode = imMsgBody.getBizCode();
        if (ImMsgBizCodeEnum.LIVING_ROOM_IM_BIZ.getCode().equals(bizCode)){
            MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
            //todo 完善
            ImMsgBody respBody = new ImMsgBody();
            respBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
            respBody.setUserId(messageDTO.getObjectId());//接收者为 body里面的 data里具体的user
            respBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_BIZ.getCode());
            JSONObject jsonObject =new JSONObject();
            jsonObject.put("userId",messageDTO.getUserId());
            jsonObject.put("context",messageDTO.getContent());
            respBody.setData(jsonObject.toString());

            imRouterRpc.sendMsg(respBody);
        }
    }

}
