package cn.junbao.yubao.live.api.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorEnum {
    PARAM_ERROR(10001,"参数错误"),
    PK_ONLINE_BUSY(10007, "目前正有人连线，请稍后再试");
    private Integer code;
    private String msg;
}
