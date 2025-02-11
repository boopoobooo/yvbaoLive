package cn.junbao.yubao.live.user.provider.rpc;

import cn.junbao.yubao.live.user.dto.UserLoginDTO;
import cn.junbao.yubao.live.user.dto.UserPhoneDTO;
import cn.junbao.yubao.live.user.interfaces.IUserPhoneRpc;
import cn.junbao.yubao.live.user.provider.service.IUserPhoneService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
public class UserPhoneRpcImpl implements IUserPhoneRpc {

    @Resource
    private IUserPhoneService userPhoneService;
    @Override
    public UserLoginDTO login(String phone) {
        return userPhoneService.login(phone);
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        return userPhoneService.queryByUserId(userId);
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        return userPhoneService.queryByPhone(phone);
    }
}
