package cn.junbao.yubao.live.msg.provider.consumer;

import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.msg.provider.consumer.handler.IMessageHandler;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
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
public class ImMsgConsumer{

    @Value("${spring.rabbitmq.topic.yubao_im_biz_msg_topic}")
    private String topic;

    @Resource
    private IMessageHandler iMessageHandler;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.yubao_im_biz_msg_topic}"))
    public void listener(String message) {
        log.info("监听消息 topic: {} message: {}", topic, message);
        // 1. 转换消息

        ImMsgBody imMsgBody = JSON.parseObject(message, ImMsgBody.class);
        iMessageHandler.onMesReceive(imMsgBody);
    }

}
