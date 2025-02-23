package cn.junbao.yubao.live.living.interfaces.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LivingRoomTypeEnum {
    DEFAULT_LIVING_ROOM(1101,"默认直播间类型"),
    PK_LIVING_ROOM(1102,"pk直播间类型");

    private Integer code;
    private String desc;
}
