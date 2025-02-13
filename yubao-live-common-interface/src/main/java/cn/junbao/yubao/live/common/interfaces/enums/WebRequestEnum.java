package cn.junbao.yubao.live.common.interfaces.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebRequestEnum {
    COOKIE_TOKEN_NAME("yubao_token","cookie中校验Tokend"),
    WEB_REQUEST_USER_ID("user_id","线程本地变量中的用户id");


    final String name;
    final String desc;
}
