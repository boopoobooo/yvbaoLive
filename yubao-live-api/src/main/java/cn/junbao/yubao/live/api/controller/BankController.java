package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.error.ApiErrorEnum;
import cn.junbao.yubao.live.api.service.IBankService;
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

    @PostMapping("/products")
    public WebResponseVO products(Integer type) {
        log.info("/products,请求参数为 type = {}",type);
        if (type == null ){
            return WebResponseVO.bizError(ApiErrorEnum.PARAM_ERROR.getCode(),ApiErrorEnum.PARAM_ERROR.getMsg());
        }

        return WebResponseVO.success(bankService.products(type));
    }

}
