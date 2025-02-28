package cn.junbao.yubao.live.user.provider.rpc;

import cn.junbao.yubao.live.user.interfaces.IUserAccountRpc;
import cn.junbao.yubao.live.user.provider.service.IUserAccountTokenService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Junbao
 * @Date: 2025/2/26 17:24
 * @Description:
 */
@DubboService
@Slf4j
public class UserAccountRpcImpl implements IUserAccountRpc {
    @Resource
    private IUserAccountTokenService accountTokenService;


    @Override
    public String createAndSaveToken(Long userId) {
        return accountTokenService.createAndSaveToken(userId);
    }

    @Override
    public Long getUserIdByToken(String token) {
        return accountTokenService.getUserIdByToken(token);
    }
}
