package cn.junbao.yubao.live.bank.provider.service.impl;

import cn.junbao.yubao.live.bank.constants.OrderStatusEnum;
import cn.junbao.yubao.live.bank.constants.PayProductTypeEnum;
import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.dto.PayProductDTO;
import cn.junbao.yubao.live.bank.provider.dao.mapper.IPayOrderMapper;
import cn.junbao.yubao.live.bank.provider.dao.po.PayOrderPO;
import cn.junbao.yubao.live.bank.provider.dao.po.PayTopicPO;
import cn.junbao.yubao.live.bank.provider.service.ICurrencyAccountService;
import cn.junbao.yubao.live.bank.provider.service.IPayOrderService;
import cn.junbao.yubao.live.bank.provider.service.IPayProductService;
import cn.junbao.yubao.live.bank.provider.service.IPayTopicService;
import cn.junbao.yubao.live.mq.starter.publisher.EventPublisher;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 09:43
 * @Description:
 */
@Service
@Slf4j
public class PayOrderServiceImpl implements IPayOrderService {
    @Resource
    private IPayOrderMapper payOrderMapper;
    @Resource
    private ICurrencyAccountService currencyAccountService;

    @Resource
    private IPayTopicService payTopicService;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private IPayProductService payProductService;

    @Override
    public PayOrderPO queryByOrderId(String orderId) {

        PayOrderPO payOrderPOReq = new PayOrderPO();
        payOrderPOReq.setOrderId(orderId);
        PayOrderPO payOrderPO = payOrderMapper.selectOne(payOrderPOReq);
        return payOrderPO;
    }


    @Override
    public String insertOne(PayOrderPO payOrderPO) {
        String orderId = UUID.randomUUID().toString();
        payOrderPO.setOrderId(orderId);
        payOrderMapper.insertOne(payOrderPO);
        return payOrderPO.getOrderId();
    }

    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setId(id);
        payOrderPO.setStatus(status);
        return payOrderMapper.updateById(payOrderPO) > 0;
    }


    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setStatus(status);
        payOrderPO.setOrderId(orderId);
        return payOrderMapper.updateByOrderId(payOrderPO) > 0;
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        // bizCode 与 order 校验
        PayOrderPO payOrderPO = this.queryByOrderId(payOrderDTO.getOrderId());
        if (payOrderPO == null) {
            log.error("[payNotify] payOrderPO is null, create a payOrderPO, userId is {}", payOrderDTO.getUserId());
            currencyAccountService.insertOne(payOrderDTO.getUserId());
            payOrderPO = this.queryByOrderId(payOrderDTO.getOrderId());
        }
        PayTopicPO payTopicPO = payTopicService.getByCode(payOrderDTO.getBizCode());
        if (payTopicPO == null || StringUtils.isEmpty(payTopicPO.getTopic())) {
            log.error("[PayOrderServiceImpl] error payTopicPO, payTopicPO is {}", payOrderDTO);
            return false;
        }
        // 调用bank层相应的一些操作
        payNotifyHandler(payOrderPO);

        // 支付成功后：根据bizCode发送mq 异步通知对应的 服务
        eventPublisher.publish(payTopicPO.getTopic(), JSON.toJSONString(payOrderPO));
        return true;
    }

    /**
     * 在bank层处理一些操作：
     * 如 判断充值商品类型，去做对应的商品记录（如：购买虚拟币，进行余额增加+流水记录）
     */
    private void payNotifyHandler(PayOrderPO payOrderPO) {
        // 更新订单状态为已支付
        this.updateOrderStatus(payOrderPO.getOrderId(), OrderStatusEnum.PAYED.getCode());
        Integer productId = payOrderPO.getProductId();
        PayProductDTO payProductDTO = payProductService.getByProductId(productId);
        if (payProductDTO != null && payProductDTO.getType().equals(PayProductTypeEnum.YUBAO_COIN.getCode())) {
            // 类型是充值虚拟币业务：
            Long userId = payOrderPO.getUserId();
            JSONObject jsonObject = JSON.parseObject(payProductDTO.getExtra());
            Integer coinNum = jsonObject.getInteger("coin");
            currencyAccountService.incr(userId, coinNum);
        }
    }

}
