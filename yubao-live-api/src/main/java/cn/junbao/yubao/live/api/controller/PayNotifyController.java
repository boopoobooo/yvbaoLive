package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.service.IPayNotifyService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 10:20  支付回调
 * @Description:
 */
@RestController
@RequestMapping("/payNotify")
public class PayNotifyController {
    @Resource
    private IPayNotifyService payNotifyService;

    @PostMapping("/wxNotify")
    public String wxNotify(@RequestParam("param") String param) {
        return payNotifyService.notifyHandler(param);
    }
}
