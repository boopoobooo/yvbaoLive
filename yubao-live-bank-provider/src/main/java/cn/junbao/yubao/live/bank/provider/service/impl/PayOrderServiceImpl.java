package cn.junbao.yubao.live.bank.provider.service.impl;

import cn.junbao.yubao.live.bank.constants.OrderStatusEnum;
import cn.junbao.yubao.live.bank.constants.PayProductTypeEnum;
import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.dto.PayProductDTO;
import cn.junbao.yubao.live.bank.provider.config.AliPayConfigProperties;
import cn.junbao.yubao.live.bank.provider.dao.mapper.IPayOrderMapper;
import cn.junbao.yubao.live.bank.provider.dao.po.PayOrderPO;
import cn.junbao.yubao.live.bank.provider.dao.po.PayTopicPO;
import cn.junbao.yubao.live.bank.provider.service.ICurrencyAccountService;
import cn.junbao.yubao.live.bank.provider.service.IPayOrderService;
import cn.junbao.yubao.live.bank.provider.service.IPayProductService;
import cn.junbao.yubao.live.bank.provider.service.IPayTopicService;
import cn.junbao.yubao.live.mq.starter.publisher.EventPublisher;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.alipay.api.AlipayConstants.CHARSET_UTF8;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 09:43
 * @Description:
 */
@Service
@Slf4j
public class PayOrderServiceImpl implements IPayOrderService {
    @Resource
    private IPayOrderMapper payOrderMapper;
    @Resource
    private ICurrencyAccountService currencyAccountService;

    @Resource
    private IPayTopicService payTopicService;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private IPayProductService payProductService;

    @Override
    public PayOrderPO queryByOrderId(Long orderId) {

        PayOrderPO payOrderPOReq = new PayOrderPO();
        payOrderPOReq.setOrderId(orderId);
        PayOrderPO payOrderPO = payOrderMapper.selectOne(payOrderPOReq);
        return payOrderPO;
    }


    @Override
    public Long insertOne(PayOrderPO payOrderPO) {
        Long orderId = ThreadLocalRandom.current().nextLong(1, 10000000000L);
        payOrderPO.setOrderId(orderId);
        payOrderMapper.insertOne(payOrderPO);
        return payOrderPO.getOrderId();
    }


    @Override
    public boolean updateOrderStatus(Long orderId, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setStatus(status);
        payOrderPO.setOrderId(orderId);
        return payOrderMapper.updateByOrderId(payOrderPO) > 0;
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        //1. 找到对应的订单
        PayOrderPO payOrderPO = this.queryByOrderId(payOrderDTO.getOrderId());
        if (payOrderPO == null) {
            log.error("[payNotify] payOrderPO is null,userId is {}", payOrderDTO.getUserId());
            return false;
        }

        //2. 更新订单状态
        // 调用bank层相应的一些操作
        this.payNotifyHandler(payOrderPO);

        return true;
    }

    @Resource
    private AlipayClient alipayClient;

    @Resource
    private AliPayConfigProperties aliPayConfigProperties;

    /**
     * 进行支付宝支付
     * @param payOrderDTO
     */
    @Override
    public String doPrepayOrder(PayOrderDTO payOrderDTO) {

        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        // 设置异步通知地址
        alipayTradePagePayRequest.setNotifyUrl(aliPayConfigProperties.getNotifyUrl());
        // 设置重定向地址
        alipayTradePagePayRequest.setReturnUrl(aliPayConfigProperties.getReturnUrl());

        // 构造业务请求参数（如果是电脑网页支付，product_code是必传参数）
        com.alibaba.fastjson2.JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", payOrderDTO.getOrderId());
        jsonObject.put("subject", payOrderDTO.getProductName());
        jsonObject.put("body", payOrderDTO.getProductDesc());
        jsonObject.put("total_amount", payOrderDTO.getTotalAmount());
        jsonObject.put("product_code", "FAST_INSTANT_TRADE_PAY");

        log.info("[jsonObject]  == {}",jsonObject.toJSONString());
        alipayTradePagePayRequest.setBizContent(jsonObject.toJSONString());

        // 请求支付宝接口
        String alipayForm;
        try {
            alipayForm = alipayClient.pageExecute(alipayTradePagePayRequest).getBody();
            log.info("[doPrepayOrder] alipayForm = {}",alipayForm);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        return alipayForm;
    }

    @Override
    public boolean checkSignature(Map<String, String> params)  {
        String orderId = params.get("out_trade_no");
        String gmtPayment = params.get("gmt_payment");
        String alipayTradeNo = params.get("trade_no");

        String sign = params.get("sign");
        String content = AlipaySignature.getSignCheckContentV1(params);
        boolean checkSignature = false; // 验证签名
        try {
            checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfigProperties.getAlipayPublicKey(), "UTF-8");
        } catch (AlipayApiException e) {
            log.error("[checkSignature] 验证签名异常",e);
            throw new RuntimeException(e);
        }
        return checkSignature;

    }

    @Override
    public List<Long> queryNoPayNotifyOrder() {
        return payOrderMapper.queryNoPayNotifyOrder();
    }

    @Override
    public List<Long> queryTimeoutCloseOrderList() {
        return payOrderMapper.queryTimeoutCloseOrderList();
    }


    /**
     * 在bank层处理一些操作：
     * 如 判断充值商品类型，去做对应的商品记录（如：购买虚拟币，进行余额增加+流水记录）
     */
    private void payNotifyHandler(PayOrderPO payOrderPO) {
        // 更新订单状态为已支付
        this.updateOrderStatus(payOrderPO.getOrderId(), OrderStatusEnum.PAYED.getCode());

        Integer productId = payOrderPO.getProductId();
        PayProductDTO payProductDTO = payProductService.getByProductId(productId);

        if (payProductDTO != null && payProductDTO.getType().equals(PayProductTypeEnum.YUBAO_COIN.getCode())) {
            // 该类型是充值虚拟币业务：
            Long userId = payOrderPO.getUserId();
            JSONObject jsonObject = JSON.parseObject(payProductDTO.getExtra());
            Integer coinNum = jsonObject.getInteger("coin");
            //增加账户
            currencyAccountService.incr(userId, coinNum);
        }
    }

}
