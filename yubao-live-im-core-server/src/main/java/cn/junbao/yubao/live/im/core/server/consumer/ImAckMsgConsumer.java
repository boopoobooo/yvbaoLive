package cn.junbao.yubao.live.im.core.server.consumer;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.live.im.core.server.service.IMsgAckCheckService;
import cn.junbao.yubao.live.im.core.server.service.ImRouterHandlerService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: Junbao
 * @Date: 2025/2/17 22:19
 * @Description: Im系统 消息队列消费者
 */
@Slf4j
@Component
public class ImAckMsgConsumer {

    @Value("${spring.rabbitmq.topic.yubao_im_ack_msg_topic}")
    private String topic;

    // 延迟交换机名称（需与生产者一致）
    private static final String DELAYED_EXCHANGE = "delayed_exchange";
    @Resource
    private IMsgAckCheckService msgAckCheckService;
    @Resource
    private ImRouterHandlerService imRouterHandlerService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.topic.yubao_im_ack_msg_topic}", durable = "true"),
                    exchange = @Exchange(
                            name = "delayed_exchange",
                            type = "x-delayed-message", // 指定交换机类型
                            delayed = "true"
                    ),
                    key = "${spring.rabbitmq.topic.yubao_im_ack_msg_topic}"
            )
    )
    public void listener(String message) {
        log.info("[ImAckMsgConsumer]监听消息 topic: {} message: {}", topic, message);
        // 1. 转换消息
        ImMsgBody imMsgBody = JSON.parseObject(message, ImMsgBody.class);
        int ackRetryTimes = msgAckCheckService.getMsgAckTimes(imMsgBody.getMsgId(), imMsgBody.getUserId(), imMsgBody.getAppId());
        log.info("[ImAckMsgConsumer] ackRetryTimes = {}",ackRetryTimes);
        if (ackRetryTimes < 0 ){
            //记录已经收到
            return;
        }
        if (ackRetryTimes < ImCoreServerConstants.ACK_MAX_RETRY_TIMES ){
            //继续重发消息
            msgAckCheckService.recordMsgAck(imMsgBody,ackRetryTimes + 1 );
            msgAckCheckService.sendDelayMsg(imMsgBody);
            imRouterHandlerService.sendMsgToClient(imMsgBody);
            log.warn("[ImAckMsgConsumer] 消息发送失败，正在进行消息发送重试，userId = {},msgId = {}",imMsgBody.getUserId(),imMsgBody.getMsgId());
        }else {
            //超过最大重试次数,移除ack记录
            msgAckCheckService.doMsgAck(imMsgBody);
            log.warn("[ImAckMsgConsumer] ackRetryTimes超过最大重试次数，停止消息发送重试，userId = {},msgId = {},data = {}",imMsgBody.getUserId(),imMsgBody.getMsgId(),imMsgBody.getData());
        }
    }


}
