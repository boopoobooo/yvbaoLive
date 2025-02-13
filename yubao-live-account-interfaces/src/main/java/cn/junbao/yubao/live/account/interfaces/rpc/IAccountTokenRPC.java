package cn.junbao.yubao.live.account.interfaces.rpc;

public interface IAccountTokenRPC {
    String createAndSaveToken(Long userId);

    Long getUserIdByToken(String token);
}
