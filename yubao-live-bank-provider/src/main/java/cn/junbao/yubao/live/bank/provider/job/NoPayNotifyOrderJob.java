package cn.junbao.yubao.live.bank.provider.job;

import cn.junbao.yubao.live.bank.constants.OrderStatusEnum;
import cn.junbao.yubao.live.bank.provider.service.IPayOrderService;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: Junbao
 * @Description:  检测未接收到或未正确处理的支付回调通知
 */
@Slf4j
@Component()
public class NoPayNotifyOrderJob {
    @Resource
    private IPayOrderService payOrderService;
    @Resource
    private AlipayClient alipayClient;
    @Scheduled(cron = "0/3 * * * * ?")
    public void exec() {
        try {
            log.info("任务；检测未接收到或未正确处理的支付回调通知");
            List<Long> orderIds = payOrderService.queryNoPayNotifyOrder();
            if (null == orderIds || orderIds.isEmpty()) return;

            for (Long orderId : orderIds) {
                AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
                bizModel.setOutTradeNo(orderId.toString());
                request.setBizModel(bizModel);

                AlipayTradeQueryResponse alipayTradeQueryResponse = alipayClient.execute(request);
                String code = alipayTradeQueryResponse.getCode();
                // 判断状态码
                if ("10000".equals(code)) {
                    payOrderService.updateOrderStatus(orderId, OrderStatusEnum.PAYED.getCode());
                }
            }
        } catch (Exception e) {
            log.error("检测未接收到或未正确处理的支付回调通知失败", e);
        }
    }

}
