package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.error.ApiErrorEnum;
import cn.junbao.yubao.live.api.service.IBankService;
import cn.junbao.yubao.live.api.vo.req.PayProductReqVO;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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


    @PostMapping("/payProduct")
    public WebResponseVO payProduct(PayProductReqVO payProductReqVO) {
        return WebResponseVO.success(bankService.payProduct(payProductReqVO));
    }

}
