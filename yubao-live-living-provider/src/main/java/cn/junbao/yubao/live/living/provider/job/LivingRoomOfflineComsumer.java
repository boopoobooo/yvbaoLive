package cn.junbao.yubao.live.living.provider.job;

import cn.junbao.yubao.im.core.server.dto.ImOfflineDTO;
import cn.junbao.yubao.im.core.server.dto.ImOnlineDTO;
import cn.junbao.yubao.live.living.provider.service.ILivingRoomService;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: Junbao
 * @Date: 2025/2/28 15:38
 * @Description:  用户在直播间中下线的处理
 */
@Slf4j
@Component
public class LivingRoomOfflineComsumer {
    @Value("${spring.rabbitmq.topic.yubao_living_room_offline_topic}")
    private String topic;
    @Resource
    private ILivingRoomService livingRoomService;
    /**
     * 处理用户加入直播间的 上线消息
     * @param message
     */
    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.yubao_living_room_offline_topic}"))
    public void listener(String message) {
        log.info("[LivingRoomOfflineComsumer]监听消息 topic: {} message: {}", topic, message);
        // 1. 转换消息
        ImOfflineDTO imOfflineDTO = JSON.parseObject(message, ImOfflineDTO.class);
        livingRoomService.userOfflineHandler(imOfflineDTO);
    }
}
