package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.IBankService;
import cn.junbao.yubao.live.api.vo.req.PayProductReqVO;
import cn.junbao.yubao.live.api.vo.resp.PayProductItemVO;
import cn.junbao.yubao.live.api.vo.resp.PayProductRespVO;
import cn.junbao.yubao.live.api.vo.resp.PayProductVO;
import cn.junbao.yubao.live.bank.constants.OrderStatusEnum;
import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.dto.PayProductDTO;
import cn.junbao.yubao.live.bank.interfaces.ICurrencyAccountRpc;
import cn.junbao.yubao.live.bank.interfaces.IPayOrderRpc;
import cn.junbao.yubao.live.bank.interfaces.IPayProductRpc;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/3/10 20:33
 * @Description:
 */
@Service
@Slf4j
public class BankServiceImpl implements IBankService {


    @DubboReference(check = false)
    private IPayProductRpc payProductRpc;
    @DubboReference(check = false)
    private ICurrencyAccountRpc currencyAccountRpc;
    @DubboReference(check = false)
    private IPayOrderRpc payOrderRpc;


    @Override
    public PayProductVO products(Integer type) {
        //查询产品列表
        List<PayProductDTO> payProductDTOS = payProductRpc.products(type);
        List<PayProductItemVO> payProductItemVOS = new ArrayList<>();
        log.info("[products]  payProductItemVOS size = {}",payProductItemVOS.size());
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

    @Override
    public PayProductRespVO payProduct(PayProductReqVO payProductReqVO) {
        // 参数校验

        if ( payProductReqVO.getProductId() == null || payProductReqVO.getPaySource() == null){
            log.warn("[payProduct]参数错误,payProductReqVO:{}", payProductReqVO);
            return null;
        }
        // 查询payProductDTO
        PayProductDTO payProductDTO = payProductRpc.getByProductId(payProductReqVO.getProductId());
        if (payProductDTO == null){
            log.warn("[payProduct] payProductDTO is null");
            return null;
        }

        // 生成一条订单（待支付状态）
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setProductId(payProductReqVO.getProductId());
        payOrderDTO.setUserId(WebRequestContext.getUserId());
        payOrderDTO.setPayTime(new Date());
        payOrderDTO.setSource(payProductReqVO.getPaySource());
        payOrderDTO.setPayChannel(payProductReqVO.getPayChannel());
        String orderId = payOrderRpc.insertOne(payOrderDTO);

        //调用支付....todo 对接支付宝沙箱

        // 模拟点击 去支付 按钮，更新订单状态为 支付中
        payOrderRpc.updateOrderStatus(orderId, OrderStatusEnum.PAYING.getCode());
        PayProductRespVO payProductRespVO = new PayProductRespVO();
        payProductRespVO.setOrderId(orderId);
        return payProductRespVO;

    }
}
