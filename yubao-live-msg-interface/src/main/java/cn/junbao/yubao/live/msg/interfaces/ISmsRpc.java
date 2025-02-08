package cn.junbao.yubao.live.msg.interfaces;

import cn.junbao.yubao.live.msg.dto.MsgCheckDTO;
import cn.junbao.yubao.live.msg.enums.MsgSendResultEnum;

public interface ISmsRpc {

    /**
     * 发送短信登录验证码接口
     * @param phone
     * @return
     */
    MsgSendResultEnum sendLoginCode(String phone);

    /**
     * 校验登录验证码
     * @param phone
     * @param code
     * @return
     */
    MsgCheckDTO checkLoginCode(String phone, String  code);
}
