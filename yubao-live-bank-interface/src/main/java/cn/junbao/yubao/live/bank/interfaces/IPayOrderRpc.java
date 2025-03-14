package cn.junbao.yubao.live.bank.interfaces;

import cn.junbao.yubao.live.bank.dto.PayOrderDTO;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 09:42
 * @Description:
 */
public interface IPayOrderRpc {
    /**
     *插入订单 ，返回orderId
     */
    String insertOne(PayOrderDTO payOrderDTO);

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
