package cn.junbao.yubao.live.api.vo;

import lombok.Data;

/**
 * @Author: Junbao
 * @Date: 2025/3/21 00:17
 * @Description:
 */
@Data
public class AliPay {
    private String traceNo; // 我们自己生成的订单编号
    private double totalAmount;// 订单的总金额
    private String subject; // 支付的名称
    private String alipayTraceNo;


}
