package cn.junbao.yubao.live.bank.provider.service;

import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.provider.dao.po.PayOrderPO;

public interface IPayOrderService {
    /**
     * 根据orderId查询订单信息
     */
    PayOrderPO queryByOrderId(String orderId);

    /**
     *插入订单 ，返回主键id
     */
    String insertOne(PayOrderPO payOrderPO);

    /**
     * 根据主键id更新订单状态
     */
    boolean updateOrderStatus(Long id, Integer status);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(String orderId, Integer status);

    /**
     * 支付回调请求的接口
     */
    boolean payNotify(PayOrderDTO payOrderDTO);
}
