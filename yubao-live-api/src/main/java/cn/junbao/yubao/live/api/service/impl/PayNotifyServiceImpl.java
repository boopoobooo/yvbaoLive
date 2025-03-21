package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.IPayNotifyService;
import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.interfaces.IPayOrderRpc;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 10:21
 * @Description:
 */
@Service
public class PayNotifyServiceImpl implements IPayNotifyService {
    @DubboReference
    private IPayOrderRpc payOrderRpc;

    @Override
    public boolean notifyHandler(Map<String, String> params) {

        //1. 支付宝验签
        boolean checkSignatureStatus =  payOrderRpc.checkSignature(params);
        if (!checkSignatureStatus){
            return false;
        }

        PayOrderDTO payOrderDTO =new PayOrderDTO();
        payOrderDTO.setUserId(WebRequestContext.getUserId());
        payOrderDTO.setOrderId(Long.valueOf(params.get("out_trade_no")));
        //2. 支付回调相关处理
        payOrderRpc.payNotify(payOrderDTO);

        return true;
    }
}
