package cn.junbao.yubao.live.gift.provider.consumer;

import cn.junbao.yubao.live.bank.dto.AccountTradeReqDTO;
import cn.junbao.yubao.live.bank.dto.AccountTradeRespDTO;
import cn.junbao.yubao.live.bank.interfaces.ICurrencyAccountRpc;
import cn.junbao.yubao.live.common.interfaces.dto.SendGiftMqMessageDTO;
import cn.junbao.yubao.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.router.enums.ImMsgBizCodeEnum;
import cn.junbao.yubao.live.im.router.interfaces.ImRouterRpc;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 23:11
 * @Description:   消费 用户送礼行为的mq消息， 进行对应的业务处理 （ 账户扣减 和 结果响应 ）
 */
@Component
@Slf4j
public class SendGiftConsumer {


    @DubboReference(check = false)
    private ICurrencyAccountRpc currencyAccountRpc;
    @DubboReference(check = false)
    private ImRouterRpc imRouterRpc;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    @Value("${spring.rabbitmq.topic.yubao_send_gift_topic}")
    private String topic;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.yubao_send_gift_topic}"))
    public void listener(String message) {
        log.info("[SendGiftConsumer]监听消息 topic: {} message: {}", topic, message);
        // 1. 转换消息
        SendGiftMqMessageDTO sendGiftMqMessageDTO = JSON.parseObject(message, SendGiftMqMessageDTO.class);

        //2. mq消费仿重校验
        String mqConsumeCacheKey = cacheKeyBuilder.buildeGiftMqConsumeKey(sendGiftMqMessageDTO.getUuid());
        Boolean consumeLockStatus = redisTemplate.opsForValue().setIfAbsent(mqConsumeCacheKey, "1", 5, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(consumeLockStatus)){
            //加锁失败，表明之前消费过，直接返回
            return;
        }

        //3. 调用账户扣减服务
        AccountTradeReqDTO accountTradeReqDTO = new AccountTradeReqDTO();
        accountTradeReqDTO.setUserId(sendGiftMqMessageDTO.getUserId());
        accountTradeReqDTO.setNum(sendGiftMqMessageDTO.getPrice());
        AccountTradeRespDTO accountTradeRespDTO = currencyAccountRpc.consumeForSendGift(accountTradeReqDTO);

        //4.处理响应信息
        // 如果余额扣减成功
        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
        JSONObject jsonObject = new JSONObject();
        if (accountTradeRespDTO.isSuccess()) {
            //账户扣减成功,响应前端：渲染svga礼物特效
            imMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS.getCode());
            imMsgBody.setUserId(sendGiftMqMessageDTO.getReceiverId());//发送给接收者 todo 后续可以拓展为全直播间人员
            jsonObject.put("url",sendGiftMqMessageDTO.getUrl());
            log.info("[SendGiftConsumer] send success, msgDTO is {}", sendGiftMqMessageDTO);
        }else {
            //扣减失败，利用IM将发送失败的消息告知用户
            imMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode());
            imMsgBody.setUserId(sendGiftMqMessageDTO.getUserId());//发送给礼物的消费方
            jsonObject.put("msg",accountTradeRespDTO.getMsg());
            log.warn("[SendGiftConsumer] send failed失败! msgDTO is {}", sendGiftMqMessageDTO);
        }

        imMsgBody.setData(jsonObject.toJSONString());
        imRouterRpc.sendMsg(imMsgBody);
    }

}
