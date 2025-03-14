package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.IPayNotifyService;
import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.interfaces.IPayOrderRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

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
    public String notifyHandler(String paramJson) {
        //todo   具体实际的回调 --会有对应的参数
        //payOrderRpc.payNotify();
        return null;
    }
}
