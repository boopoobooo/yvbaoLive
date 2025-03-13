package cn.junbao.yubao.live.bank.enums;

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
    QIYU_COIN(0, "直播间充值-旗鱼虚拟币产品");

    int code;
    String desc;


}
