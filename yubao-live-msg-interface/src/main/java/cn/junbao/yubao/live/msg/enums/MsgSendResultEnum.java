package cn.junbao.yubao.live.msg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public enum MsgSendResultEnum implements Serializable {
    SEND_FAIL(0,"发送失败"),
    SEND_SUCCESS (1,"发送成功"),
    MSG_PARAM_ERROR(2,"消息参数异常");

    final int code;
    final String desc;
}
