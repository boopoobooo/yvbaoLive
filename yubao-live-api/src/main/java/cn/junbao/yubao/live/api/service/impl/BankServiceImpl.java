package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.IBankService;
import cn.junbao.yubao.live.api.vo.resp.PayProductItemVO;
import cn.junbao.yubao.live.api.vo.resp.PayProductVO;
import cn.junbao.yubao.live.bank.dto.PayProductDTO;
import cn.junbao.yubao.live.bank.interfaces.ICurrencyAccountRpc;
import cn.junbao.yubao.live.bank.interfaces.IPayProductRpc;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/3/10 20:33
 * @Description:
 */
@Service
public class BankServiceImpl implements IBankService {


    @DubboReference(check = false)
    private IPayProductRpc payProductRpc;
    @DubboReference(check = false)
    private ICurrencyAccountRpc currencyAccountRpc;
    @Override
    public PayProductVO products(Integer type) {
        //查询产品列表
        List<PayProductDTO> payProductDTOS = payProductRpc.products(type);
        List<PayProductItemVO> payProductItemVOS = new ArrayList<>();
        for (PayProductDTO payProductDTO : payProductDTOS) {
            PayProductItemVO payProductItemVO = new PayProductItemVO();
            payProductItemVO.setId(payProductDTO.getId());
            payProductItemVO.setName(payProductDTO.getName());
            payProductItemVO.setCoinNum(JSON.parseObject(payProductDTO.getExtra()).getInteger("coin"));
            payProductItemVOS.add(payProductItemVO);
        }
        PayProductVO payProductVO = new PayProductVO();
        Long userId = WebRequestContext.getUserId();
        payProductVO.setCurrentBalance(currencyAccountRpc.getBalance(userId));
        payProductVO.setPayProductItemVOList(payProductItemVOS);
        return payProductVO;
    }
}
