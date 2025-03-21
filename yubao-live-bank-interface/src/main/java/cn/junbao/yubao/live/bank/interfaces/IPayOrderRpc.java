package cn.junbao.yubao.live.bank.interfaces;

import cn.junbao.yubao.live.bank.dto.PayOrderDTO;

import java.util.Map;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 09:42
 * @Description:
 */
public interface IPayOrderRpc {
    /**
     *插入订单 ，返回orderId
     */
    Long insertOne(PayOrderDTO payOrderDTO);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long orderId, Integer status);

    /**
     * 产品支付
     */
     String doPrepayOrder(PayOrderDTO payOrderDTO);


    /**
     * 支付回调请求的接口
     */
    boolean payNotify(PayOrderDTO payOrderDTO);

    boolean checkSignature(Map<String, String> params);
}
