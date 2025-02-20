package cn.junbao.yubao.live.mq.starter.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description 消息发送
 */
@Slf4j
@Component
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 预定义延迟交换机名称（需与RabbitMQ配置一致）
    private static final String DELAYED_EXCHANGE = "delayed_exchange";

    public void publish(String topic, String messageJSON){
        try {
            rabbitTemplate.convertAndSend(topic, messageJSON);
            log.info("发送MQ消息 topic:{} message:{}", topic, messageJSON);
        } catch (Exception e) {
            log.error("发送MQ消息失败 topic:{} message:{}", topic, messageJSON, e);
            throw e;
        }
    }

    /**
     * 发送延迟消息（自动关联预定义延迟交换机）
     * @param topic 路由键（对应队列绑定的路由规则）
     * @param messageJSON 消息内容（JSON字符串）
     * @param delayMillis 延迟时间（单位：毫秒，最大值不超过24.8天）
     */
    public void publishDelayedMessage(String topic, String messageJSON, int delayMillis) {
        try {
            // 参数校验
            if (delayMillis < 0) {
                throw new IllegalArgumentException("延迟时间不能为负数");
            }

            rabbitTemplate.convertAndSend(DELAYED_EXCHANGE, topic, messageJSON, message -> {
                message.getMessageProperties().setHeader("x-delay", delayMillis);
                return message;
            });

            log.info("发送延迟消息成功 | exchange:{} | topic:{} | delay:{}ms | message:{}",
                    DELAYED_EXCHANGE, topic, delayMillis, messageJSON);
        } catch (Exception e) {
            log.error("发送延迟消息失败 | exchange:{} | topic:{} | delay:{}ms | message:{} | 错误详情:",
                    DELAYED_EXCHANGE, topic, delayMillis, messageJSON, e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }


}

