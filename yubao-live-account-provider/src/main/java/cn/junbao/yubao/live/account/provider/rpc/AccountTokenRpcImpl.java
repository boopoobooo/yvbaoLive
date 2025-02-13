package cn.junbao.yubao.live.account.provider.rpc;

import cn.junbao.yubao.live.account.interfaces.rpc.IAccountTokenRpc;
import cn.junbao.yubao.live.account.provider.service.IAccountTokenService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class AccountTokenRpcImpl implements IAccountTokenRpc {

    @Resource
    private IAccountTokenService accountTokenService;


    @Override
    public String createAndSaveToken(Long userId) {
        return accountTokenService.createAndSaveToken(userId);
    }

    @Override
    public Long getUserIdByToken(String token) {
        return accountTokenService.getUserIdByToken(token);
    }
}
