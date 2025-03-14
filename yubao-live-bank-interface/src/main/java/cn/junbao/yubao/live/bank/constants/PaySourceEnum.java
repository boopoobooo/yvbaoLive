package cn.junbao.yubao.live.bank.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 10:06   支付场景枚举
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum PaySourceEnum {
    YUBAO_LIVING_ROOM(1, "鱼宝直播间内支付"),
    YUBAO_USER_CENTER(2, "用户中心内支付");

    private int code;
    private String desc;

}
