package cn.junbao.yubao.live.bank.provider.rpc;

import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.interfaces.IPayOrderRpc;
import cn.junbao.yubao.live.bank.provider.dao.po.PayOrderPO;
import cn.junbao.yubao.live.bank.provider.service.IPayOrderService;
import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.Map;

import static com.alipay.api.AlipayConstants.CHARSET_UTF8;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 09:42
 * @Description:
 */

@DubboService
@Slf4j
public class PayOrderRpcImpl implements IPayOrderRpc {
    @Resource
    private IPayOrderService payOrderService;

    @Override
    public Long insertOne(PayOrderDTO payOrderDTO) {
        return payOrderService.insertOne(ConvertBeanUtils.convert(payOrderDTO, PayOrderPO.class));
    }


    @Override
    public boolean updateOrderStatus(Long orderId, Integer status) {
        return payOrderService.updateOrderStatus(orderId, status);
    }

    @Override
    public String  doPrepayOrder(PayOrderDTO payOrderDTO) {
       return payOrderService.doPrepayOrder(payOrderDTO);
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        return payOrderService.payNotify(payOrderDTO);
    }

    @Override
    public boolean checkSignature(Map<String, String> params) {
        return payOrderService.checkSignature(params);
    }
}
