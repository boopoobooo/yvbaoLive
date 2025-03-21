package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.service.IPayNotifyService;
import com.alipay.api.internal.util.AlipaySignature;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 10:20  支付回调
 * @Description:
 */
@RestController
@RequestMapping("/payNotify")
@Slf4j
public class PayNotifyController {
    @Resource
    private IPayNotifyService payNotifyService;

    /**
     * 处理支付宝异步通知的接口（注意这里必须是POST接口）
     *
     * @param request HTTP请求对象，包含支付宝返回的通知信息
     * @return 返回给支付宝的确认信息
     */
    @PostMapping("/notify")
    @ResponseBody
    public String payNotify(HttpServletRequest request) {

        log.info("====================payNotify====================");
        // 获取支付宝返回的各个参数
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        requestParameterMap.forEach((key, value) -> log.info(key + " = " + Arrays.toString(value)));
        log.info("====================payNotify====================");

        if (!request.getParameter("trade_status").equals("TRADE_SUCCESS")){
            return "false";
        }
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }

        boolean status = payNotifyService.notifyHandler(params);
        log.info("[payNotify] 支付回调结果:{}",status);

        // 告诉支付宝，已经成功收到异步通知
        return status? "true" : "false";
    }


}
