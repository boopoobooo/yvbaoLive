package cn.junbao.yubao.live.im.router.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImMsgBizCodeEnum {
    LIVING_ROOM_IM_BIZ("5555","直播间内聊天消息"),
    LIVING_ROOM_SEND_GIFT_SUCCESS("5556", "送礼成功"),
    LIVING_ROOM_SEND_GIFT_FAIL("5557", "送礼失败");
    final String  code;
    final String desc;
}
