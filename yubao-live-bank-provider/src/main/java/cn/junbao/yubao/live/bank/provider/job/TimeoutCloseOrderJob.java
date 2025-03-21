package cn.junbao.yubao.live.bank.provider.job;

import cn.junbao.yubao.live.bank.constants.OrderStatusEnum;
import cn.junbao.yubao.live.bank.provider.service.IPayOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: Junbao
 * @Description: 超时关单
 */
@Slf4j
@Component()
public class TimeoutCloseOrderJob {

    @Resource
    private IPayOrderService payOrderService;
    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        try {
            log.info("任务；超时30分钟订单关闭");
            List<Long> orderIds = payOrderService.queryTimeoutCloseOrderList();
            if (null == orderIds || orderIds.isEmpty()) {
                return;
            }
            for (Long orderId : orderIds) {
                boolean status = payOrderService.updateOrderStatus(orderId, OrderStatusEnum.IN_VALID.getCode());
                log.info("定时任务，超时30分钟订单关闭 orderId: {} status：{}", orderId, status);
            }
        } catch (Exception e) {
            log.error("定时任务，超时15分钟订单关闭失败", e);
        }
    }

}
