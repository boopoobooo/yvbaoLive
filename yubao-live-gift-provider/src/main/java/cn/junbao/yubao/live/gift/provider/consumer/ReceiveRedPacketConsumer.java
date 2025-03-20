package cn.junbao.yubao.live.gift.provider.consumer;

import cn.junbao.yubao.live.bank.dto.AccountTradeReqDTO;
import cn.junbao.yubao.live.bank.dto.AccountTradeRespDTO;
import cn.junbao.yubao.live.bank.interfaces.ICurrencyAccountRpc;
import cn.junbao.yubao.live.common.interfaces.dto.SendGiftMqMessageDTO;
import cn.junbao.yubao.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import cn.junbao.yubao.live.gift.bo.SendRedPacketBO;
import cn.junbao.yubao.live.gift.constants.SendGiftTypeEnum;
import cn.junbao.yubao.live.gift.dto.SendGiftRespMsgDTO;
import cn.junbao.yubao.live.gift.provider.service.IRedPacketConfigService;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
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
 * @Description:   抢红包后的 消息处理  账户修改
 */
@Component
@Slf4j
public class ReceiveRedPacketConsumer {


    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private IRedPacketConfigService redPacketConfigService;


    @Value("${spring.rabbitmq.topic.receive-red-packet}")
    private String topic;



    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.receive-red-packet}"))
    public void listener(String message) {
        log.info("[ReceiveRedPacketConsumer]监听消息 topic: {} message: {}", topic, message);
        // 1. 转换消息
        SendRedPacketBO sendRedPacketBO = JSON.parseObject(message,SendRedPacketBO.class);
        redPacketConfigService.receiveRedPacketHandler(sendRedPacketBO.getReqDTO(), sendRedPacketBO.getPrice());
    }

}
