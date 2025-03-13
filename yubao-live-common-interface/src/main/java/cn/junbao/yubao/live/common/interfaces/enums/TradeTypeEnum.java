package cn.junbao.yubao.live.common.interfaces.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeTypeEnum {
    SEND_GIFT_TRADE(0,"送礼物交易"),
    LIVING_RECHARGE(1,"直播间充值");
    private Integer code ;
    private String msg;
}
