package cn.junbao.yubao.live.framework.web.strater.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizBaseErrorEnum {

    PARAM_ERROR(100001,"参数异常"),
    TOKEN_ERROR(100002,"用户token异常");

    private final int errorCode;
    private final String errorMsg;
}
