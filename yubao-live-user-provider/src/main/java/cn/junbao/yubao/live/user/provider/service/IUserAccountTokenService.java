package cn.junbao.yubao.live.user.provider.service;

public interface IUserAccountTokenService {

    String createAndSaveToken(Long userId);

    Long getUserIdByToken(String token);
}
