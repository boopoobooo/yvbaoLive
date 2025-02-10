package cn.junbao.yubao.live.user.provider.rpc;

import cn.junbao.yubao.live.user.dto.UserLoginDTO;
import cn.junbao.yubao.live.user.dto.UserPhoneDTO;
import cn.junbao.yubao.live.user.interfaces.IUserPhoneRpc;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
public class UserPhoneRpcImpl implements IUserPhoneRpc {
    @Override
    public UserLoginDTO login(String phone) {
        return null;
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        return null;
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        return null;
    }
}
