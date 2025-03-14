package cn.junbao.yubao.live.bank.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Junbao
 * @Date: 2025/3/10 20:16
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum PayProductTypeEnum {
    YUBAO_COIN(0, "直播间充值-鱼宝虚拟币产品");

    int code;
    String desc;


}
