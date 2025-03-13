package cn.junbao.yubao.live.api.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorEnum {
    PARAM_ERROR(10001,"参数错误");
    private Integer code;
    private String msg;
}
