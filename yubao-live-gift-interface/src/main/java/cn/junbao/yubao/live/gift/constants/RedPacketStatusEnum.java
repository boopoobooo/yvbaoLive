package cn.junbao.yubao.live.gift.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Junbao
 * @Date: 2025/3/18 17:20
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum RedPacketStatusEnum {
    WAIT(1,"待准备"),
    IS_PREPARED(2, "已准备"),
    IS_SEND(3, "已发送");

    int code;
    String desc;

}
