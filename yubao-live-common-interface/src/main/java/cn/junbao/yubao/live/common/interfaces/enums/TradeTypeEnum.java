package cn.junbao.yubao.live.common.interfaces.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeTypeEnum {
    SEND_GIFT_TRADE(5556,"送礼物交易类型");
    private Integer code ;
    private String msg;
}
