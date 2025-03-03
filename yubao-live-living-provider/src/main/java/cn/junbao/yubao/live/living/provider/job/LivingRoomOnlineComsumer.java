package cn.junbao.yubao.live.living.provider.job;

import cn.junbao.yubao.im.core.server.dto.ImOnlineDTO;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
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
 * @Description:  直播间中， 用户加入到直播间，将用户信息加入到直播间的用户列表中
 */
@Slf4j
@Component
public class LivingRoomOnlineComsumer {
    @Value("${spring.rabbitmq.topic.yubao_living_room_online_topic}")
    private String topic;
    @Resource
    private ILivingRoomService livingRoomService;
    /**
     * 处理用户加入直播间的 上线消息
     * @param message
     */
    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.yubao_living_room_online_topic}"))
    public void listener(String message) {
        log.info("[LivingRoomOnlineComsumer]监听消息 topic: {} message: {}", topic, message);
        // 1. 转换消息
        ImOnlineDTO imOnlineDTO = JSON.parseObject(message, ImOnlineDTO.class);
        livingRoomService.userOnlineHandler(imOnlineDTO);
    }
}
