package cn.junbao.yubao.live.user.interfaces;

public interface IUserAccountRpc {
    String createAndSaveToken(Long userId);

    Long getUserIdByToken(String token);
}
