package cn.junbao.yubao.live.account.interfaces.rpc;

public interface IAccountTokenRpc {
    String createAndSaveToken(Long userId);

    Long getUserIdByToken(String token);
}
