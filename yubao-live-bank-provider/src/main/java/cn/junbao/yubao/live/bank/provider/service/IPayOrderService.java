package cn.junbao.yubao.live.bank.provider.service;

import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.provider.dao.po.PayOrderPO;
import com.alipay.api.AlipayApiException;

import java.util.List;
import java.util.Map;

public interface IPayOrderService {
    /**
     * 根据orderId查询订单信息
     */
    PayOrderPO queryByOrderId(Long orderId);

    /**
     *插入订单 ，返回主键id
     */
    Long insertOne(PayOrderPO payOrderPO);


    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long orderId, Integer status);

    /**
     * 支付回调请求的接口
     */
    boolean payNotify(PayOrderDTO payOrderDTO);

    String doPrepayOrder(PayOrderDTO payOrderDTO);

    boolean checkSignature(Map<String, String> params) ;

    /**
     * 查询没有支付的订单
     * @return
     */
    List<Long> queryNoPayNotifyOrder();

    List<Long> queryTimeoutCloseOrderList();

}
