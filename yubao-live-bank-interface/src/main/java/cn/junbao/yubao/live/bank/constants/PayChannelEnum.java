package cn.junbao.yubao.live.bank.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 10:07   支付渠道
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum PayChannelEnum {

    ZHI_FU_BAO(0, "支付宝"),
    WEI_XIN(1, "微信"),
    YIN_LIAN(2, "银联"),
    SHOU_YIN_TAI(3, "收银台");
    private int code;
    private String desc;

}
