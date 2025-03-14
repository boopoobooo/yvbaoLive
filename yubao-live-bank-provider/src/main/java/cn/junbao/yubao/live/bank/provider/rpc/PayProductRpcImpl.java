package cn.junbao.yubao.live.bank.provider.rpc;

import cn.junbao.yubao.live.bank.dto.PayProductDTO;
import cn.junbao.yubao.live.bank.interfaces.IPayProductRpc;
import cn.junbao.yubao.live.bank.provider.service.IPayProductService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/3/10 20:18
 * @Description:
 */
@DubboService
public class PayProductRpcImpl implements IPayProductRpc {


    @Resource
    private IPayProductService payProductService;

    @Override
    public List<PayProductDTO> products(Integer type) {
        return payProductService.products(type);
    }

    @Override
    public PayProductDTO getByProductId(Integer productId) {
        return payProductService.getByProductId(productId);
    }
}
