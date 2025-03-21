package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.error.ApiErrorEnum;
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
import cn.junbao.yubao.live.framework.web.strater.exception.YuBaoErrorException;
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
        // 1.参数校验
        if ( payProductReqVO.getProductId() == null || payProductReqVO.getPaySource() == null){
            log.warn("[payProduct]参数错误,payProductReqVO:{}", payProductReqVO);
            throw new YuBaoErrorException(ApiErrorEnum.PARAM_ERROR.getCode(),ApiErrorEnum.PARAM_ERROR.getMsg());
        }

        // 2. 查询payProductDTO
        PayProductDTO payProductDTO = payProductRpc.getByProductId(payProductReqVO.getProductId());
        if (payProductDTO == null){
            log.warn("[payProduct] payProductDTO is null");
            throw new YuBaoErrorException(ApiErrorEnum.PARAM_ERROR.getCode(),ApiErrorEnum.PARAM_ERROR.getMsg());
        }

        //3. 创建订单 生成一条订单（待支付状态）
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setProductId(payProductReqVO.getProductId());
        payOrderDTO.setProductName(payProductDTO.getName());
        payOrderDTO.setProductDesc(payProductDTO.getName());
        payOrderDTO.setTotalAmount(payProductDTO.getPrice());
        payOrderDTO.setUserId(WebRequestContext.getUserId());
        payOrderDTO.setPayTime(new Date());
        payOrderDTO.setSource(payProductReqVO.getPaySource());
        payOrderDTO.setPayChannel(payProductReqVO.getPayChannel());
        payOrderDTO.setStatus(OrderStatusEnum.WAITING_PAY.getCode());
        Long orderId = payOrderRpc.insertOne(payOrderDTO);
        payOrderDTO.setOrderId(orderId);

        //4. 调用alipay接口  赋值dto中的payurl
        String payUrl = payOrderRpc.doPrepayOrder(payOrderDTO);
        log.info("[payProduct]支付宝沙箱响应 payurl = {}",payUrl);

        //5. 更新订单状态为支付中
        payOrderRpc.updateOrderStatus(orderId, OrderStatusEnum.PAYING.getCode());
        PayProductRespVO payProductRespVO = new PayProductRespVO();
        payProductRespVO.setOrderId(orderId);
        payProductRespVO.setPayUrl(payUrl);
        return payProductRespVO;

    }
}
