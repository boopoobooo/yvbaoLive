package cn.junbao.yubao.live.im.provider.service;

public interface ImTokenService {
    /**
     * 创建用户登录im服务的token
     * @param userId
     * @param appId
     * @return
     */
    String createImLoginToken(long userId, int appId);

    /**
     * 根据token检索用户id
     * @param token
     * @return
     */
    Long getUserIdByToken(String token);
}
