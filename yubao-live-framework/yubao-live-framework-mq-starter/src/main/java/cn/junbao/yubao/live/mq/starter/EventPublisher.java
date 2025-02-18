package cn.junbao.yubao.live.mq.starter;

import lombok.extern.slf4j.Slf4j;
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

    public void publish(String topic, String messageJSON){
        try {
            rabbitTemplate.convertAndSend(topic, messageJSON);
            log.info("发送MQ消息 topic:{} message:{}", topic, messageJSON);
        } catch (Exception e) {
            log.error("发送MQ消息失败 topic:{} message:{}", topic, messageJSON, e);
            throw e;
        }
    }

}

