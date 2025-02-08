package cn.junbao.yubao.live.msg.provider.rpc;

import cn.junbao.yubao.live.msg.dto.MsgCheckDTO;
import cn.junbao.yubao.live.msg.enums.MsgSendResultEnum;
import cn.junbao.yubao.live.msg.interfaces.ISmsRpc;
import cn.junbao.yubao.live.msg.provider.service.ISmsService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class SmsRpcImpl implements ISmsRpc {
    @Resource
    private ISmsService smsService;

    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        return smsService.sendLoginCode(phone);
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, String  code) {
        return smsService.checkLoginCode(phone,code);
    }
}
