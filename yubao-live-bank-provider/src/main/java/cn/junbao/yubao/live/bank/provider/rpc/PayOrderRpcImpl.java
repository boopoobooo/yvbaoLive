package cn.junbao.yubao.live.bank.provider.rpc;

import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.interfaces.IPayOrderRpc;
import cn.junbao.yubao.live.bank.provider.dao.po.PayOrderPO;
import cn.junbao.yubao.live.bank.provider.service.IPayOrderService;
import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 09:42
 * @Description:
 */

@DubboService
public class PayOrderRpcImpl implements IPayOrderRpc {
    @Resource
    private IPayOrderService payOrderService;

    @Override
    public String insertOne(PayOrderDTO payOrderDTO) {
        return payOrderService.insertOne(ConvertBeanUtils.convert(payOrderDTO, PayOrderPO.class));
    }

    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        return payOrderService.updateOrderStatus(id, status);
    }

    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        return payOrderService.updateOrderStatus(orderId, status);
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        return payOrderService.payNotify(payOrderDTO);
    }
}
