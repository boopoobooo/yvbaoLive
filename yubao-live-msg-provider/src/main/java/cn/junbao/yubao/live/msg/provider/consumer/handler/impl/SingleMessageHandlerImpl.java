package cn.junbao.yubao.live.msg.provider.consumer.handler.impl;

import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.router.interfaces.ImRouterRpc;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.interfaces.rpc.ILivingRoomRpc;
import cn.junbao.yubao.live.msg.dto.MessageDTO;
import cn.junbao.yubao.live.msg.enums.ImMsgBizCodeEnum;
import cn.junbao.yubao.live.msg.provider.consumer.handler.IMessageHandler;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/2/19 09:55
 * @Description:
 */
@Component
@Slf4j
public class SingleMessageHandlerImpl implements IMessageHandler {


    @DubboReference(check = false)
    private ImRouterRpc imRouterRpc;

    @DubboReference(check = false)
    private ILivingRoomRpc livingRoomRpc;
    @Override
    public void onMesReceive(ImMsgBody imMsgBody) {
        String bizCode = imMsgBody.getBizCode();
        if (ImMsgBizCodeEnum.LIVING_ROOM_IM_BIZ.getCode().equals(bizCode)){
            MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
            Integer roomId = messageDTO.getRoomId();
            LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
            livingRoomReqDTO.setRoomId(roomId);
            livingRoomReqDTO.setAppId(imMsgBody.getAppId());
            List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
            if (CollectionUtils.isEmpty(userIdList)){
                log.info("[onMesReceive] 当前房间没有用户，不能发送消息");
                return;
            }
            List<ImMsgBody> imMsgBodyList = new ArrayList<>();
            for (Long userId : userIdList) {

                ImMsgBody respBody = new ImMsgBody();
                respBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
                respBody.setUserId(userId);//接收者为 body里面的 data里具体的user
                respBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_BIZ.getCode());
                respBody.setData(JSON.toJSONString(messageDTO));
                imMsgBodyList.add(respBody);
            }
            //发送直播间消息
            imRouterRpc.batchSendMsg(imMsgBodyList);
        }
    }

}
