package cn.junbao.yubao.live.im.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
@AllArgsConstructor
public enum ImMsgTypeCode {
    IM_LOGIN_MSG(1001,"IM系统登录消息类型"),
    IM_LOGOUT_MSG(1002,"IM系统登出消息类型"),
    IM_BIZ_MSG(1003,"IM系统常规业务消息类型"),
    IM_HEARTBEAT_MSG(1004,"IM系统心跳消息类型");
    final int code;
    final String desc;
}
