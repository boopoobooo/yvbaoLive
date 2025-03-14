package cn.junbao.yubao.live.bank.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 10:07   订单状态
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum OrderStatusEnum {
    WAITING_PAY(0, "待支付"),
    PAYING(1, "支付中"),
    PAYED(2, "已支付"),
    PAY_BACK(3, "撤销"),
    IN_VALID(4, "无效");

    int code;
    String desc;

}
