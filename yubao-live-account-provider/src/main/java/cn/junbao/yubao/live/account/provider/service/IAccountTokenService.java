package cn.junbao.yubao.live.account.provider.service;

public interface IAccountTokenService {

    String createAndSaveToken(Long userId);

    Long getUserIdByToken(String token);
}
