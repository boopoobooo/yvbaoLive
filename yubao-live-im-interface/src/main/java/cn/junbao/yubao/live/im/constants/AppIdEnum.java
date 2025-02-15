package cn.junbao.yubao.live.im.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppIdEnum {

    YUBAO_LIVE_BIZ(1001,"鱼宝直播相关业务");
    private int appId;
    private String desc;
}
