package cn.junbao.yubao.live.msg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImMsgBizCodeEnum {
    LIVING_ROOM_IM_BIZ("5555","直播间内聊天消息");
    final String  code;
    final String desc;
}
