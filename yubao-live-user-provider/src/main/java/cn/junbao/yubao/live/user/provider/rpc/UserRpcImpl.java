package cn.junbao.yubao.live.user.provider.rpc;

import cn.junbao.yubao.live.user.dto.UserDTO;
import cn.junbao.yubao.live.user.interfaces.IUserRpc;
import cn.junbao.yubao.live.user.provider.service.IUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class UserRpcImpl implements IUserRpc {
    Logger logger = LoggerFactory.getLogger(UserRpcImpl.class);

    private final IUserService userService;

    public UserRpcImpl(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public String sayHello(String name) {
        return "Hello =============";
    }

    @Override
    public UserDTO getUserById(Long userId) {
        logger.info("impl getuserByid 开始");
        return userService.getUserById(userId);
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        if (userIdList.isEmpty()) return new HashMap<>();
        return userService.batchQueryUserInfo(userIdList);
    }
}
