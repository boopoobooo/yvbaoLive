package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.IGiftService;
import cn.junbao.yubao.live.api.vo.req.GiftReqVO;
import cn.junbao.yubao.live.api.vo.resp.GiftConfigVO;
import cn.junbao.yubao.live.common.interfaces.dto.SendGiftMqMessageDTO;
import cn.junbao.yubao.live.common.interfaces.topic.GiftProviderTopicNames;
import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import cn.junbao.yubao.live.gift.dto.GiftConfigDTO;
import cn.junbao.yubao.live.gift.interfaces.IGiftConfigRpc;
import cn.junbao.yubao.live.mq.starter.publisher.EventPublisher;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 22:22
 * @Description:
 */
@Service
@Slf4j
public class GiftServiceImpl implements IGiftService {

    @DubboReference(check = false)
    private IGiftConfigRpc giftConfigRpc;

    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<GiftConfigVO> listGift() {
        List<GiftConfigDTO> giftConfigDTOList = giftConfigRpc.queryGiftList();
        return ConvertBeanUtils.convertList(giftConfigDTOList, GiftConfigVO.class);
    }

    @Override
    public boolean send(GiftReqVO giftReqVO) {

        int giftId = giftReqVO.getGiftId();
        GiftConfigDTO giftConfigDTO = giftConfigRpc.getByGiftId(giftId);
        if (giftConfigDTO == null ){
            log.warn("[GiftServiceImpl]礼物不存在,giftId:{}",giftId);
            return false;
        }
        /*if (giftReqVO.getReceiverId() == giftReqVO.getSenderUserId()){
            log.warn("[GiftServiceImpl]不能送礼给自己,giftId:{},senderUserId = {}",giftId,giftReqVO.getSenderUserId());
            return false;
        }*/

        // 进行异步消费
        SendGiftMqMessageDTO sendGiftMqMessageDTO = new SendGiftMqMessageDTO();
        sendGiftMqMessageDTO.setUserId(WebRequestContext.getUserId());//发送者userid
        sendGiftMqMessageDTO.setSenderNickName(giftReqVO.getSenderNickName());
        sendGiftMqMessageDTO.setGiftId(giftId);
        sendGiftMqMessageDTO.setGiftName(giftConfigDTO.getGiftName());
        sendGiftMqMessageDTO.setRoomId(giftReqVO.getRoomId());
        sendGiftMqMessageDTO.setReceiverId(giftReqVO.getReceiverId());
        sendGiftMqMessageDTO.setPrice(giftConfigDTO.getPrice());
        sendGiftMqMessageDTO.setUrl(giftConfigDTO.getSvgaUrl());
        sendGiftMqMessageDTO.setType(giftReqVO.getType());

        sendGiftMqMessageDTO.setUuid(UUID.randomUUID().toString());// 设置唯一标识UUID，防止mq重复消费

        //发送mq
        eventPublisher.publish(GiftProviderTopicNames.SEND_GIFT, JSON.toJSONString(sendGiftMqMessageDTO));

        return true;
    }
}
