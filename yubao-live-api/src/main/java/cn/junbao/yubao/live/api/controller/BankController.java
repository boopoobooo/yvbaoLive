package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.error.ApiErrorEnum;
import cn.junbao.yubao.live.api.service.IBankService;
import cn.junbao.yubao.live.api.vo.req.PayProductReqVO;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Junbao
 * @Date: 2025/3/10 20:27
 * @Description:
 */
@RestController
@RequestMapping("/bank")
@Slf4j
public class BankController {
    @Resource
    private IBankService bankService;

    /**
     * 获取充值商品列表
     * @param type  商品类型
     */
    @PostMapping("/products")
    public WebResponseVO products(Integer type) {
        log.info("/products,请求参数为 type = {}",type);
        if (type == null ){
            return WebResponseVO.bizError(ApiErrorEnum.PARAM_ERROR.getCode(),ApiErrorEnum.PARAM_ERROR.getMsg());
        }

        return WebResponseVO.success(bankService.products(type));
    }


    // 1.申请调用第三方支付接口（签名-》支付宝/微信）（生成一条支付中状态的订单）
    // 2.生成一个（特定的支付页）二维码（输入账户密码，支付）（第三方平台完成）
    // 3.发送回调请求-》业务方
    // 要求（可以接收不同平台的回调数据）
    // 可以根据业务标识去回调不同的业务服务（自定义参数组成中，塞入一个业务code,根据业务code去回调不同的业务服务）
    @PostMapping("/payProduct")
    public WebResponseVO payProduct(PayProductReqVO payProductReqVO) {
        return WebResponseVO.success(bankService.payProduct(payProductReqVO));
    }

}
