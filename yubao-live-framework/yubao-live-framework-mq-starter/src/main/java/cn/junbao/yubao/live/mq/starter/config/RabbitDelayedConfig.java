package cn.junbao.yubao.live.mq.starter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Junbao
 * @Date: 2025/2/20 16:34
 * @Description:
 */
@Configuration
public class RabbitDelayedConfig {

    @Value("${spring.rabbitmq.topic.yubao_im_ack_msg_topic}")
    private String queueName;

    // 声明延迟交换机（必须为 x-delayed-message 类型）
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(
                "delayed_exchange",
                "x-delayed-message",
                true,
                false,
                args
        );
    }

    // 声明队列并绑定到延迟交换机
    @Bean
    public Queue delayedQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding delayedBinding() {
        return BindingBuilder.bind(delayedQueue())
                .to(delayedExchange())
                .with(queueName) // 路由键需与发送时的topic一致
                .noargs();
    }
}