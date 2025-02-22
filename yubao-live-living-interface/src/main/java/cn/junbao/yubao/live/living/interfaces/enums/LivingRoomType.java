package cn.junbao.yubao.live.living.interfaces.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LivingRoomType {
    DEFAULT_LIVING_ROOM_TYPE(1101,"默认直播间类型");

    private Integer code;
    private String desc;
}
