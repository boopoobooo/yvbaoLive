package cn.junbao.yubao.live.gift.provider.consumer;

import cn.junbao.yubao.live.bank.dto.AccountTradeReqDTO;
import cn.junbao.yubao.live.bank.dto.AccountTradeRespDTO;
import cn.junbao.yubao.live.bank.interfaces.ICurrencyAccountRpc;
import cn.junbao.yubao.live.common.interfaces.dto.SendGiftMqMessageDTO;
import cn.junbao.yubao.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import cn.junbao.yubao.live.gift.constants.SendGiftTypeEnum;
import cn.junbao.yubao.live.gift.dto.SendGiftRespMsgDTO;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.constants.ImMsgTypeCode;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.router.enums.ImMsgBizCodeEnum;
import cn.junbao.yubao.live.im.router.interfaces.ImRouterRpc;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.interfaces.rpc.ILivingRoomRpc;
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

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @DubboReference(check = false)
    private ILivingRoomRpc livingRoomRpc;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    @Value("${spring.rabbitmq.topic.yubao_send_gift_topic}")
    private String topic;


    private static final Long PK_INIT_NUM = 500L;
    private static final Long PK_MAX_NUM = 1000L;
    private static final Long PK_MIN_NUM = 00L;

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

        Integer sendGiftType = sendGiftMqMessageDTO.getType();
        Integer roomId = sendGiftMqMessageDTO.getRoomId();
        if (accountTradeRespDTO.isSuccess()) {
            //账户扣减成功,响应前端：渲染svga礼物特效

            LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
            livingRoomReqDTO.setRoomId(sendGiftMqMessageDTO.getRoomId());
            livingRoomReqDTO.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
            //查询到当前直播间内的 用户
            List<Long> userIdList  = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);

            if (sendGiftType.equals(SendGiftTypeEnum.DEFAULT_SEND_GIFT.getCode())) {
                // 默认送礼，发送给全直播用户礼物特效
                SendGiftRespMsgDTO sendGiftRespMsgDTO = new SendGiftRespMsgDTO();
                sendGiftRespMsgDTO.setSvgaUrl(sendGiftMqMessageDTO.getUrl());
                sendGiftRespMsgDTO.setSenderNickName(sendGiftMqMessageDTO.getSenderNickName());
                sendGiftRespMsgDTO.setGiftName(sendGiftMqMessageDTO.getGiftName());


                // 利用封装方法发送单播消息，bizCode为success类型
                this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS.getCode(), JSON.toJSONString(sendGiftRespMsgDTO));
                log.info("[SendGiftConsumer] send success, msgDTO is {}", sendGiftMqMessageDTO);
            } else if (sendGiftType.equals(SendGiftTypeEnum.PK_SEND_GIFT.getCode())) {
                // PK送礼，要求全体可见
                // 1 礼物特效url全直播间可见
                jsonObject.put("url", sendGiftMqMessageDTO.getUrl());
                // 2 TODO PK进度条全直播间可见
                String pkNumKey = cacheKeyBuilder.buildLivingPkKey(roomId);
                String incrKey = cacheKeyBuilder.buildLivingPkSendSeq(roomId);

                // 获取 pkUserId 和 pkObjId
                Long pkObjId = livingRoomRpc.queryOnlinePkUserId(roomId);
                LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
                if (pkObjId == null || livingRoomRespDTO == null || livingRoomRespDTO.getAnchorId() == null) {
                    log.error("[sendGiftConsumer] 两个用户已经有不在线的，roomId is {}", roomId);
                    return;
                }
                Long pkUserId = livingRoomRespDTO.getAnchorId();
                Long resultNum = null;
                Long pkNum = 0L;

                // 获取该条消息的序列号，避免消息乱序
                Long sendGiftSeqNum = redisTemplate.opsForValue().increment(incrKey);
                if (sendGiftMqMessageDTO.getReceiverId().equals(pkUserId)) {
                    // 收礼人是房主userId，则进度条增加   (进度直接取自礼物的金额)
                    resultNum = redisTemplate.opsForValue().increment(pkNumKey, sendGiftMqMessageDTO.getPrice());
                    if (PK_MAX_NUM <= resultNum) {
                        jsonObject.put("winnerId", pkUserId);
                        // 返回给前端的pkNum最大为MAX_NUM
                        pkNum = PK_MAX_NUM;
                    } else {
                        pkNum = resultNum;
                    }
                } else if (sendGiftMqMessageDTO.getReceiverId().equals(pkObjId)) {
                    // 收礼人是来挑战的，则进图条减少
                    resultNum = redisTemplate.opsForValue().decrement(pkNumKey, sendGiftMqMessageDTO.getPrice());
                    if (PK_MIN_NUM >= resultNum) {
                        jsonObject.put("winnerId", pkObjId);
                        // 返回给前端的pkNum最小为MIN_NUM
                        pkNum = PK_MIN_NUM;
                    } else {
                        pkNum = resultNum;
                    }
                }
                jsonObject.put("sendGiftSeqNum", sendGiftSeqNum);
                jsonObject.put("pkNum", pkNum);
                // 3 搜索要发送的用户
                // 利用封装方法发送批量消息，bizCode为PK_SEND_SUCCESS
                this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_PK_SEND_GIFT_SUCCESS.getCode(), jsonObject);
            }

        }else {
            //扣减失败，利用IM将发送失败的消息告知用户
            imMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode());
            imMsgBody.setUserId(sendGiftMqMessageDTO.getUserId());//发送给礼物的消费方
            jsonObject.put("msg",accountTradeRespDTO.getMsg());
            log.warn("[SendGiftConsumer] send failed失败! msgDTO is {}", sendGiftMqMessageDTO);
        }
    }
    /**
     * 单向通知送礼对象
     */
    private void sendImMsgSingleton(Long userId, String bizCode, JSONObject jsonObject) {
        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
        imMsgBody.setBizCode(bizCode);
        imMsgBody.setUserId(userId);
        imMsgBody.setData(jsonObject.toJSONString());
        imRouterRpc.sendMsg(imMsgBody);
    }

    /**
     * 批量发送im消息
     */
    private void batchSendImMsg(List<Long> userIdList, String bizCode, String data) {
        List<ImMsgBody> imMsgBodies = userIdList.stream().map(userId -> {
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setData(data);
            imMsgBody.setUserId(userId);
            return imMsgBody;
        }).collect(Collectors.toList());
        imRouterRpc.batchSendMsg(imMsgBodies);
    }

    /**
     * 批量发送im消息
     */
    private void batchSendImMsg(List<Long> userIdList, String bizCode, JSONObject jsonObject) {
        List<ImMsgBody> imMsgBodies = userIdList.stream().map(userId -> {
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setData(jsonObject.toJSONString());
            imMsgBody.setUserId(userId);
            return imMsgBody;
        }).collect(Collectors.toList());
        imRouterRpc.batchSendMsg(imMsgBodies);
    }

}
